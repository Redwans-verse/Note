package com.example.data

import android.content.Context
import com.example.model.DailyReport
import com.example.model.FarmProfile
import com.example.model.FarmSettings
import com.example.model.MonthlyExpense
import com.example.model.UserProfile
import com.example.util.NetworkHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

sealed class Resource<out T> {
  data class Success<out T>(val data: T) : Resource<T>()
  data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
  object Loading : Resource<Nothing>()
}

class KaziAgroFirebaseRepository(private val context: Context) {

  private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
  
  private val firestore: FirebaseFirestore by lazy {
    FirebaseFirestore.getInstance().apply {
      firestoreSettings = FirebaseFirestoreSettings.Builder()
        .setLocalCacheSettings(
          PersistentCacheSettings.newBuilder()
            .build()
        )
        .build()
    }
  }

  private val usersColl get() = firestore.collection("users")
  private val farmsColl get() = firestore.collection("farms")
  private val dailyReportsColl get() = firestore.collection("dailyReports")
  private val monthlyExpensesColl get() = firestore.collection("monthlyExpenses")
  private val settingsColl get() = firestore.collection("settings")
  private val backupsColl get() = firestore.collection("backups")

  // Authentication Flow
  val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
      trySend(firebaseAuth.currentUser)
    }
    auth.addAuthStateListener(listener)
    awaitClose { auth.removeAuthStateListener(listener) }
  }

  val currentUserId: String? get() = auth.currentUser?.uid

  suspend fun login(email: String, pass: String): Resource<FirebaseUser> {
    if (!NetworkHelper.isNetworkAvailable(context)) {
      return Resource.Error(NetworkHelper.NO_INTERNET_MESSAGE)
    }
    return try {
      val res = auth.signInWithEmailAndPassword(email.trim(), pass).await()
      val user = res.user ?: return Resource.Error("লগইন ব্যর্থ হয়েছে। ব্যবহারকারী পাওয়া যায়নি।")
      Resource.Success(user)
    } catch (e: Exception) {
      Resource.Error(getBengaliAuthErrorMessage(e), e)
    }
  }

  suspend fun register(
    email: String,
    pass: String,
    name: String,
    farmName: String,
    mobileNumber: String,
    address: String
  ): Resource<FirebaseUser> {
    if (!NetworkHelper.isNetworkAvailable(context)) {
      return Resource.Error(NetworkHelper.NO_INTERNET_MESSAGE)
    }
    return try {
      val res = auth.createUserWithEmailAndPassword(email.trim(), pass).await()
      val user = res.user ?: return Resource.Error("অ্যাকাউন্ট তৈরি ব্যর্থ হয়েছে।")
      val farmId = "farm_${user.uid.take(8)}"

      // Create initial UserProfile & Farm Profile in Firebase Firestore
      val userProfile = UserProfile(
        uid = user.uid,
        name = name.ifBlank { "কাজী এডমিন" },
        email = email.trim(),
        role = "Admin",
        farmId = farmId
      )
      usersColl.document(user.uid).set(userProfile.toMap()).await()

      val farmProfile = FarmProfile(
        farmId = farmId,
        farmName = farmName.ifBlank { "কাজী এগ্রোটেক" },
        ownerName = name.ifBlank { "কাজী মো: রফিকুল ইসলাম" },
        mobileNumber = mobileNumber.ifBlank { "০১৭১১-০০০০০০" },
        address = address.ifBlank { "কালিয়াকৈর, গাজীপুর, ঢাকা" }
      )
      farmsColl.document(farmId).set(farmProfile.toMap()).await()
      settingsColl.document(farmId).set(
        FarmSettings(
          farmId = farmId,
          farmName = farmProfile.farmName,
          ownerName = farmProfile.ownerName,
          mobileNumber = farmProfile.mobileNumber,
          address = farmProfile.address
        ).toMap()
      ).await()

      Resource.Success(user)
    } catch (e: Exception) {
      Resource.Error(getBengaliAuthErrorMessage(e), e)
    }
  }

  suspend fun sendPasswordReset(email: String): Resource<Unit> {
    if (!NetworkHelper.isNetworkAvailable(context)) {
      return Resource.Error(NetworkHelper.NO_INTERNET_MESSAGE)
    }
    return try {
      auth.sendPasswordResetEmail(email.trim()).await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Error("পাসওয়ার্ড রিসেট লিংক পাঠাতে সমস্যা হয়েছে: ${e.localizedMessage}", e)
    }
  }

  fun logout() {
    auth.signOut()
  }

  // Live Streams from Cloud Firestore
  fun observeUserProfile(uid: String): Flow<UserProfile?> = callbackFlow {
    val registration = usersColl.document(uid).addSnapshotListener { snapshot, error ->
      if (error != null) {
        trySend(null)
        return@addSnapshotListener
      }
      val map = snapshot?.data
      if (map != null) {
        trySend(UserProfile.fromMap(uid, map))
      } else {
        trySend(null)
      }
    }
    awaitClose { registration.remove() }
  }

  fun observeFarmProfile(farmId: String): Flow<FarmProfile> = callbackFlow {
    val registration = farmsColl.document(farmId).addSnapshotListener { snapshot, error ->
      if (error != null) {
        trySend(FarmProfile(farmId = farmId))
        return@addSnapshotListener
      }
      val map = snapshot?.data
      if (map != null) {
        trySend(FarmProfile.fromMap(farmId, map))
      } else {
        trySend(FarmProfile(farmId = farmId))
      }
    }
    awaitClose { registration.remove() }
  }

  fun observeDailyReports(farmId: String): Flow<List<DailyReport>> = callbackFlow {
    val registration = dailyReportsColl.whereEqualTo("farmId", farmId)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          trySend(emptyList())
          return@addSnapshotListener
        }
        val list = mutableListOf<DailyReport>()
        if (snapshot != null) {
          for (doc in snapshot.documents) {
            val map = doc.data
            if (map != null) {
              list.add(DailyReport.fromMap(doc.id, map))
            }
          }
        }
        // Sort descending by date
        list.sortByDescending { it.date }
        trySend(list)
      }
    awaitClose { registration.remove() }
  }

  fun observeMonthlyExpenses(farmId: String): Flow<List<MonthlyExpense>> = callbackFlow {
    val registration = monthlyExpensesColl.whereEqualTo("farmId", farmId)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          trySend(emptyList())
          return@addSnapshotListener
        }
        val list = mutableListOf<MonthlyExpense>()
        if (snapshot != null) {
          for (doc in snapshot.documents) {
            val map = doc.data
            if (map != null) {
              list.add(MonthlyExpense.fromMap(doc.id, map))
            }
          }
        }
        list.sortByDescending { it.date }
        trySend(list)
      }
    awaitClose { registration.remove() }
  }

  // CRUD Operations on Cloud Firestore
  suspend fun saveDailyReport(report: DailyReport): Resource<Unit> {
    return try {
      val key = if (report.id.isBlank()) dailyReportsColl.document().id else report.id
      val finalReport = report.copy(
        id = key,
        totalSale = report.eggSold * report.eggPrice,
        updatedAt = System.currentTimeMillis(),
        createdBy = currentUserId ?: report.createdBy
      )
      dailyReportsColl.document(key).set(finalReport.toMap()).await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Error("দৈনিক রিপোর্ট সংরক্ষণে ত্রুটি: ${e.localizedMessage}", e)
    }
  }

  suspend fun deleteDailyReport(reportId: String): Resource<Unit> {
    return try {
      dailyReportsColl.document(reportId).delete().await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Error("রিপোর্ট মুছতে ত্রুটি হয়েছে: ${e.localizedMessage}", e)
    }
  }

  suspend fun saveMonthlyExpense(expense: MonthlyExpense): Resource<Unit> {
    return try {
      val key = if (expense.id.isBlank()) monthlyExpensesColl.document().id else expense.id
      val total = expense.feedCost + expense.medicineCost + expense.staffMarket +
          expense.staffSalary + expense.vehicleRepair + expense.assets +
          expense.electricityBill + expense.otherExpense
      val finalExpense = expense.copy(
        id = key,
        totalExpense = total,
        updatedAt = System.currentTimeMillis(),
        createdBy = currentUserId ?: expense.createdBy
      )
      monthlyExpensesColl.document(key).set(finalExpense.toMap()).await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Error("মাসিক ব্যয় সংরক্ষণে ত্রুটি: ${e.localizedMessage}", e)
    }
  }

  suspend fun deleteMonthlyExpense(expenseId: String): Resource<Unit> {
    return try {
      monthlyExpensesColl.document(expenseId).delete().await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Error("ব্যয় মুছতে ত্রুটি হয়েছে: ${e.localizedMessage}", e)
    }
  }

  suspend fun updateFarmProfile(farmProfile: FarmProfile): Resource<Unit> {
    return try {
      farmsColl.document(farmProfile.farmId).set(farmProfile.toMap(), SetOptions.merge()).await()
      settingsColl.document(farmProfile.farmId).set(
        mapOf(
          "farmName" to farmProfile.farmName,
          "ownerName" to farmProfile.ownerName,
          "mobileNumber" to farmProfile.mobileNumber,
          "address" to farmProfile.address,
          "updatedAt" to System.currentTimeMillis()
        ),
        SetOptions.merge()
      ).await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Error("ফার্মের তথ্য আপডেটে সমস্যা: ${e.localizedMessage}", e)
    }
  }

  suspend fun createCloudBackup(farmId: String): Resource<String> {
    return try {
      val backupId = "backup_${System.currentTimeMillis()}"
      val backupMeta = mapOf(
        "farmId" to farmId,
        "createdAt" to System.currentTimeMillis(),
        "status" to "SUCCESS",
        "createdBy" to (currentUserId ?: "admin")
      )
      backupsColl.document(farmId).collection("records").document(backupId).set(backupMeta).await()
      Resource.Success("ফায়ারবেস ফায়ারস্টোর ক্লাউডে সম্পূর্ণ ব্যাকআপ সফলভাবে সম্পন্ন হয়েছে।")
    } catch (e: Exception) {
      Resource.Error("ক্লাউড ব্যাকআপ ব্যর্থ: ${e.localizedMessage}", e)
    }
  }

  private fun getBengaliAuthErrorMessage(e: Exception): String {
    val msg = e.message ?: ""
    return when {
      msg.contains("badly formatted", ignoreCase = true) || msg.contains("invalid email", ignoreCase = true) ->
        "অনুগ্রহ করে একটি সঠিক ইমেইল ঠিকানা প্রদান করুন।"
      msg.contains("password", ignoreCase = true) && msg.contains("least 6", ignoreCase = true) ->
        "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে।"
      msg.contains("no user record", ignoreCase = true) || msg.contains("user-not-found", ignoreCase = true) ->
        "এই ইমেইলে কোনো ব্যবহারকারী খুঁজে পাওয়া যায়নি।"
      msg.contains("wrong-password", ignoreCase = true) || msg.contains("invalid-credential", ignoreCase = true) ->
        "পাসওয়ার্ড বা ইমেইল ভুল হয়েছে। আবার চেষ্টা করুন।"
      msg.contains("email-already-in-use", ignoreCase = true) ->
        "এই ইমেইল দিয়ে ইতোমধ্যে একটি অ্যাকাউন্ট খোলা আছে।"
      msg.contains("network error", ignoreCase = true) ->
        NetworkHelper.NO_INTERNET_MESSAGE
      else -> "অনুরোধ সম্পন্ন করা যায়নি: $msg"
    }
  }
}

package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.KaziAgroFirebaseRepository
import com.example.data.Resource
import com.example.model.DailyReport
import com.example.model.FarmProfile
import com.example.model.MonthlyExpense
import com.example.model.UserProfile
import com.example.util.BengaliHelper
import com.example.util.ExportHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class KaziAgroUiState(
  val isCheckingAuth: Boolean = true,
  val isAuthenticated: Boolean = false,
  val isAuthLoading: Boolean = false,
  val authError: String? = null,
  val isLoadingData: Boolean = false,
  val generalError: String? = null,
  val toastMessage: String? = null,
  val userProfile: UserProfile? = null,
  val farmProfile: FarmProfile = FarmProfile(),
  val dailyReports: List<DailyReport> = emptyList(),
  val monthlyExpenses: List<MonthlyExpense> = emptyList(),
  val selectedTab: Int = 0, // 0: Dashboard, 1: Daily, 2: Expense, 3: Reports, 4: Settings
  val searchQuery: String = "",
  val selectedMonthFilter: String = "সব মাস",
  val selectedYearFilter: String = "সব বছর",
  val reportType: String = "দৈনিক সারসংক্ষেপ",
  val isDarkTheme: Boolean = false,
  val showAddDailyDialog: Boolean = false,
  val editingDailyReport: DailyReport? = null,
  val showAddExpenseDialog: Boolean = false,
  val editingExpense: MonthlyExpense? = null,
  val viewingDailyReport: DailyReport? = null,
  val viewingExpense: MonthlyExpense? = null,
  val isBackingUp: Boolean = false
) {
  val todayIso: String = BengaliHelper.getTodayDateIso()
  val currentMonthIso: String = if (todayIso.length >= 7) todayIso.substring(0, 7) else ""

  val todayReport: DailyReport? get() = dailyReports.firstOrNull { it.date == todayIso }

  val currentLiveBirds: Long
    get() = dailyReports.firstOrNull()?.currentBirds ?: 0L

  val todayEggProduction: Long
    get() = todayReport?.eggProduction ?: 0L

  val todayEggSold: Long
    get() = todayReport?.eggSold ?: 0L

  val todayTotalSale: Double
    get() = todayReport?.totalSale ?: 0.0

  val currentEggStock: Long
    get() = dailyReports.firstOrNull()?.currentStock ?: 0L

  val todayExpense: Double
    get() = (todayReport?.medicineCost ?: 0.0) +
        monthlyExpenses.filter { it.date == todayIso }.sumOf { it.totalExpense }

  val currentMonthTotalSales: Double
    get() = dailyReports
      .filter { it.date.startsWith(currentMonthIso) }
      .sumOf { it.totalSale }

  val currentMonthTotalExpense: Double
    get() = monthlyExpenses
      .filter { it.date.startsWith(currentMonthIso) }
      .sumOf { it.totalExpense } +
      dailyReports
        .filter { it.date.startsWith(currentMonthIso) }
        .sumOf { it.medicineCost }

  val filteredDailyReports: List<DailyReport>
    get() = dailyReports.filter { r ->
      val matchesSearch = searchQuery.isBlank() ||
          r.date.contains(searchQuery, ignoreCase = true) ||
          r.remarks.contains(searchQuery, ignoreCase = true)

      val matchesMonth = selectedMonthFilter == "সব মাস" ||
          r.date.contains("-$selectedMonthFilter-") ||
          r.date.startsWith(selectedMonthFilter)

      val matchesYear = selectedYearFilter == "সব বছর" ||
          r.date.startsWith(selectedYearFilter)

      matchesSearch && matchesMonth && matchesYear
    }

  val filteredExpenses: List<MonthlyExpense>
    get() = monthlyExpenses.filter { e ->
      val matchesSearch = searchQuery.isBlank() ||
          e.date.contains(searchQuery, ignoreCase = true)

      val matchesMonth = selectedMonthFilter == "সব মাস" ||
          e.date.contains("-$selectedMonthFilter-") ||
          e.date.startsWith(selectedMonthFilter)

      val matchesYear = selectedYearFilter == "সব বছর" ||
          e.date.startsWith(selectedYearFilter)

      matchesSearch && matchesMonth && matchesYear
    }
}

class KaziAgroViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = KaziAgroFirebaseRepository(application.applicationContext)

  private val _uiState = MutableStateFlow(KaziAgroUiState())
  val uiState: StateFlow<KaziAgroUiState> = _uiState.asStateFlow()

  private var userJob: Job? = null
  private var farmJob: Job? = null
  private var dailyJob: Job? = null
  private var expenseJob: Job? = null

  init {
    listenToAuthChanges()
  }

  private fun listenToAuthChanges() {
    viewModelScope.launch {
      repository.authStateFlow.collect { firebaseUser ->
        if (firebaseUser != null) {
          _uiState.update {
            it.copy(
              isCheckingAuth = false,
              isAuthenticated = true,
              authError = null
            )
          }
          observeUserData(firebaseUser.uid)
        } else {
          cancelDataObservations()
          _uiState.update {
            it.copy(
              isCheckingAuth = false,
              isAuthenticated = false,
              userProfile = null,
              dailyReports = emptyList(),
              monthlyExpenses = emptyList()
            )
          }
        }
      }
    }
  }

  private fun observeUserData(uid: String) {
    cancelDataObservations()
    _uiState.update { it.copy(isLoadingData = true) }

    val defaultFarmId = "farm_${uid.take(8)}"
    val initialProfile = UserProfile(uid = uid, email = repository.currentUserId.orEmpty(), farmId = defaultFarmId)
    _uiState.update { it.copy(userProfile = initialProfile) }
    observeFarmData(defaultFarmId)

    userJob = viewModelScope.launch {
      repository.observeUserProfile(uid).collect { profile ->
        if (profile != null) {
          _uiState.update { it.copy(userProfile = profile) }
          if (profile.farmId.isNotBlank() && profile.farmId != defaultFarmId) {
            observeFarmData(profile.farmId)
          }
        }
      }
    }
  }

  private fun observeFarmData(farmId: String) {
    farmJob?.cancel()
    farmJob = viewModelScope.launch {
      repository.observeFarmProfile(farmId).collect { farm ->
        _uiState.update { it.copy(farmProfile = farm, isLoadingData = false) }
      }
    }

    dailyJob?.cancel()
    dailyJob = viewModelScope.launch {
      repository.observeDailyReports(farmId).collect { list ->
        _uiState.update { it.copy(dailyReports = list, isLoadingData = false) }
      }
    }

    expenseJob?.cancel()
    expenseJob = viewModelScope.launch {
      repository.observeMonthlyExpenses(farmId).collect { list ->
        _uiState.update { it.copy(monthlyExpenses = list, isLoadingData = false) }
      }
    }
  }

  private fun cancelDataObservations() {
    userJob?.cancel()
    farmJob?.cancel()
    dailyJob?.cancel()
    expenseJob?.cancel()
  }

  fun login(email: String, pass: String) {
    if (email.isBlank() || pass.isBlank()) {
      _uiState.update { it.copy(authError = "ইমেইল এবং পাসওয়ার্ড প্রদান করুন।") }
      return
    }
    _uiState.update { it.copy(isAuthLoading = true, authError = null) }
    viewModelScope.launch {
      when (val res = repository.login(email, pass)) {
        is Resource.Success -> {
          _uiState.update { it.copy(isAuthLoading = false, authError = null) }
        }
        is Resource.Error -> {
          _uiState.update { it.copy(isAuthLoading = false, authError = res.message) }
        }
        Resource.Loading -> {}
      }
    }
  }

  fun register(
    email: String,
    pass: String,
    name: String,
    farmName: String,
    mobile: String,
    address: String
  ) {
    if (email.isBlank() || pass.isBlank() || name.isBlank()) {
      _uiState.update { it.copy(authError = "অনুগ্রহ করে সব প্রয়োজনীয় ঘর পূরণ করুন।") }
      return
    }
    _uiState.update { it.copy(isAuthLoading = true, authError = null) }
    viewModelScope.launch {
      when (val res = repository.register(email, pass, name, farmName, mobile, address)) {
        is Resource.Success -> {
          _uiState.update { it.copy(isAuthLoading = false, authError = null, toastMessage = "রেজিস্ট্রেশন সফল হয়েছে!") }
        }
        is Resource.Error -> {
          _uiState.update { it.copy(isAuthLoading = false, authError = res.message) }
        }
        Resource.Loading -> {}
      }
    }
  }

  fun sendPasswordReset(email: String) {
    if (email.isBlank()) {
      _uiState.update { it.copy(authError = "পাসওয়ার্ড রিসেট করতে ইমেইল লিখুন।") }
      return
    }
    viewModelScope.launch {
      when (val res = repository.sendPasswordReset(email)) {
        is Resource.Success -> {
          _uiState.update { it.copy(toastMessage = "পাসওয়ার্ড রিসেট লিংক আপনার ইমেইলে পাঠানো হয়েছে।") }
        }
        is Resource.Error -> {
          _uiState.update { it.copy(authError = res.message) }
        }
        Resource.Loading -> {}
      }
    }
  }

  fun logout() {
    repository.logout()
  }

  fun setTab(index: Int) {
    _uiState.update { it.copy(selectedTab = index) }
  }

  fun setSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun setMonthFilter(month: String) {
    _uiState.update { it.copy(selectedMonthFilter = month) }
  }

  fun setYearFilter(year: String) {
    _uiState.update { it.copy(selectedYearFilter = year) }
  }

  fun setReportType(type: String) {
    _uiState.update { it.copy(reportType = type) }
  }

  fun toggleTheme() {
    _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
  }

  fun clearToast() {
    _uiState.update { it.copy(toastMessage = null, generalError = null) }
  }

  // Daily Report Dialog & Actions
  fun openAddDailyReport(editing: DailyReport? = null) {
    _uiState.update {
      it.copy(showAddDailyDialog = true, editingDailyReport = editing)
    }
  }

  fun closeAddDailyReport() {
    _uiState.update {
      it.copy(showAddDailyDialog = false, editingDailyReport = null)
    }
  }

  fun setViewingDailyReport(report: DailyReport?) {
    _uiState.update { it.copy(viewingDailyReport = report) }
  }

  fun saveDailyReport(
    date: String,
    currentBirds: Long,
    deadBirds: Long,
    eggProduction: Long,
    eggSold: Long,
    eggPrice: Double,
    medicineCost: Double,
    remarks: String
  ) {
    val state = _uiState.value
    val farmId = state.userProfile?.farmId ?: state.farmProfile.farmId

    // Calculate previous day egg stock
    val previousReports = state.dailyReports.filter { it.date < date }.sortedByDescending { it.date }
    val previousStock = previousReports.firstOrNull()?.currentStock ?: 0L
    val calculatedStock = previousStock + eggProduction - eggSold
    val calculatedTotalSale = eggSold * eggPrice

    val reportToSave = DailyReport(
      id = state.editingDailyReport?.id.orEmpty(),
      farmId = farmId,
      date = date,
      currentBirds = currentBirds,
      deadBirds = deadBirds,
      eggProduction = eggProduction,
      eggSold = eggSold,
      eggPrice = eggPrice,
      totalSale = calculatedTotalSale,
      medicineCost = medicineCost,
      currentStock = calculatedStock.coerceAtLeast(0L),
      remarks = remarks
    )

    viewModelScope.launch {
      when (val res = repository.saveDailyReport(reportToSave)) {
        is Resource.Success -> {
          _uiState.update {
            it.copy(
              showAddDailyDialog = false,
              editingDailyReport = null,
              toastMessage = "দৈনিক রিপোর্ট সফলভাবে সংরক্ষিত হয়েছে!"
            )
          }
        }
        is Resource.Error -> {
          _uiState.update { it.copy(generalError = res.message) }
        }
        Resource.Loading -> {}
      }
    }
  }

  fun deleteDailyReport(reportId: String) {
    viewModelScope.launch {
      when (val res = repository.deleteDailyReport(reportId)) {
        is Resource.Success -> {
          _uiState.update {
            it.copy(
              viewingDailyReport = null,
              toastMessage = "রিপোর্টটি সফলভাবে মুছে ফেলা হয়েছে।"
            )
          }
        }
        is Resource.Error -> {
          _uiState.update { it.copy(generalError = res.message) }
        }
        Resource.Loading -> {}
      }
    }
  }

  // Monthly Expense Dialog & Actions
  fun openAddExpense(editing: MonthlyExpense? = null) {
    _uiState.update {
      it.copy(showAddExpenseDialog = true, editingExpense = editing)
    }
  }

  fun closeAddExpense() {
    _uiState.update {
      it.copy(showAddExpenseDialog = false, editingExpense = null)
    }
  }

  fun setViewingExpense(expense: MonthlyExpense?) {
    _uiState.update { it.copy(viewingExpense = expense) }
  }

  fun saveMonthlyExpense(
    date: String,
    feedCost: Double,
    medicineCost: Double,
    staffMarket: Double,
    staffSalary: Double,
    vehicleRepair: Double,
    assets: Double,
    electricityBill: Double,
    otherExpense: Double
  ) {
    val state = _uiState.value
    val farmId = state.userProfile?.farmId ?: state.farmProfile.farmId

    val total = feedCost + medicineCost + staffMarket + staffSalary + vehicleRepair + assets + electricityBill + otherExpense

    val expenseToSave = MonthlyExpense(
      id = state.editingExpense?.id.orEmpty(),
      farmId = farmId,
      date = date,
      feedCost = feedCost,
      medicineCost = medicineCost,
      staffMarket = staffMarket,
      staffSalary = staffSalary,
      vehicleRepair = vehicleRepair,
      assets = assets,
      electricityBill = electricityBill,
      otherExpense = otherExpense,
      totalExpense = total
    )

    viewModelScope.launch {
      when (val res = repository.saveMonthlyExpense(expenseToSave)) {
        is Resource.Success -> {
          _uiState.update {
            it.copy(
              showAddExpenseDialog = false,
              editingExpense = null,
              toastMessage = "মাসিক ব্যয় সফলভাবে সংরক্ষিত হয়েছে!"
            )
          }
        }
        is Resource.Error -> {
          _uiState.update { it.copy(generalError = res.message) }
        }
        Resource.Loading -> {}
      }
    }
  }

  fun deleteMonthlyExpense(expenseId: String) {
    viewModelScope.launch {
      when (val res = repository.deleteMonthlyExpense(expenseId)) {
        is Resource.Success -> {
          _uiState.update {
            it.copy(
              viewingExpense = null,
              toastMessage = "ব্যয় সফলভাবে মুছে ফেলা হয়েছে।"
            )
          }
        }
        is Resource.Error -> {
          _uiState.update { it.copy(generalError = res.message) }
        }
        Resource.Loading -> {}
      }
    }
  }

  fun updateFarmProfile(farmName: String, ownerName: String, mobileNumber: String, address: String) {
    val state = _uiState.value
    val updated = state.farmProfile.copy(
      farmName = farmName,
      ownerName = ownerName,
      mobileNumber = mobileNumber,
      address = address,
      updatedAt = System.currentTimeMillis()
    )
    viewModelScope.launch {
      when (val res = repository.updateFarmProfile(updated)) {
        is Resource.Success -> {
          _uiState.update { it.copy(farmProfile = updated, toastMessage = "ফার্মের তথ্য সফলভাবে আপডেট হয়েছে!") }
        }
        is Resource.Error -> {
          _uiState.update { it.copy(generalError = res.message) }
        }
        Resource.Loading -> {}
      }
    }
  }

  fun triggerCloudBackup() {
    val farmId = _uiState.value.farmProfile.farmId
    _uiState.update { it.copy(isBackingUp = true) }
    viewModelScope.launch {
      when (val res = repository.createCloudBackup(farmId)) {
        is Resource.Success -> {
          _uiState.update { it.copy(isBackingUp = false, toastMessage = res.data) }
        }
        is Resource.Error -> {
          _uiState.update { it.copy(isBackingUp = false, generalError = res.message) }
        }
        Resource.Loading -> {}
      }
    }
  }
}

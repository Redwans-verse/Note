package com.example.model

data class UserProfile(
  val uid: String = "",
  val name: String = "",
  val email: String = "",
  val role: String = "Admin",
  val farmId: String = "farm_default",
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
) {
  fun toMap(): Map<String, Any?> = mapOf(
    "uid" to uid,
    "name" to name,
    "email" to email,
    "role" to role,
    "farmId" to farmId,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt
  )

  companion object {
    fun fromMap(uid: String, map: Map<String, Any?>): UserProfile {
      return UserProfile(
        uid = uid,
        name = (map["name"] as? String) ?: "",
        email = (map["email"] as? String) ?: "",
        role = (map["role"] as? String) ?: "Admin",
        farmId = (map["farmId"] as? String) ?: "farm_default",
        createdAt = ((map["createdAt"] as? Number)?.toLong()) ?: System.currentTimeMillis(),
        updatedAt = ((map["updatedAt"] as? Number)?.toLong()) ?: System.currentTimeMillis()
      )
    }
  }
}

data class FarmProfile(
  val farmId: String = "farm_default",
  val farmName: String = "কাজী এগ্রোটেক",
  val ownerName: String = "কাজী মো: রফিকুল ইসলাম",
  val mobileNumber: String = "০১৭০০-০০০০০০",
  val address: String = "কালিয়াকৈর, গাজীপুর, ঢাকা",
  val logoUrl: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
) {
  fun toMap(): Map<String, Any?> = mapOf(
    "farmId" to farmId,
    "farmName" to farmName,
    "ownerName" to ownerName,
    "mobileNumber" to mobileNumber,
    "address" to address,
    "logoUrl" to logoUrl,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt
  )

  companion object {
    fun fromMap(farmId: String, map: Map<String, Any?>): FarmProfile {
      return FarmProfile(
        farmId = farmId,
        farmName = (map["farmName"] as? String) ?: "কাজী এগ্রোটেক",
        ownerName = (map["ownerName"] as? String) ?: "কাজী মো: রফিকুল ইসলাম",
        mobileNumber = (map["mobileNumber"] as? String) ?: "০১৭০০-০০০০০০",
        address = (map["address"] as? String) ?: "কালিয়াকৈর, গাজীপুর, ঢাকা",
        logoUrl = (map["logoUrl"] as? String) ?: "",
        createdAt = ((map["createdAt"] as? Number)?.toLong()) ?: System.currentTimeMillis(),
        updatedAt = ((map["updatedAt"] as? Number)?.toLong()) ?: System.currentTimeMillis()
      )
    }
  }
}

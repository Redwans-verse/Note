package com.example.model

data class DailyReport(
  val id: String = "",
  val farmId: String = "farm_default",
  val date: String = "", // YYYY-MM-DD
  val currentBirds: Long = 0L,
  val deadBirds: Long = 0L,
  val eggProduction: Long = 0L,
  val eggSold: Long = 0L,
  val eggPrice: Double = 0.0,
  val totalSale: Double = 0.0,
  val medicineCost: Double = 0.0,
  val currentStock: Long = 0L,
  val remarks: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val createdBy: String = ""
) {
  fun toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "farmId" to farmId,
    "date" to date,
    "currentBirds" to currentBirds,
    "deadBirds" to deadBirds,
    "eggProduction" to eggProduction,
    "eggSold" to eggSold,
    "eggPrice" to eggPrice,
    "totalSale" to totalSale,
    "medicineCost" to medicineCost,
    "currentStock" to currentStock,
    "remarks" to remarks,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt,
    "createdBy" to createdBy
  )

  companion object {
    fun fromMap(id: String, map: Map<String, Any?>): DailyReport {
      return DailyReport(
        id = id,
        farmId = (map["farmId"] as? String) ?: "farm_default",
        date = (map["date"] as? String) ?: "",
        currentBirds = ((map["currentBirds"] as? Number)?.toLong()) ?: 0L,
        deadBirds = ((map["deadBirds"] as? Number)?.toLong()) ?: 0L,
        eggProduction = ((map["eggProduction"] as? Number)?.toLong()) ?: 0L,
        eggSold = ((map["eggSold"] as? Number)?.toLong()) ?: 0L,
        eggPrice = ((map["eggPrice"] as? Number)?.toDouble()) ?: 0.0,
        totalSale = ((map["totalSale"] as? Number)?.toDouble()) ?: 0.0,
        medicineCost = ((map["medicineCost"] as? Number)?.toDouble()) ?: 0.0,
        currentStock = ((map["currentStock"] as? Number)?.toLong()) ?: 0L,
        remarks = (map["remarks"] as? String) ?: "",
        createdAt = ((map["createdAt"] as? Number)?.toLong()) ?: System.currentTimeMillis(),
        updatedAt = ((map["updatedAt"] as? Number)?.toLong()) ?: System.currentTimeMillis(),
        createdBy = (map["createdBy"] as? String) ?: ""
      )
    }
  }
}

data class MonthlyExpense(
  val id: String = "",
  val farmId: String = "farm_default",
  val date: String = "", // YYYY-MM-DD
  val feedCost: Double = 0.0,
  val medicineCost: Double = 0.0,
  val staffMarket: Double = 0.0,
  val staffSalary: Double = 0.0,
  val vehicleRepair: Double = 0.0,
  val assets: Double = 0.0,
  val electricityBill: Double = 0.0,
  val otherExpense: Double = 0.0,
  val totalExpense: Double = 0.0,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val createdBy: String = ""
) {
  fun toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "farmId" to farmId,
    "date" to date,
    "feedCost" to feedCost,
    "medicineCost" to medicineCost,
    "staffMarket" to staffMarket,
    "staffSalary" to staffSalary,
    "vehicleRepair" to vehicleRepair,
    "assets" to assets,
    "electricityBill" to electricityBill,
    "otherExpense" to otherExpense,
    "totalExpense" to totalExpense,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt,
    "createdBy" to createdBy
  )

  companion object {
    fun fromMap(id: String, map: Map<String, Any?>): MonthlyExpense {
      return MonthlyExpense(
        id = id,
        farmId = (map["farmId"] as? String) ?: "farm_default",
        date = (map["date"] as? String) ?: "",
        feedCost = ((map["feedCost"] as? Number)?.toDouble()) ?: 0.0,
        medicineCost = ((map["medicineCost"] as? Number)?.toDouble()) ?: 0.0,
        staffMarket = ((map["staffMarket"] as? Number)?.toDouble()) ?: 0.0,
        staffSalary = ((map["staffSalary"] as? Number)?.toDouble()) ?: 0.0,
        vehicleRepair = ((map["vehicleRepair"] as? Number)?.toDouble()) ?: 0.0,
        assets = ((map["assets"] as? Number)?.toDouble()) ?: 0.0,
        electricityBill = ((map["electricityBill"] as? Number)?.toDouble()) ?: 0.0,
        otherExpense = ((map["otherExpense"] as? Number)?.toDouble()) ?: 0.0,
        totalExpense = ((map["totalExpense"] as? Number)?.toDouble()) ?: 0.0,
        createdAt = ((map["createdAt"] as? Number)?.toLong()) ?: System.currentTimeMillis(),
        updatedAt = ((map["updatedAt"] as? Number)?.toLong()) ?: System.currentTimeMillis(),
        createdBy = (map["createdBy"] as? String) ?: ""
      )
    }
  }
}

data class FarmSettings(
  val farmId: String = "farm_default",
  val farmName: String = "কাজী এগ্রোটেক",
  val ownerName: String = "কাজী মো: রফিকুল ইসলাম",
  val mobileNumber: String = "০১৭০০-০০০০০০",
  val address: String = "কালিয়াকৈর, গাজীপুর, ঢাকা",
  val logoUrl: String = "",
  val theme: String = "LIGHT",
  val updatedAt: Long = System.currentTimeMillis()
) {
  fun toMap(): Map<String, Any?> = mapOf(
    "farmId" to farmId,
    "farmName" to farmName,
    "ownerName" to ownerName,
    "mobileNumber" to mobileNumber,
    "address" to address,
    "logoUrl" to logoUrl,
    "theme" to theme,
    "updatedAt" to updatedAt
  )

  companion object {
    fun fromMap(farmId: String, map: Map<String, Any?>): FarmSettings {
      return FarmSettings(
        farmId = farmId,
        farmName = (map["farmName"] as? String) ?: "কাজী এগ্রোটেক",
        ownerName = (map["ownerName"] as? String) ?: "কাজী মো: রফিকুল ইসলাম",
        mobileNumber = (map["mobileNumber"] as? String) ?: "০১৭০০-০০০০০০",
        address = (map["address"] as? String) ?: "কালিয়াকৈর, গাজীপুর, ঢাকা",
        logoUrl = (map["logoUrl"] as? String) ?: "",
        theme = (map["theme"] as? String) ?: "LIGHT",
        updatedAt = ((map["updatedAt"] as? Number)?.toLong()) ?: System.currentTimeMillis()
      )
    }
  }
}

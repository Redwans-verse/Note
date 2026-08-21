package com.example.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BengaliHelper {

  private val englishToBengaliDigits = mapOf(
    '0' to '০', '1' to '১', '2' to '২', '3' to '৩', '4' to '৪',
    '5' to '৫', '6' to '৬', '7' to '৭', '8' to '৮', '9' to '৯', '.' to '.'
  )

  private val bengaliToEnglishDigits = mapOf(
    '০' to '0', '১' to '1', '২' to '2', '৩' to '3', '৪' to '4',
    '৫' to '5', '৬' to '6', '৭' to '7', '৮' to '8', '৯' to '9', '.' to '.'
  )

  fun toBengaliDigits(input: Any?): String {
    if (input == null) return "০"
    val str = input.toString()
    val sb = StringBuilder()
    for (ch in str) {
      sb.append(englishToBengaliDigits[ch] ?: ch)
    }
    return sb.toString()
  }

  fun toEnglishDigits(input: String): String {
    val sb = StringBuilder()
    for (ch in input) {
      sb.append(bengaliToEnglishDigits[ch] ?: ch)
    }
    return sb.toString()
  }

  fun formatCurrency(amount: Double): String {
    val formatter = DecimalFormat("#,##,##0.##")
    val formatted = formatter.format(amount)
    return "৳ ${toBengaliDigits(formatted)}"
  }

  fun formatQuantity(qty: Long, unit: String = "টি"): String {
    val formatter = DecimalFormat("#,##,##0")
    val formatted = formatter.format(qty)
    return "${toBengaliDigits(formatted)} $unit"
  }

  fun getTodayDateIso(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return sdf.format(Date())
  }

  fun formatDateDisplay(isoDate: String): String {
    if (isoDate.isBlank()) return ""
    return try {
      val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
      val date = parser.parse(isoDate) ?: Date()
      val displayFormat = SimpleDateFormat("dd MMM, yyyy", Locale.US)
      toBengaliDigits(displayFormat.format(date))
    } catch (e: Exception) {
      toBengaliDigits(isoDate)
    }
  }

  fun parseBengaliLong(input: String): Long {
    val normalized = toEnglishDigits(input.trim().replace(",", "").replace(" ", ""))
    return normalized.toLongOrNull() ?: 0L
  }

  fun parseBengaliDouble(input: String): Double {
    val normalized = toEnglishDigits(input.trim().replace(",", "").replace(" ", ""))
    return normalized.toDoubleOrNull() ?: 0.0
  }
}

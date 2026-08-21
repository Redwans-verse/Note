package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.model.DailyReport
import com.example.model.FarmProfile
import com.example.model.MonthlyExpense
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportHelper {

  fun shareDailyReportsExcel(context: Context, farm: FarmProfile, reports: List<DailyReport>) {
    try {
      val sb = StringBuilder()
      sb.append("\uFEFF") // UTF-8 BOM for Excel Bengali rendering
      sb.append("কাজী এগ্রোটেক - লেয়ার পোল্ট্রি দৈনিক রিপোর্ট\n")
      sb.append("ফার্মের নাম: ${farm.farmName}, মালিক: ${farm.ownerName}, মোবাইল: ${farm.mobileNumber}\n\n")
      sb.append("তারিখ,বর্তমান মুরগী,মৃত মুরগী,ডিম উৎপাদন,বিক্রয় (ডিম),ডিমের দাম (৳),মোট বিক্রয় (৳),ঔষধ খরচ (৳),বর্তমান স্টক,মন্তব্য\n")

      reports.forEach { r ->
        sb.append("\"${r.date}\",")
        sb.append("${r.currentBirds},")
        sb.append("${r.deadBirds},")
        sb.append("${r.eggProduction},")
        sb.append("${r.eggSold},")
        sb.append("${r.eggPrice},")
        sb.append("${r.totalSale},")
        sb.append("${r.medicineCost},")
        sb.append("${r.currentStock},")
        sb.append("\"${r.remarks.replace("\"", "\"\"")}\"\n")
      }

      val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
      val file = File(context.cacheDir, "KaziAgro_DailyReport_$timeStamp.csv")
      FileOutputStream(file).use { out ->
        out.write(sb.toString().toByteArray(Charsets.UTF_8))
      }

      val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "কাজী এগ্রোটেক দৈনিক রিপোর্ট")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      context.startActivity(Intent.createChooser(intent, "এক্সেল/সিএসভি রিপোর্ট শেয়ার করুন"))
    } catch (e: Exception) {
      Toast.makeText(context, "এক্সেল এক্সপোর্ট ব্যর্থ: ${e.message}", Toast.LENGTH_SHORT).show()
    }
  }

  fun shareExpensesExcel(context: Context, farm: FarmProfile, expenses: List<MonthlyExpense>) {
    try {
      val sb = StringBuilder()
      sb.append("\uFEFF") // UTF-8 BOM
      sb.append("কাজী এগ্রোটেক - মাসিক ব্যয় রেজিস্টার\n")
      sb.append("ফার্মের নাম: ${farm.farmName}, মালিক: ${farm.ownerName}, মোবাইল: ${farm.mobileNumber}\n\n")
      sb.append("তারিখ,ফিড/খাবার,মেডিসিন ও ভ্যাকসিন,স্টাফ বাজার,স্টাফ বেতন,গাড়ি মেরামত,আসবাবপত্র,বিদ্যুৎ বিল,অন্যান্য,মোট ব্যয় (৳)\n")

      expenses.forEach { e ->
        sb.append("\"${e.date}\",")
        sb.append("${e.feedCost},")
        sb.append("${e.medicineCost},")
        sb.append("${e.staffMarket},")
        sb.append("${e.staffSalary},")
        sb.append("${e.vehicleRepair},")
        sb.append("${e.assets},")
        sb.append("${e.electricityBill},")
        sb.append("${e.otherExpense},")
        sb.append("${e.totalExpense}\n")
      }

      val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
      val file = File(context.cacheDir, "KaziAgro_MonthlyExpense_$timeStamp.csv")
      FileOutputStream(file).use { out ->
        out.write(sb.toString().toByteArray(Charsets.UTF_8))
      }

      val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "কাজী এগ্রোটেক মাসিক ব্যয় রিপোর্ট")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      context.startActivity(Intent.createChooser(intent, "এক্সেল রিপোর্ট শেয়ার করুন"))
    } catch (e: Exception) {
      Toast.makeText(context, "এক্সেল এক্সপোর্ট ব্যর্থ: ${e.message}", Toast.LENGTH_SHORT).show()
    }
  }

  fun generateAndShareDailyReportPdf(context: Context, farm: FarmProfile, reports: List<DailyReport>) {
    try {
      val doc = PdfDocument()
      val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
      val page = doc.startPage(pageInfo)
      val canvas = page.canvas

      val paintTitle = Paint().apply {
        color = Color.rgb(13, 110, 72)
        textSize = 18f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
      }

      val paintSub = Paint().apply {
        color = Color.DKGRAY
        textSize = 10f
        isAntiAlias = true
      }

      val paintText = Paint().apply {
        color = Color.BLACK
        textSize = 9f
        isAntiAlias = true
      }

      val paintHeader = Paint().apply {
        color = Color.rgb(13, 110, 72)
        textSize = 9f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
      }

      val paintLine = Paint().apply {
        color = Color.LTGRAY
        strokeWidth = 1f
      }

      var y = 40f
      canvas.drawText(farm.farmName, 40f, y, paintTitle)
      y += 16f
      canvas.drawText("লেয়ার পোল্ট্রি ফার্ম দৈনিক রিপোর্ট | মালিক: ${farm.ownerName} | মোবাইল: ${farm.mobileNumber}", 40f, y, paintSub)
      y += 14f
      canvas.drawText("ঠিকানা: ${farm.address}", 40f, y, paintSub)
      y += 10f
      canvas.drawLine(40f, y, 555f, y, paintLine)
      y += 20f

      // Table Headers
      val colX = floatArrayOf(40f, 100f, 150f, 200f, 260f, 320f, 380f, 440f, 500f)
      canvas.drawText("তারিখ", colX[0], y, paintHeader)
      canvas.drawText("মুরগী", colX[1], y, paintHeader)
      canvas.drawText("মৃত", colX[2], y, paintHeader)
      canvas.drawText("ডিম উৎপাদন", colX[3], y, paintHeader)
      canvas.drawText("বিক্রয়", colX[4], y, paintHeader)
      canvas.drawText("দর (৳)", colX[5], y, paintHeader)
      canvas.drawText("মোট বিক্রয়", colX[6], y, paintHeader)
      canvas.drawText("ঔষধ (৳)", colX[7], y, paintHeader)
      canvas.drawText("স্টক", colX[8], y, paintHeader)

      y += 6f
      canvas.drawLine(40f, y, 555f, y, paintLine)
      y += 16f

      var totalEggs = 0L
      var totalSold = 0L
      var totalSalesAmount = 0.0
      var totalMeds = 0.0

      reports.take(35).forEach { r ->
        totalEggs += r.eggProduction
        totalSold += r.eggSold
        totalSalesAmount += r.totalSale
        totalMeds += r.medicineCost

        canvas.drawText(r.date, colX[0], y, paintText)
        canvas.drawText(BengaliHelper.toBengaliDigits(r.currentBirds), colX[1], y, paintText)
        canvas.drawText(BengaliHelper.toBengaliDigits(r.deadBirds), colX[2], y, paintText)
        canvas.drawText(BengaliHelper.toBengaliDigits(r.eggProduction), colX[3], y, paintText)
        canvas.drawText(BengaliHelper.toBengaliDigits(r.eggSold), colX[4], y, paintText)
        canvas.drawText(BengaliHelper.toBengaliDigits(r.eggPrice), colX[5], y, paintText)
        canvas.drawText(BengaliHelper.toBengaliDigits(r.totalSale.toLong()), colX[6], y, paintText)
        canvas.drawText(BengaliHelper.toBengaliDigits(r.medicineCost.toLong()), colX[7], y, paintText)
        canvas.drawText(BengaliHelper.toBengaliDigits(r.currentStock), colX[8], y, paintText)
        y += 15f
      }

      y += 6f
      canvas.drawLine(40f, y, 555f, y, paintLine)
      y += 16f
      val paintTotal = Paint().apply {
        color = Color.rgb(13, 110, 72)
        textSize = 10f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
      }
      canvas.drawText("সর্বমোট: ডিম উৎপাদন: ${BengaliHelper.toBengaliDigits(totalEggs)} টি | বিক্রয়: ${BengaliHelper.toBengaliDigits(totalSold)} টি | বিক্রয় আয়: ${BengaliHelper.formatCurrency(totalSalesAmount)} | ঔষধ: ${BengaliHelper.formatCurrency(totalMeds)}", 40f, y, paintTotal)

      y += 24f
      canvas.drawText("কাজী এগ্রোটেক ম্যানেজমেন্ট সিস্টেম দ্বারা প্রস্তুতকৃত", 40f, y, paintSub)

      doc.finishPage(page)

      val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
      val file = File(context.cacheDir, "KaziAgro_Report_$timeStamp.pdf")
      FileOutputStream(file).use { out ->
        doc.writeTo(out)
      }
      doc.close()

      val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "কাজী এগ্রোটেক দৈনিক রিপোর্ট PDF")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      context.startActivity(Intent.createChooser(intent, "পিডিএফ রিপোর্ট শেয়ার করুন"))
    } catch (e: Exception) {
      Toast.makeText(context, "পিডিএফ তৈরি ব্যর্থ: ${e.message}", Toast.LENGTH_SHORT).show()
    }
  }
}

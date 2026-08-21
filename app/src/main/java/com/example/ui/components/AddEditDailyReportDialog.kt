package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.DailyReport
import com.example.ui.theme.FarmGreenPrimary
import com.example.util.BengaliHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDailyReportDialog(
  editingReport: DailyReport?,
  previousStock: Long,
  onDismiss: () -> Unit,
  onSave: (
    date: String,
    currentBirds: Long,
    deadBirds: Long,
    eggProduction: Long,
    eggSold: Long,
    eggPrice: Double,
    medicineCost: Double,
    remarks: String
  ) -> Unit
) {
  var date by remember { mutableStateOf(editingReport?.date ?: BengaliHelper.getTodayDateIso()) }
  var currentBirdsStr by remember { mutableStateOf(editingReport?.let { BengaliHelper.toBengaliDigits(it.currentBirds) } ?: "") }
  var deadBirdsStr by remember { mutableStateOf(editingReport?.let { BengaliHelper.toBengaliDigits(it.deadBirds) } ?: "০") }
  var eggProductionStr by remember { mutableStateOf(editingReport?.let { BengaliHelper.toBengaliDigits(it.eggProduction) } ?: "") }
  var eggSoldStr by remember { mutableStateOf(editingReport?.let { BengaliHelper.toBengaliDigits(it.eggSold) } ?: "") }
  var eggPriceStr by remember { mutableStateOf(editingReport?.let { BengaliHelper.toBengaliDigits(it.eggPrice) } ?: "১০.৫") }
  var medicineCostStr by remember { mutableStateOf(editingReport?.let { BengaliHelper.toBengaliDigits(it.medicineCost) } ?: "০") }
  var remarks by remember { mutableStateOf(editingReport?.remarks ?: "") }

  var validationError by remember { mutableStateOf<String?>(null) }

  // Auto-calculated values
  val eggProduction = remember(eggProductionStr) { BengaliHelper.parseBengaliLong(eggProductionStr) }
  val eggSold = remember(eggSoldStr) { BengaliHelper.parseBengaliLong(eggSoldStr) }
  val eggPrice = remember(eggPriceStr) { BengaliHelper.parseBengaliDouble(eggPriceStr) }
  val currentBirds = remember(currentBirdsStr) { BengaliHelper.parseBengaliLong(currentBirdsStr) }
  val deadBirds = remember(deadBirdsStr) { BengaliHelper.parseBengaliLong(deadBirdsStr) }
  val medicineCost = remember(medicineCostStr) { BengaliHelper.parseBengaliDouble(medicineCostStr) }

  val autoTotalSale by remember(eggSold, eggPrice) {
    derivedStateOf { eggSold * eggPrice }
  }

  val autoCurrentStock by remember(eggProduction, eggSold, previousStock) {
    derivedStateOf {
      val baseStock = if (editingReport != null) previousStock else previousStock
      (baseStock + eggProduction - eggSold).coerceAtLeast(0L)
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Text(
              if (editingReport == null) "নতুন দৈনিক রিপোর্ট" else "রিপোর্ট সম্পাদনা",
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
          },
          navigationIcon = {
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Close")
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = FarmGreenPrimary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
          )
        )
      }
    ) { padding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .background(MaterialTheme.colorScheme.background)
          .verticalScroll(rememberScrollState())
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        if (validationError != null) {
          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = validationError!!,
              color = MaterialTheme.colorScheme.onErrorContainer,
              fontSize = 13.sp,
              modifier = Modifier.padding(12.dp)
            )
          }
        }

        // Date Picker/Input
        OutlinedTextField(
          value = date,
          onValueChange = { date = it },
          label = { Text("তারিখ (YYYY-MM-DD) *") },
          leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = FarmGreenPrimary) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmGreenPrimary)
        )

        // Current Birds & Dead Birds
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = currentBirdsStr,
            onValueChange = { currentBirdsStr = it },
            label = { Text("বর্তমান মুরগী *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("input_current_birds"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmGreenPrimary)
          )

          OutlinedTextField(
            value = deadBirdsStr,
            onValueChange = { deadBirdsStr = it },
            label = { Text("মৃত মুরগী") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("input_dead_birds"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmGreenPrimary)
          )
        }

        // Egg Production & Egg Sold
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = eggProductionStr,
            onValueChange = { eggProductionStr = it },
            label = { Text("ডিম উৎপাদন (টি) *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("input_egg_production"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmGreenPrimary)
          )

          OutlinedTextField(
            value = eggSoldStr,
            onValueChange = { eggSoldStr = it },
            label = { Text("বিক্রয় (ডিম) *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("input_egg_sold"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmGreenPrimary)
          )
        }

        // Egg Price & Medicine Cost
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = eggPriceStr,
            onValueChange = { eggPriceStr = it },
            label = { Text("প্রতি ডিমের দর (৳)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmGreenPrimary)
          )

          OutlinedTextField(
            value = medicineCostStr,
            onValueChange = { medicineCostStr = it },
            label = { Text("ঔষধ খরচ (৳)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmGreenPrimary)
          )
        }

        // Auto-calculated Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              "স্বয়ংক্রিয় হিসাব (Auto Calculated)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = FarmGreenPrimary
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("মোট বিক্রয় আয়:", fontSize = 13.sp)
              Text(
                BengaliHelper.formatCurrency(autoTotalSale),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = FarmGreenPrimary
              )
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("দিনের শেষে ডিমের স্টক:", fontSize = 13.sp)
              Text(
                BengaliHelper.formatQuantity(autoCurrentStock),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // Remarks
        OutlinedTextField(
          value = remarks,
          onValueChange = { remarks = it },
          label = { Text("মন্তব্য / নোট (ঐচ্ছিক)") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          minLines = 2,
          colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmGreenPrimary)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .height(50.dp),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("বাতিল")
          }

          Button(
            onClick = {
              if (date.isBlank()) {
                validationError = "অনুগ্রহ করে তারিখ নির্বাচন করুন।"
                return@Button
              }
              if (currentBirdsStr.isBlank() || currentBirds <= 0) {
                validationError = "বর্তমান মুরগীর সংখ্যা উল্লেখ করুন।"
                return@Button
              }
              if (eggProductionStr.isBlank() && eggSoldStr.isBlank()) {
                validationError = "ডিম উৎপাদন বা বিক্রয়ের তথ্য দিন।"
                return@Button
              }

              validationError = null
              onSave(
                date,
                currentBirds,
                deadBirds,
                eggProduction,
                eggSold,
                eggPrice,
                medicineCost,
                remarks
              )
            },
            modifier = Modifier
              .weight(1.5f)
              .height(50.dp)
              .testTag("save_daily_report_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
          ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("সংরক্ষণ করুন", fontSize = 15.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

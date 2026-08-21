package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.MonthlyExpense
import com.example.ui.theme.EggAmberSecondary
import com.example.ui.theme.FarmGreenPrimary
import com.example.util.BengaliHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseDialog(
  editingExpense: MonthlyExpense?,
  onDismiss: () -> Unit,
  onSave: (
    date: String,
    feedCost: Double,
    medicineCost: Double,
    staffMarket: Double,
    staffSalary: Double,
    vehicleRepair: Double,
    assets: Double,
    electricityBill: Double,
    otherExpense: Double
  ) -> Unit
) {
  var date by remember { mutableStateOf(editingExpense?.date ?: BengaliHelper.getTodayDateIso()) }
  var feedCostStr by remember { mutableStateOf(editingExpense?.let { BengaliHelper.toBengaliDigits(it.feedCost) } ?: "০") }
  var medicineCostStr by remember { mutableStateOf(editingExpense?.let { BengaliHelper.toBengaliDigits(it.medicineCost) } ?: "০") }
  var staffMarketStr by remember { mutableStateOf(editingExpense?.let { BengaliHelper.toBengaliDigits(it.staffMarket) } ?: "০") }
  var staffSalaryStr by remember { mutableStateOf(editingExpense?.let { BengaliHelper.toBengaliDigits(it.staffSalary) } ?: "০") }
  var vehicleRepairStr by remember { mutableStateOf(editingExpense?.let { BengaliHelper.toBengaliDigits(it.vehicleRepair) } ?: "০") }
  var assetsStr by remember { mutableStateOf(editingExpense?.let { BengaliHelper.toBengaliDigits(it.assets) } ?: "০") }
  var electricityBillStr by remember { mutableStateOf(editingExpense?.let { BengaliHelper.toBengaliDigits(it.electricityBill) } ?: "০") }
  var otherExpenseStr by remember { mutableStateOf(editingExpense?.let { BengaliHelper.toBengaliDigits(it.otherExpense) } ?: "০") }

  var validationError by remember { mutableStateOf<String?>(null) }

  val feed = remember(feedCostStr) { BengaliHelper.parseBengaliDouble(feedCostStr) }
  val med = remember(medicineCostStr) { BengaliHelper.parseBengaliDouble(medicineCostStr) }
  val market = remember(staffMarketStr) { BengaliHelper.parseBengaliDouble(staffMarketStr) }
  val salary = remember(staffSalaryStr) { BengaliHelper.parseBengaliDouble(staffSalaryStr) }
  val vehicle = remember(vehicleRepairStr) { BengaliHelper.parseBengaliDouble(vehicleRepairStr) }
  val asset = remember(assetsStr) { BengaliHelper.parseBengaliDouble(assetsStr) }
  val electricity = remember(electricityBillStr) { BengaliHelper.parseBengaliDouble(electricityBillStr) }
  val other = remember(otherExpenseStr) { BengaliHelper.parseBengaliDouble(otherExpenseStr) }

  val autoTotalExpense by remember(feed, med, market, salary, vehicle, asset, electricity, other) {
    derivedStateOf {
      feed + med + market + salary + vehicle + asset + electricity + other
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
              if (editingExpense == null) "নতুন মাসিক ব্যয় হিসাব" else "ব্যয় সম্পাদনা",
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
            containerColor = EggAmberSecondary,
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

        // Date
        OutlinedTextField(
          value = date,
          onValueChange = { date = it },
          label = { Text("তারিখ (YYYY-MM-DD) *") },
          leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = EggAmberSecondary) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EggAmberSecondary)
        )

        // Feed & Medicine
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = feedCostStr,
            onValueChange = { feedCostStr = it },
            label = { Text("ফিড / খাবার (৳)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EggAmberSecondary)
          )

          OutlinedTextField(
            value = medicineCostStr,
            onValueChange = { medicineCostStr = it },
            label = { Text("মেডিসিন ও ভ্যাকসিন (৳)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EggAmberSecondary)
          )
        }

        // Staff Market & Salary
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = staffMarketStr,
            onValueChange = { staffMarketStr = it },
            label = { Text("স্টাফ বাজার (৳)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EggAmberSecondary)
          )

          OutlinedTextField(
            value = staffSalaryStr,
            onValueChange = { staffSalaryStr = it },
            label = { Text("স্টাফ বেতন / মজুরি (৳)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EggAmberSecondary)
          )
        }

        // Vehicle & Assets
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = vehicleRepairStr,
            onValueChange = { vehicleRepairStr = it },
            label = { Text("গাড়ি মেরামত / যন্ত্র (৳)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EggAmberSecondary)
          )

          OutlinedTextField(
            value = assetsStr,
            onValueChange = { assetsStr = it },
            label = { Text("আসবাবপত্র / সম্পদ (৳)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EggAmberSecondary)
          )
        }

        // Electricity & Other
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = electricityBillStr,
            onValueChange = { electricityBillStr = it },
            label = { Text("বিদ্যুৎ বিল (৳)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EggAmberSecondary)
          )

          OutlinedTextField(
            value = otherExpenseStr,
            onValueChange = { otherExpenseStr = it },
            label = { Text("অন্যান্য খরচ (৳)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EggAmberSecondary)
          )
        }

        // Auto calculated Total Expense Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("মোট ব্যয় (স্বয়ংক্রিয় হিসাব):", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(
              BengaliHelper.formatCurrency(autoTotalExpense),
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFDC2626)
            )
          }
        }

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
                validationError = "অনুগ্রহ করে তারিখ দিন।"
                return@Button
              }
              if (autoTotalExpense <= 0) {
                validationError = "কমপক্ষে একটি খরচের পরিমাণ উল্লেখ করুন।"
                return@Button
              }

              validationError = null
              onSave(
                date,
                feed,
                med,
                market,
                salary,
                vehicle,
                asset,
                electricity,
                other
              )
            },
            modifier = Modifier
              .weight(1.5f)
              .height(50.dp)
              .testTag("save_expense_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EggAmberSecondary)
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

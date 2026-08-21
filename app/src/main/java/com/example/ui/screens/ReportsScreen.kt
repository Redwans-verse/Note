package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KaziAgroUiState
import com.example.ui.theme.EggAmberSecondary
import com.example.ui.theme.FarmGreenPrimary
import com.example.util.BengaliHelper
import com.example.util.ExportHelper

@Composable
fun ReportsScreen(
  uiState: KaziAgroUiState,
  onMonthChange: (String) -> Unit,
  onYearChange: (String) -> Unit
) {
  val context = LocalContext.current

  val reportTypes = listOf(
    "সার্বিক রিপোর্ট",
    "ডিম উৎপাদন রিপোর্ট",
    "বিক্রয় রিপোর্ট",
    "ব্যয় বিশ্লেষণ রিপোর্ট"
  )
  var selectedType by remember { mutableStateOf(reportTypes[0]) }

  val monthsList = listOf("সব মাস", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
  val yearsList = listOf("সব বছর", "2024", "2025", "2026", "2027")

  // Filtered dataset calculations
  val reports = uiState.filteredDailyReports
  val expenses = uiState.filteredExpenses

  val totalEggsProduced = reports.sumOf { it.eggProduction }
  val totalEggsSold = reports.sumOf { it.eggSold }
  val totalSalesAmount = reports.sumOf { it.totalSale }
  val totalMedicineInDaily = reports.sumOf { it.medicineCost }
  val totalDirectExpenses = expenses.sumOf { it.totalExpense }
  val grandTotalExpenses = totalMedicineInDaily + totalDirectExpenses
  val netProfit = totalSalesAmount - grandTotalExpenses
  val isProfitable = netProfit >= 0

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("reports_screen"),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "ব্যবসায়িক রিপোর্ট ও অডিট",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    // Report Type Chips
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        reportTypes.forEach { type ->
          val isSelected = selectedType == type
          FilterChip(
            selected = isSelected,
            onClick = { selectedType = type },
            label = { Text(type, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = FarmGreenPrimary,
              selectedLabelColor = Color.White
            )
          )
        }
      }
    }

    // Month & Year Filter Row
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        monthsList.forEach { m ->
          val isSelected = uiState.selectedMonthFilter == m
          val label = if (m == "সব মাস") "সব মাস" else "${BengaliHelper.toBengaliDigits(m)} নং মাস"
          FilterChip(
            selected = isSelected,
            onClick = { onMonthChange(m) },
            label = { Text(label, fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = EggAmberSecondary,
              selectedLabelColor = Color.White
            )
          )
        }
      }
    }

    // Summary Financial KPI Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "নির্বাচিত সময়ের আর্থিক পর্যালোচনা",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("মোট উৎপাদিত ডিম:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(BengaliHelper.formatQuantity(totalEggsProduced), fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("মোট বিক্রীত ডিম:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(BengaliHelper.formatQuantity(totalEggsSold), fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("সর্বমোট বিক্রয় আয়:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
              BengaliHelper.formatCurrency(totalSalesAmount),
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = FarmGreenPrimary
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("সর্বমোট ব্যয়/খরচ:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
              BengaliHelper.formatCurrency(grandTotalExpenses),
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFDC2626)
            )
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(1.dp)
              .background(MaterialTheme.colorScheme.outlineVariant)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              if (isProfitable) "নীট অর্জিত লাভ:" else "নীট ক্ষতি:",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = if (isProfitable) FarmGreenPrimary else Color(0xFFDC2626)
            )
            Text(
              BengaliHelper.formatCurrency(kotlin.math.abs(netProfit)),
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = if (isProfitable) FarmGreenPrimary else Color(0xFFDC2626)
            )
          }
        }
      }
    }

    // Export Actions Section
    item {
      Text(
        text = "রিপোর্ট এক্সপোর্ট ও প্রিন্ট",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(4.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = {
            ExportHelper.generateAndShareDailyReportPdf(context, uiState.farmProfile, reports)
          },
          modifier = Modifier
            .weight(1f)
            .height(48.dp),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
        ) {
          Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("পিডিএফ (PDF)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = {
            ExportHelper.shareDailyReportsExcel(context, uiState.farmProfile, reports)
          },
          modifier = Modifier
            .weight(1f)
            .height(48.dp),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = EggAmberSecondary)
        ) {
          Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("এক্সেল (Excel)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Detailed Breakdown List
    item {
      Text(
        text = "রেকর্ড তালিকা (${BengaliHelper.toBengaliDigits(reports.size)} টি দৈনিক রেকর্ড)",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    if (reports.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Text(
            text = "নির্বাচিত ফিল্টারে কোনো তথ্য পাওয়া যায়নি।",
            fontSize = 13.sp,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    } else {
      items(reports.size) { index ->
        val item = reports[index]
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                BengaliHelper.formatDateDisplay(item.date),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                "উৎপাদন: ${BengaliHelper.formatQuantity(item.eggProduction)} | বিক্রয়: ${BengaliHelper.formatQuantity(item.eggSold)}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(
                BengaliHelper.formatCurrency(item.totalSale),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = FarmGreenPrimary
              )
              Text(
                "স্টক: ${BengaliHelper.formatQuantity(item.currentStock)}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(32.dp))
    }
  }
}

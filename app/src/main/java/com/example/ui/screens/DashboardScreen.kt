package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KaziAgroUiState
import com.example.ui.theme.EggAmberSecondary
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.FarmTeal
import com.example.util.BengaliHelper

@Composable
fun DashboardScreen(
  uiState: KaziAgroUiState,
  onOpenAddDailyReport: () -> Unit,
  onOpenAddExpense: () -> Unit,
  onNavigateToReports: () -> Unit,
  onNavigateToDaily: () -> Unit,
  onNavigateToExpenses: () -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("dashboard_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    if (uiState.isLoadingData && uiState.dailyReports.isEmpty() && uiState.monthlyExpenses.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = FarmGreenPrimary.copy(alpha = 0.08f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            CircularProgressIndicator(
              color = FarmGreenPrimary,
              strokeWidth = 2.dp,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = "ফায়ারস্টোর ক্লাউড থেকে ডাটা সিঙ্ক হচ্ছে...",
              fontSize = 12.sp,
              color = FarmGreenPrimary
            )
          }
        }
      }
    }
    item {
      Spacer(modifier = Modifier.height(8.dp))
      // Farm Header Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FarmGreenPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = uiState.farmProfile.farmName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "মালিক: ${uiState.farmProfile.ownerName} | ${uiState.farmProfile.mobileNumber}",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color.White.copy(alpha = 0.2f),
              modifier = Modifier.padding(start = 8.dp)
            ) {
              Text(
                text = BengaliHelper.formatDateDisplay(uiState.todayIso),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }

    // Quick Actions
    item {
      Text(
        text = "কুইক একশন",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onOpenAddDailyReport,
          modifier = Modifier
            .weight(1f)
            .height(48.dp),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("দৈনিক রিপোর্ট", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onOpenAddExpense,
          modifier = Modifier
            .weight(1f)
            .height(48.dp),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = EggAmberSecondary)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("মাসিক ব্যয়", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onNavigateToReports,
          modifier = Modifier
            .weight(0.9f)
            .height(48.dp),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(2.dp))
          Text("রিপোর্ট", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // 7 KPI Stat Cards Grid (Layout organized in rows)
    item {
      Text(
        text = "ফার্ম সারসংক্ষেপ",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(6.dp))

      // Row 1: বর্তমান মুরগী & আজকের ডিম উৎপাদন
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        KpiCard(
          title = "বর্তমান মুরগী",
          value = BengaliHelper.formatQuantity(uiState.currentLiveBirds),
          icon = Icons.Default.Home,
          accentColor = FarmGreenPrimary,
          modifier = Modifier.weight(1f)
        )
        KpiCard(
          title = "আজকের ডিম উৎপাদন",
          value = BengaliHelper.formatQuantity(uiState.todayEggProduction),
          icon = Icons.Default.Egg,
          accentColor = EggAmberSecondary,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Row 2: আজকের মোট বিক্রয় & বর্তমান ডিম স্টক
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        KpiCard(
          title = "আজকের মোট বিক্রয়",
          value = BengaliHelper.formatCurrency(uiState.todayTotalSale),
          icon = Icons.Default.TrendingUp,
          accentColor = FarmTeal,
          modifier = Modifier.weight(1f)
        )
        KpiCard(
          title = "বর্তমান ডিম স্টক",
          value = BengaliHelper.formatQuantity(uiState.currentEggStock),
          icon = Icons.Default.Egg,
          accentColor = Color(0xFFD97706),
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Row 3: আজকের মোট ব্যয়
      KpiCard(
        title = "আজকের মোট ব্যয়",
        value = BengaliHelper.formatCurrency(uiState.todayExpense),
        icon = Icons.Default.Payments,
        accentColor = Color(0xFFDC2626),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Row 4: চলতি মাসের মোট বিক্রয় & চলতি মাসের মোট ব্যয়
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        KpiCard(
          title = "চলতি মাসের বিক্রয়",
          value = BengaliHelper.formatCurrency(uiState.currentMonthTotalSales),
          icon = Icons.Default.MonetizationOn,
          accentColor = FarmGreenPrimary,
          modifier = Modifier.weight(1f)
        )
        KpiCard(
          title = "চলতি মাসের ব্যয়",
          value = BengaliHelper.formatCurrency(uiState.currentMonthTotalExpense),
          icon = Icons.Default.ReceiptLong,
          accentColor = Color(0xFFB91C1C),
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Visual Charts
    item {
      Text(
        text = "উৎপাদন ও বিক্রয় ট্রেন্ড",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(6.dp))

      // Egg Production 7-Day Chart
      ProductionTrendChartCard(reports = uiState.dailyReports.take(7).reversed())
    }

    // Financial Overview Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "চলতি মাসের আর্থিক হিসাব",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(12.dp))

          val profit = uiState.currentMonthTotalSales - uiState.currentMonthTotalExpense
          val isProfitable = profit >= 0

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("মোট বিক্রয় আয়:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(BengaliHelper.formatCurrency(uiState.currentMonthTotalSales), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
          }

          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("মোট খরচ/ব্যয়:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(BengaliHelper.formatCurrency(uiState.currentMonthTotalExpense), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
          }

          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(1.dp)
              .background(MaterialTheme.colorScheme.outlineVariant)
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              if (isProfitable) "নীট লাভ:" else "নীট ক্ষতি:",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = if (isProfitable) FarmGreenPrimary else Color(0xFFDC2626)
            )
            Text(
              BengaliHelper.formatCurrency(kotlin.math.abs(profit)),
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = if (isProfitable) FarmGreenPrimary else Color(0xFFDC2626)
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
fun KpiCard(
  title: String,
  value: String,
  icon: ImageVector,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = 0.12f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(16.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = value,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@Composable
fun ProductionTrendChartCard(reports: List<com.example.model.DailyReport>) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "দৈনিক ডিম উৎপাদন চিত্র (সর্বশেষ রিপোর্ট)",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(14.dp))

      if (reports.isEmpty()) {
        Text(
          "এখনো কোনো রিপোর্ট সংরক্ষণ করা হয়নি। নতুন রিপোর্ট যোগ করুন।",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      } else {
        val maxVal = (reports.maxOfOrNull { it.eggProduction } ?: 100L).coerceAtLeast(10L)

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          reports.forEach { report ->
            val fraction = (report.eggProduction.toFloat() / maxVal.toFloat()).coerceIn(0.1f, 1f)
            val dateLabel = if (report.date.length >= 5) report.date.takeLast(5) else report.date

            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Bottom,
              modifier = Modifier.weight(1f)
            ) {
              Text(
                text = BengaliHelper.toBengaliDigits(report.eggProduction),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = FarmGreenPrimary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Box(
                modifier = Modifier
                  .width(18.dp)
                  .height((fraction * 75).dp)
                  .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                  .background(
                    Brush.verticalGradient(
                      listOf(FarmGreenPrimary, FarmGreenPrimary.copy(alpha = 0.6f))
                    )
                  )
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = BengaliHelper.toBengaliDigits(dateLabel),
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }
  }
}

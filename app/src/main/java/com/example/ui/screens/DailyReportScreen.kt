package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyReport
import com.example.ui.KaziAgroUiState
import com.example.ui.theme.FarmGreenPrimary
import com.example.util.BengaliHelper
import com.example.util.ExportHelper

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DailyReportScreen(
  uiState: KaziAgroUiState,
  onOpenAddReport: () -> Unit,
  onEditReport: (DailyReport) -> Unit,
  onDeleteReport: (String) -> Unit,
  onSearchChange: (String) -> Unit,
  onMonthChange: (String) -> Unit,
  onYearChange: (String) -> Unit
) {
  val context = LocalContext.current
  val horizontalScrollState = rememberScrollState()

  var selectedReportForAction by remember { mutableStateOf<DailyReport?>(null) }
  var reportToDelete by remember { mutableStateOf<DailyReport?>(null) }
  var viewingReportDetails by remember { mutableStateOf<DailyReport?>(null) }

  val monthsList = listOf("সব মাস", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
  val yearsList = listOf("সব বছর", "2024", "2025", "2026", "2027")

  Box(
    modifier = Modifier
      .fillMaxSize()
      .testTag("daily_report_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 12.dp)
    ) {
      Spacer(modifier = Modifier.height(8.dp))

      // Top Filter & Search Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = uiState.searchQuery,
          onValueChange = onSearchChange,
          placeholder = { Text("তারিখ বা মন্তব্য খুঁজুন...", fontSize = 13.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = FarmGreenPrimary) },
          trailingIcon = {
            if (uiState.searchQuery.isNotEmpty()) {
              IconButton(onClick = { onSearchChange("") }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.weight(1f),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FarmGreenPrimary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
          )
        )

        // PDF Export Button
        IconButton(
          onClick = {
            ExportHelper.generateAndShareDailyReportPdf(context, uiState.farmProfile, uiState.filteredDailyReports)
          },
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(FarmGreenPrimary.copy(alpha = 0.1f))
        ) {
          Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF Export", tint = FarmGreenPrimary)
        }

        // Excel Export Button
        IconButton(
          onClick = {
            ExportHelper.shareDailyReportsExcel(context, uiState.farmProfile, uiState.filteredDailyReports)
          },
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(FarmGreenPrimary.copy(alpha = 0.1f))
        ) {
          Icon(Icons.Default.TableChart, contentDescription = "Excel Export", tint = FarmGreenPrimary)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Month & Year Filter Row
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
            label = { Text(label, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = FarmGreenPrimary,
              selectedLabelColor = Color.White
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Modern Sheet View Table Container
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        if (uiState.filteredDailyReports.isEmpty()) {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                "কোনো দৈনিক রিপোর্ট পাওয়া যায়নি।",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                "নিচের '+' বাটনে চাপ দিয়ে নতুন রিপোর্ট যুক্ত করুন।",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
              )
            }
          }
        } else {
          // Table Layout with Sticky Header & Frozen Date Column
          Row(modifier = Modifier.fillMaxSize()) {
            // FROZEN COLUMN (Date)
            Column(
              modifier = Modifier
                .width(100.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
              // Sticky Header Cell for Date
              Box(
                modifier = Modifier
                  .width(100.dp)
                  .height(48.dp)
                  .background(FarmGreenPrimary)
                  .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "তারিখ",
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  fontSize = 12.sp
                )
              }

              // Scrollable Date Column Rows
              LazyColumn(modifier = Modifier.weight(1f)) {
                items(uiState.filteredDailyReports, key = { it.id }) { report ->
                  Box(
                    modifier = Modifier
                      .width(100.dp)
                      .height(52.dp)
                      .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                      .combinedClickable(
                        onClick = { viewingReportDetails = report },
                        onLongClick = { selectedReportForAction = report }
                      )
                      .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = BengaliHelper.toBengaliDigits(report.date),
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = MaterialTheme.colorScheme.onSurface,
                      textAlign = TextAlign.Center
                    )
                  }
                }
              }
            }

            // HORIZONTALLY SCROLLABLE METRIC COLUMNS
            Column(
              modifier = Modifier
                .weight(1f)
                .horizontalScroll(horizontalScrollState)
            ) {
              // Sticky Header Row for other fields
              Row(
                modifier = Modifier
                  .height(48.dp)
                  .background(FarmGreenPrimary.copy(alpha = 0.9f)),
                verticalAlignment = Alignment.CenterVertically
              ) {
                TableHeaderCell("বর্তমান মুরগী", width = 85.dp)
                TableHeaderCell("মৃত", width = 55.dp)
                TableHeaderCell("ডিম উৎপাদন", width = 85.dp)
                TableHeaderCell("বিক্রয় (ডিম)", width = 80.dp)
                TableHeaderCell("দর (৳)", width = 60.dp)
                TableHeaderCell("মোট বিক্রয়", width = 95.dp)
                TableHeaderCell("ঔষধ (৳)", width = 75.dp)
                TableHeaderCell("বর্তমান স্টক", width = 85.dp)
                TableHeaderCell("মন্তব্য", width = 110.dp)
              }

              // Scrollable Rows for Metrics
              LazyColumn(modifier = Modifier.weight(1f)) {
                items(uiState.filteredDailyReports, key = { it.id }) { report ->
                  Row(
                    modifier = Modifier
                      .height(52.dp)
                      .combinedClickable(
                        onClick = { viewingReportDetails = report },
                        onLongClick = { selectedReportForAction = report }
                      ),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    TableCell(BengaliHelper.toBengaliDigits(report.currentBirds), width = 85.dp)
                    TableCell(BengaliHelper.toBengaliDigits(report.deadBirds), width = 55.dp, textColor = Color(0xFFDC2626))
                    TableCell(BengaliHelper.toBengaliDigits(report.eggProduction), width = 85.dp, isBold = true)
                    TableCell(BengaliHelper.toBengaliDigits(report.eggSold), width = 80.dp)
                    TableCell(BengaliHelper.toBengaliDigits(report.eggPrice), width = 60.dp)
                    TableCell(BengaliHelper.formatCurrency(report.totalSale), width = 95.dp, isBold = true, textColor = FarmGreenPrimary)
                    TableCell(BengaliHelper.formatCurrency(report.medicineCost), width = 75.dp)
                    TableCell(BengaliHelper.toBengaliDigits(report.currentStock), width = 85.dp, isBold = true)
                    TableCell(report.remarks.ifBlank { "-" }, width = 110.dp)
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(72.dp))
    }

    // Floating Action Button: "নতুন রিপোর্ট"
    FloatingActionButton(
      onClick = onOpenAddReport,
      containerColor = FarmGreenPrimary,
      contentColor = Color.White,
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
        .testTag("add_daily_report_fab")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text("নতুন রিপোর্ট", fontWeight = FontWeight.Bold, fontSize = 14.sp)
      }
    }

    // Action Context Bottom Sheet (Edit / Delete / View)
    if (selectedReportForAction != null) {
      val rep = selectedReportForAction!!
      ModalBottomSheet(
        onDismissRequest = { selectedReportForAction = null },
        sheetState = rememberModalBottomSheetState()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
          Text(
            text = "তারিখ: ${BengaliHelper.formatDateDisplay(rep.date)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(16.dp))

          // View Details Option
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .combinedClickable(onClick = {
                viewingReportDetails = rep
                selectedReportForAction = null
              })
              .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Visibility, contentDescription = null, tint = FarmGreenPrimary)
            Spacer(modifier = Modifier.width(12.dp))
            Text("বিস্তারিত দেখুন", fontSize = 15.sp)
          }

          // Edit Option
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .combinedClickable(onClick = {
                onEditReport(rep)
                selectedReportForAction = null
              })
              .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF2563EB))
            Spacer(modifier = Modifier.width(12.dp))
            Text("সম্পাদনা করুন", fontSize = 15.sp)
          }

          // Delete Option
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .combinedClickable(onClick = {
                reportToDelete = rep
                selectedReportForAction = null
              })
              .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFDC2626))
            Spacer(modifier = Modifier.width(12.dp))
            Text("মুছে ফেলুন", fontSize = 15.sp, color = Color(0xFFDC2626))
          }

          Spacer(modifier = Modifier.height(20.dp))
        }
      }
    }

    // Details Dialog
    if (viewingReportDetails != null) {
      val r = viewingReportDetails!!
      AlertDialog(
        onDismissRequest = { viewingReportDetails = null },
        title = {
          Text(
            "দৈনিক রিপোর্ট বিবরণ (${BengaliHelper.formatDateDisplay(r.date)})",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ReportDetailRow("বর্তমান মুরগী:", BengaliHelper.formatQuantity(r.currentBirds))
            ReportDetailRow("মৃত মুরগী:", BengaliHelper.formatQuantity(r.deadBirds))
            ReportDetailRow("ডিম উৎপাদন:", BengaliHelper.formatQuantity(r.eggProduction))
            ReportDetailRow("বিক্রয় (ডিম):", BengaliHelper.formatQuantity(r.eggSold))
            ReportDetailRow("প্রতি ডিমের দাম:", BengaliHelper.formatCurrency(r.eggPrice))
            ReportDetailRow("মোট বিক্রয়:", BengaliHelper.formatCurrency(r.totalSale))
            ReportDetailRow("ঔষধ খরচ:", BengaliHelper.formatCurrency(r.medicineCost))
            ReportDetailRow("বর্তমান স্টক:", BengaliHelper.formatQuantity(r.currentStock))
            if (r.remarks.isNotBlank()) {
              ReportDetailRow("মন্তব্য:", r.remarks)
            }
          }
        },
        confirmButton = {
          Button(
            onClick = { viewingReportDetails = null },
            colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
          ) {
            Text("ঠিক আছে")
          }
        }
      )
    }

    // Delete Confirmation Dialog
    if (reportToDelete != null) {
      AlertDialog(
        onDismissRequest = { reportToDelete = null },
        title = { Text("রিপোর্ট মুছে ফেলার নিশ্চিতকরণ", fontWeight = FontWeight.Bold) },
        text = {
          Text(
            "আপনি কি নিশ্চিত যে ${BengaliHelper.formatDateDisplay(reportToDelete!!.date)} তারিখের রিপোর্টটি স্থায়ীভাবে মুছে ফেলতে চান?",
            fontSize = 14.sp
          )
        },
        confirmButton = {
          Button(
            onClick = {
              onDeleteReport(reportToDelete!!.id)
              reportToDelete = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
          ) {
            Text("হ্যাঁ, মুছুন")
          }
        },
        dismissButton = {
          TextButton(onClick = { reportToDelete = null }) {
            Text("বাতিল")
          }
        }
      )
    }
  }
}

@Composable
fun TableHeaderCell(title: String, width: androidx.compose.ui.unit.Dp) {
  Box(
    modifier = Modifier
      .width(width)
      .height(48.dp)
      .border(0.5.dp, Color.White.copy(alpha = 0.2f))
      .padding(horizontal = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = title,
      fontWeight = FontWeight.Bold,
      color = Color.White,
      fontSize = 11.sp,
      textAlign = TextAlign.Center,
      maxLines = 2
    )
  }
}

@Composable
fun TableCell(
  text: String,
  width: androidx.compose.ui.unit.Dp,
  isBold: Boolean = false,
  textColor: Color = Color.Unspecified
) {
  Box(
    modifier = Modifier
      .width(width)
      .height(52.dp)
      .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
      .padding(horizontal = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      fontSize = 11.sp,
      fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
      color = if (textColor != Color.Unspecified) textColor else MaterialTheme.colorScheme.onSurface,
      textAlign = TextAlign.Center,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
fun ReportDetailRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
  }
}

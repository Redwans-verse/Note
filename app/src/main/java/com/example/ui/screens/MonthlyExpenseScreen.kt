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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MonthlyExpense
import com.example.ui.KaziAgroUiState
import com.example.ui.theme.EggAmberSecondary
import com.example.ui.theme.FarmGreenPrimary
import com.example.util.BengaliHelper
import com.example.util.ExportHelper

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MonthlyExpenseScreen(
  uiState: KaziAgroUiState,
  onOpenAddExpense: () -> Unit,
  onEditExpense: (MonthlyExpense) -> Unit,
  onDeleteExpense: (String) -> Unit,
  onSearchChange: (String) -> Unit,
  onMonthChange: (String) -> Unit,
  onYearChange: (String) -> Unit
) {
  val context = LocalContext.current
  val horizontalScrollState = rememberScrollState()

  var selectedExpenseForAction by remember { mutableStateOf<MonthlyExpense?>(null) }
  var expenseToDelete by remember { mutableStateOf<MonthlyExpense?>(null) }
  var viewingExpenseDetails by remember { mutableStateOf<MonthlyExpense?>(null) }

  val monthsList = listOf("সব মাস", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")

  Box(
    modifier = Modifier
      .fillMaxSize()
      .testTag("monthly_expense_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 12.dp)
    ) {
      Spacer(modifier = Modifier.height(8.dp))

      // Filter & Search Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = uiState.searchQuery,
          onValueChange = onSearchChange,
          placeholder = { Text("তারিখ দিয়ে খুঁজুন...", fontSize = 13.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EggAmberSecondary) },
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
            focusedBorderColor = EggAmberSecondary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
          )
        )

        // Excel Export
        IconButton(
          onClick = {
            ExportHelper.shareExpensesExcel(context, uiState.farmProfile, uiState.filteredExpenses)
          },
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(EggAmberSecondary.copy(alpha = 0.1f))
        ) {
          Icon(Icons.Default.TableChart, contentDescription = "Excel Export", tint = EggAmberSecondary)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Month Filter Row
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
              selectedContainerColor = EggAmberSecondary,
              selectedLabelColor = Color.White
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Sheet View Table Container
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        if (uiState.filteredExpenses.isEmpty()) {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                "কোনো মাসিক ব্যয়ের হিসাব পাওয়া যায়নি।",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                "নিচের '+' বাটনে চাপ দিয়ে নতুন ব্যয় যোগ করুন।",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
              )
            }
          }
        } else {
          Row(modifier = Modifier.fillMaxSize()) {
            // FROZEN COLUMN (Date)
            Column(
              modifier = Modifier
                .width(100.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
              Box(
                modifier = Modifier
                  .width(100.dp)
                  .height(48.dp)
                  .background(EggAmberSecondary)
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

              LazyColumn(modifier = Modifier.weight(1f)) {
                items(uiState.filteredExpenses, key = { it.id }) { expense ->
                  Box(
                    modifier = Modifier
                      .width(100.dp)
                      .height(52.dp)
                      .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                      .combinedClickable(
                        onClick = { viewingExpenseDetails = expense },
                        onLongClick = { selectedExpenseForAction = expense }
                      )
                      .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = BengaliHelper.toBengaliDigits(expense.date),
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = MaterialTheme.colorScheme.onSurface,
                      textAlign = TextAlign.Center
                    )
                  }
                }
              }
            }

            // HORIZONTALLY SCROLLABLE COST COLUMNS
            Column(
              modifier = Modifier
                .weight(1f)
                .horizontalScroll(horizontalScrollState)
            ) {
              Row(
                modifier = Modifier
                  .height(48.dp)
                  .background(EggAmberSecondary.copy(alpha = 0.9f)),
                verticalAlignment = Alignment.CenterVertically
              ) {
                TableHeaderCell("মোট ব্যয় (৳)", width = 95.dp)
                TableHeaderCell("ফিড / খাবার", width = 85.dp)
                TableHeaderCell("মেডিসিন/ভ্যাকসিন", width = 95.dp)
                TableHeaderCell("স্টাফ বাজার", width = 80.dp)
                TableHeaderCell("স্টাফ বেতন", width = 85.dp)
                TableHeaderCell("গাড়ি মেরামত", width = 85.dp)
                TableHeaderCell("আসবাবপত্র", width = 80.dp)
                TableHeaderCell("বিদ্যুৎ বিল", width = 80.dp)
                TableHeaderCell("অন্যান্য", width = 80.dp)
              }

              LazyColumn(modifier = Modifier.weight(1f)) {
                items(uiState.filteredExpenses, key = { it.id }) { expense ->
                  Row(
                    modifier = Modifier
                      .height(52.dp)
                      .combinedClickable(
                        onClick = { viewingExpenseDetails = expense },
                        onLongClick = { selectedExpenseForAction = expense }
                      ),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    TableCell(BengaliHelper.formatCurrency(expense.totalExpense), width = 95.dp, isBold = true, textColor = Color(0xFFDC2626))
                    TableCell(BengaliHelper.formatCurrency(expense.feedCost), width = 85.dp)
                    TableCell(BengaliHelper.formatCurrency(expense.medicineCost), width = 95.dp)
                    TableCell(BengaliHelper.formatCurrency(expense.staffMarket), width = 80.dp)
                    TableCell(BengaliHelper.formatCurrency(expense.staffSalary), width = 85.dp)
                    TableCell(BengaliHelper.formatCurrency(expense.vehicleRepair), width = 85.dp)
                    TableCell(BengaliHelper.formatCurrency(expense.assets), width = 80.dp)
                    TableCell(BengaliHelper.formatCurrency(expense.electricityBill), width = 80.dp)
                    TableCell(BengaliHelper.formatCurrency(expense.otherExpense), width = 80.dp)
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(72.dp))
    }

    // Floating Button: "নতুন ব্যয়"
    FloatingActionButton(
      onClick = onOpenAddExpense,
      containerColor = EggAmberSecondary,
      contentColor = Color.White,
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
        .testTag("add_expense_fab")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text("নতুন ব্যয়", fontWeight = FontWeight.Bold, fontSize = 14.sp)
      }
    }

    // Context Action Bottom Sheet
    if (selectedExpenseForAction != null) {
      val exp = selectedExpenseForAction!!
      ModalBottomSheet(
        onDismissRequest = { selectedExpenseForAction = null },
        sheetState = rememberModalBottomSheetState()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
          Text(
            text = "ব্যয়: ${BengaliHelper.formatDateDisplay(exp.date)} (মোট ${BengaliHelper.formatCurrency(exp.totalExpense)})",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(16.dp))

          // View Details
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .combinedClickable(onClick = {
                viewingExpenseDetails = exp
                selectedExpenseForAction = null
              })
              .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Visibility, contentDescription = null, tint = FarmGreenPrimary)
            Spacer(modifier = Modifier.width(12.dp))
            Text("বিস্তারিত দেখুন", fontSize = 15.sp)
          }

          // Edit
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .combinedClickable(onClick = {
                onEditExpense(exp)
                selectedExpenseForAction = null
              })
              .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF2563EB))
            Spacer(modifier = Modifier.width(12.dp))
            Text("সম্পাদনা করুন", fontSize = 15.sp)
          }

          // Delete
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .combinedClickable(onClick = {
                expenseToDelete = exp
                selectedExpenseForAction = null
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

    // Expense Details Dialog
    if (viewingExpenseDetails != null) {
      val e = viewingExpenseDetails!!
      AlertDialog(
        onDismissRequest = { viewingExpenseDetails = null },
        title = {
          Text(
            "ব্যয় বিবরণ (${BengaliHelper.formatDateDisplay(e.date)})",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ReportDetailRow("ফিড / খাবার:", BengaliHelper.formatCurrency(e.feedCost))
            ReportDetailRow("মেডিসিন ও ভ্যাকসিন:", BengaliHelper.formatCurrency(e.medicineCost))
            ReportDetailRow("স্টাফ বাজার:", BengaliHelper.formatCurrency(e.staffMarket))
            ReportDetailRow("স্টাফ বেতন / মজুরি:", BengaliHelper.formatCurrency(e.staffSalary))
            ReportDetailRow("গাড়ি মেরামত / যন্ত্র:", BengaliHelper.formatCurrency(e.vehicleRepair))
            ReportDetailRow("আসবাবপত্র / সম্পদ:", BengaliHelper.formatCurrency(e.assets))
            ReportDetailRow("বিদ্যুৎ বিল:", BengaliHelper.formatCurrency(e.electricityBill))
            ReportDetailRow("অন্যান্য খরচ:", BengaliHelper.formatCurrency(e.otherExpense))
            ReportDetailRow("সর্বমোট ব্যয়:", BengaliHelper.formatCurrency(e.totalExpense))
          }
        },
        confirmButton = {
          Button(
            onClick = { viewingExpenseDetails = null },
            colors = ButtonDefaults.buttonColors(containerColor = EggAmberSecondary)
          ) {
            Text("ঠিক আছে")
          }
        }
      )
    }

    // Delete Confirmation Dialog
    if (expenseToDelete != null) {
      AlertDialog(
        onDismissRequest = { expenseToDelete = null },
        title = { Text("ব্যয় মুছে ফেলার নিশ্চিতকরণ", fontWeight = FontWeight.Bold) },
        text = {
          Text(
            "আপনি কি নিশ্চিত যে ${BengaliHelper.formatDateDisplay(expenseToDelete!!.date)} তারিখের ব্যয়ের হিসাবটি মুছে ফেলতে চান?",
            fontSize = 14.sp
          )
        },
        confirmButton = {
          Button(
            onClick = {
              onDeleteExpense(expenseToDelete!!.id)
              expenseToDelete = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
          ) {
            Text("হ্যাঁ, মুছুন")
          }
        },
        dismissButton = {
          TextButton(onClick = { expenseToDelete = null }) {
            Text("বাতিল")
          }
        }
      )
    }
  }
}

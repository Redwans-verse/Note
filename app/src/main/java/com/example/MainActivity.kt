package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.KaziAgroViewModel
import com.example.ui.components.AddEditDailyReportDialog
import com.example.ui.components.AddEditExpenseDialog
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DailyReportScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MonthlyExpenseScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.FarmGreenPrimary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  private val viewModel: KaziAgroViewModel by viewModels()

  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    setContent {
      val uiState by viewModel.uiState.collectAsStateWithLifecycle()
      val context = LocalContext.current
      val snackbarHostState = remember { SnackbarHostState() }

      var isSplashDone by remember { mutableStateOf(false) }

      LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
          Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
          viewModel.clearToast()
        }
      }

      LaunchedEffect(uiState.generalError) {
        uiState.generalError?.let {
          snackbarHostState.showSnackbar(it)
          viewModel.clearToast()
        }
      }

      MyApplicationTheme(darkTheme = uiState.isDarkTheme) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          if (!isSplashDone) {
            SplashScreen(
              onAnimationFinished = { isSplashDone = true }
            )
          } else if (!uiState.isAuthenticated) {
            AuthScreen(
              isLoading = uiState.isAuthLoading,
              errorMessage = uiState.authError,
              onLogin = { email, pass -> viewModel.login(email, pass) },
              onRegister = { email, pass, name, farmName, mobile, addr ->
                viewModel.register(email, pass, name, farmName, mobile, addr)
              },
              onResetPassword = { email -> viewModel.sendPasswordReset(email) }
            )
          } else {
            // Main Farm Application Scaffold
            Scaffold(
              snackbarHost = { SnackbarHost(snackbarHostState) },
              topBar = {
                CenterAlignedTopAppBar(
                  title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(
                        modifier = Modifier
                          .size(34.dp)
                          .clip(CircleShape)
                          .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                      ) {
                        Image(
                          painter = painterResource(id = R.drawable.ic_kazi_logo),
                          contentDescription = null,
                          contentScale = ContentScale.Crop,
                          modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                        )
                      }
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = when (uiState.selectedTab) {
                          0 -> uiState.farmProfile.farmName
                          1 -> "দৈনিক রিপোর্ট রেজিস্টার"
                          2 -> "মাসিক ব্যয় রেজিস্টার"
                          3 -> "ব্যবসায়িক রিপোর্ট"
                          4 -> "ফার্ম সেটিংস"
                          else -> "কাজী এগ্রোটেক"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  },
                  colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = FarmGreenPrimary,
                    titleContentColor = Color.White
                  )
                )
              },
              bottomBar = {
                NavigationBar(
                  containerColor = MaterialTheme.colorScheme.surface,
                  tonalElevation = 6.dp,
                  modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                  NavigationBarItem(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.setTab(0) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "ড্যাশবোর্ড") },
                    label = { Text("ড্যাশবোর্ড", fontSize = 11.sp, fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                      selectedIconColor = FarmGreenPrimary,
                      selectedTextColor = FarmGreenPrimary,
                      indicatorColor = FarmGreenPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_dashboard")
                  )

                  NavigationBarItem(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.setTab(1) },
                    icon = { Icon(Icons.Default.EventNote, contentDescription = "দৈনিক") },
                    label = { Text("দৈনিক", fontSize = 11.sp, fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                      selectedIconColor = FarmGreenPrimary,
                      selectedTextColor = FarmGreenPrimary,
                      indicatorColor = FarmGreenPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_daily")
                  )

                  NavigationBarItem(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.setTab(2) },
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "ব্যয়") },
                    label = { Text("ব্যয়", fontSize = 11.sp, fontWeight = if (uiState.selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                      selectedIconColor = FarmGreenPrimary,
                      selectedTextColor = FarmGreenPrimary,
                      indicatorColor = FarmGreenPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_expenses")
                  )

                  NavigationBarItem(
                    selected = uiState.selectedTab == 3,
                    onClick = { viewModel.setTab(3) },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = "রিপোর্ট") },
                    label = { Text("রিপোর্ট", fontSize = 11.sp, fontWeight = if (uiState.selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                      selectedIconColor = FarmGreenPrimary,
                      selectedTextColor = FarmGreenPrimary,
                      indicatorColor = FarmGreenPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_reports")
                  )

                  NavigationBarItem(
                    selected = uiState.selectedTab == 4,
                    onClick = { viewModel.setTab(4) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "সেটিংস") },
                    label = { Text("সেটিংস", fontSize = 11.sp, fontWeight = if (uiState.selectedTab == 4) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                      selectedIconColor = FarmGreenPrimary,
                      selectedTextColor = FarmGreenPrimary,
                      indicatorColor = FarmGreenPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_settings")
                  )
                }
              }
            ) { paddingValues ->
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(paddingValues)
              ) {
                when (uiState.selectedTab) {
                  0 -> DashboardScreen(
                    uiState = uiState,
                    onOpenAddDailyReport = { viewModel.openAddDailyReport() },
                    onOpenAddExpense = { viewModel.openAddExpense() },
                    onNavigateToReports = { viewModel.setTab(3) },
                    onNavigateToDaily = { viewModel.setTab(1) },
                    onNavigateToExpenses = { viewModel.setTab(2) }
                  )
                  1 -> DailyReportScreen(
                    uiState = uiState,
                    onOpenAddReport = { viewModel.openAddDailyReport() },
                    onEditReport = { viewModel.openAddDailyReport(it) },
                    onDeleteReport = { viewModel.deleteDailyReport(it) },
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onMonthChange = { viewModel.setMonthFilter(it) },
                    onYearChange = { viewModel.setYearFilter(it) }
                  )
                  2 -> MonthlyExpenseScreen(
                    uiState = uiState,
                    onOpenAddExpense = { viewModel.openAddExpense() },
                    onEditExpense = { viewModel.openAddExpense(it) },
                    onDeleteExpense = { viewModel.deleteMonthlyExpense(it) },
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onMonthChange = { viewModel.setMonthFilter(it) },
                    onYearChange = { viewModel.setYearFilter(it) }
                  )
                  3 -> ReportsScreen(
                    uiState = uiState,
                    onMonthChange = { viewModel.setMonthFilter(it) },
                    onYearChange = { viewModel.setYearFilter(it) }
                  )
                  4 -> SettingsScreen(
                    uiState = uiState,
                    onUpdateFarmProfile = { name, owner, mob, addr ->
                      viewModel.updateFarmProfile(name, owner, mob, addr)
                    },
                    onTriggerCloudBackup = { viewModel.triggerCloudBackup() },
                    onToggleTheme = { viewModel.toggleTheme() },
                    onLogout = { viewModel.logout() },
                    onResetPassword = { viewModel.sendPasswordReset(it) }
                  )
                }
              }

              // Dialogs
              if (uiState.showAddDailyDialog) {
                AddEditDailyReportDialog(
                  editingReport = uiState.editingDailyReport,
                  previousStock = uiState.currentEggStock,
                  onDismiss = { viewModel.closeAddDailyReport() },
                  onSave = { date, currentBirds, deadBirds, eggProd, eggSold, eggPrice, medCost, remarks ->
                    viewModel.saveDailyReport(
                      date = date,
                      currentBirds = currentBirds,
                      deadBirds = deadBirds,
                      eggProduction = eggProd,
                      eggSold = eggSold,
                      eggPrice = eggPrice,
                      medicineCost = medCost,
                      remarks = remarks
                    )
                  }
                )
              }

              if (uiState.showAddExpenseDialog) {
                AddEditExpenseDialog(
                  editingExpense = uiState.editingExpense,
                  onDismiss = { viewModel.closeAddExpense() },
                  onSave = { date, feed, med, market, salary, vehicle, asset, elec, other ->
                    viewModel.saveMonthlyExpense(
                      date = date,
                      feedCost = feed,
                      medicineCost = med,
                      staffMarket = market,
                      staffSalary = salary,
                      vehicleRepair = vehicle,
                      assets = asset,
                      electricityBill = elec,
                      otherExpense = other
                    )
                  }
                )
              }
            }
          }
        }
      }
    }
  }
}

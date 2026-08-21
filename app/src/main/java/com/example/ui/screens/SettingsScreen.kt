package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.KaziAgroUiState
import com.example.ui.theme.EggAmberSecondary
import com.example.ui.theme.FarmGreenPrimary

@Composable
fun SettingsScreen(
  uiState: KaziAgroUiState,
  onUpdateFarmProfile: (farmName: String, ownerName: String, mobile: String, address: String) -> Unit,
  onTriggerCloudBackup: () -> Unit,
  onToggleTheme: () -> Unit,
  onLogout: () -> Unit,
  onResetPassword: (String) -> Unit
) {
  var farmName by remember(uiState.farmProfile) { mutableStateOf(uiState.farmProfile.farmName) }
  var ownerName by remember(uiState.farmProfile) { mutableStateOf(uiState.farmProfile.ownerName) }
  var mobileNumber by remember(uiState.farmProfile) { mutableStateOf(uiState.farmProfile.mobileNumber) }
  var address by remember(uiState.farmProfile) { mutableStateOf(uiState.farmProfile.address) }

  var isEditingProfile by remember { mutableStateOf(false) }
  var showLogoutConfirm by remember { mutableStateOf(false) }
  var showPasswordResetConfirm by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp)
      .testTag("settings_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Spacer(modifier = Modifier.height(8.dp))

    // Profile Card Header
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(FarmGreenPrimary.copy(alpha = 0.1f)),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_kazi_logo),
            contentDescription = "ফার্ম লোগো",
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape)
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = uiState.farmProfile.farmName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "প্রশাসক: ${uiState.farmProfile.ownerName}",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "ইমেইল: ${uiState.userProfile?.email.orEmpty()}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
          )
        }
      }
    }

    // Farm Information Section
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "ফার্ম ও যোগাযোগের তথ্য",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
          if (!isEditingProfile) {
            TextButton(onClick = { isEditingProfile = true }) {
              Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = FarmGreenPrimary)
              Spacer(modifier = Modifier.width(4.dp))
              Text("পরিবর্তন", color = FarmGreenPrimary, fontSize = 13.sp)
            }
          }
        }

        if (isEditingProfile) {
          OutlinedTextField(
            value = farmName,
            onValueChange = { farmName = it },
            label = { Text("ফার্মের নাম") },
            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = FarmGreenPrimary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmGreenPrimary)
          )

          OutlinedTextField(
            value = ownerName,
            onValueChange = { ownerName = it },
            label = { Text("মালিকের নাম") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = FarmGreenPrimary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmGreenPrimary)
          )

          OutlinedTextField(
            value = mobileNumber,
            onValueChange = { mobileNumber = it },
            label = { Text("মোবাইল নম্বর") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FarmGreenPrimary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmGreenPrimary)
          )

          OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("ফার্মের ঠিকানা") },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = FarmGreenPrimary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FarmGreenPrimary)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            OutlinedButton(onClick = { isEditingProfile = false }) {
              Text("বাতিল")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                onUpdateFarmProfile(farmName, ownerName, mobileNumber, address)
                isEditingProfile = false
              },
              colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
            ) {
              Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("সেভ করুন")
            }
          }
        } else {
          SettingInfoRow(Icons.Default.Home, "ফার্মের নাম", uiState.farmProfile.farmName)
          SettingInfoRow(Icons.Default.Person, "মালিকের নাম", uiState.farmProfile.ownerName)
          SettingInfoRow(Icons.Default.Phone, "মোবাইল নম্বর", uiState.farmProfile.mobileNumber)
          SettingInfoRow(Icons.Default.LocationOn, "ঠিকানা", uiState.farmProfile.address)
        }
      }
    }

    // Cloud Database & Backup Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CloudDone, contentDescription = null, tint = FarmGreenPrimary)
          Spacer(modifier = Modifier.width(8.dp))
          Text("ফায়ারবেস ক্লাউড স্ট্যাটাস", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        Text(
          "সমস্ত ডাটা সরাসরি Google Cloud Firebase Realtime Database-এ সিঙ্ক হচ্ছে। কোনো লোকাল ডাটাবেজ ব্যবহার করা হচ্ছে না।",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
          onClick = onTriggerCloudBackup,
          enabled = !uiState.isBackingUp,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
        ) {
          if (uiState.isBackingUp) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
          } else {
            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("ক্লাউড ব্যাকআপ নিশ্চিত করুন", fontSize = 14.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // App Preferences Card (Theme & Password)
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("অ্যাপ সেটিংস", fontSize = 15.sp, fontWeight = FontWeight.Bold)

        // Dark Theme Switch
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              if (uiState.isDarkTheme) Icons.Default.Brightness4 else Icons.Default.Brightness7,
              contentDescription = null,
              tint = EggAmberSecondary
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("ডার্ক মোড (Dark Theme)", fontSize = 14.sp)
          }
          Switch(
            checked = uiState.isDarkTheme,
            onCheckedChange = { onToggleTheme() },
            colors = SwitchDefaults.colors(checkedThumbColor = FarmGreenPrimary)
          )
        }

        // Change Password
        OutlinedButton(
          onClick = { showPasswordResetConfirm = true },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("পাসওয়ার্ড পরিবর্তন লিংক পাঠান")
        }
      }
    }

    // Logout Button
    Button(
      onClick = { showLogoutConfirm = true },
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .testTag("logout_button"),
      shape = RoundedCornerShape(14.dp),
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
    ) {
      Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.White)
      Spacer(modifier = Modifier.width(8.dp))
      Text("লগআউট করুন", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }

    Spacer(modifier = Modifier.height(30.dp))
  }

  // Logout Confirm Dialog
  if (showLogoutConfirm) {
    AlertDialog(
      onDismissRequest = { showLogoutConfirm = false },
      title = { Text("লগআউট নিশ্চিতকরণ", fontWeight = FontWeight.Bold) },
      text = { Text("আপনি কি নিশ্চিত যে কাজী এগ্রোটেক অ্যাপ থেকে লগআউট করতে চান?") },
      confirmButton = {
        Button(
          onClick = {
            showLogoutConfirm = false
            onLogout()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
        ) {
          Text("লগআউট")
        }
      },
      dismissButton = {
        TextButton(onClick = { showLogoutConfirm = false }) {
          Text("বাতিল")
        }
      }
    )
  }

  // Password Reset Dialog
  if (showPasswordResetConfirm) {
    val email = uiState.userProfile?.email ?: ""
    AlertDialog(
      onDismissRequest = { showPasswordResetConfirm = false },
      title = { Text("পাসওয়ার্ড পরিবর্তন", fontWeight = FontWeight.Bold) },
      text = { Text("আপনার ইমেইল ($email)-এ পাসওয়ার্ড রিসেট লিংক পাঠানো হবে। আপনি কি এগিয়ে যেতে চান?") },
      confirmButton = {
        Button(
          onClick = {
            showPasswordResetConfirm = false
            onResetPassword(email)
          },
          colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
        ) {
          Text("লিংক পাঠান")
        }
      },
      dismissButton = {
        TextButton(onClick = { showPasswordResetConfirm = false }) {
          Text("বাতিল")
        }
      }
    )
  }
}

@Composable
fun SettingInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(modifier = Modifier.width(10.dp))
    Column {
      Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Text(value.ifBlank { "-" }, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
  }
}

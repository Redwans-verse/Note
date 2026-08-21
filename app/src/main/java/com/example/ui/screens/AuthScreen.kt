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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.FarmGreenPrimary

@Composable
fun AuthScreen(
  isLoading: Boolean,
  errorMessage: String?,
  onLogin: (email: String, pass: String) -> Unit,
  onRegister: (email: String, pass: String, name: String, farmName: String, mobile: String, address: String) -> Unit,
  onResetPassword: (email: String) -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register

  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var isPasswordVisible by remember { mutableStateOf(false) }

  // Registration Fields
  var ownerName by remember { mutableStateOf("") }
  var farmName by remember { mutableStateOf("কাজী এগ্রোটেক") }
  var mobileNumber by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }

  var showForgotDialog by remember { mutableStateOf(false) }
  var resetEmail by remember { mutableStateOf("") }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("auth_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // Header Brand
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(CircleShape)
          .background(FarmGreenPrimary.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_kazi_logo),
          contentDescription = "কাজী এগ্রোটেক",
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "কাজী এগ্রোটেক",
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = FarmGreenPrimary
      )

      Text(
        text = "লেয়ার পোল্ট্রি ফার্ম ম্যানেজমেন্ট সিস্টেম",
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Card Container
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          // Tab Selection
          TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = FarmGreenPrimary
          ) {
            Tab(
              selected = selectedTab == 0,
              onClick = { selectedTab = 0 },
              text = {
                Text(
                  "লগইন",
                  fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 16.sp
                )
              }
            )
            Tab(
              selected = selectedTab == 1,
              onClick = { selectedTab = 1 },
              text = {
                Text(
                  "নতুন একাউন্ট",
                  fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 16.sp
                )
              }
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          if (errorMessage != null) {
            Card(
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
            ) {
              Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontSize = 13.sp,
                modifier = Modifier.padding(12.dp)
              )
            }
          }

          if (selectedTab == 0) {
            // LOGIN FORM
            OutlinedTextField(
              value = email,
              onValueChange = { email = it },
              label = { Text("ইমেইল বা ইউজারনেম") },
              leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = FarmGreenPrimary) },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("login_email_input"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FarmGreenPrimary,
                focusedLabelColor = FarmGreenPrimary
              )
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
              value = password,
              onValueChange = { password = it },
              label = { Text("পাসওয়ার্ড") },
              leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = FarmGreenPrimary) },
              trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                  Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                  )
                }
              },
              visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("login_password_input"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FarmGreenPrimary,
                focusedLabelColor = FarmGreenPrimary
              )
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              TextButton(onClick = {
                resetEmail = email
                showForgotDialog = true
              }) {
                Text(
                  "পাসওয়ার্ড ভুলে গেছেন?",
                  fontSize = 13.sp,
                  color = FarmGreenPrimary
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
              onClick = { onLogin(email, password) },
              enabled = !isLoading,
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("login_submit_button"),
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
            ) {
              if (isLoading) {
                CircularProgressIndicator(
                  color = Color.White,
                  strokeWidth = 2.dp,
                  modifier = Modifier.size(22.dp)
                )
              } else {
                Text("লগইন করুন", fontSize = 16.sp, fontWeight = FontWeight.Bold)
              }
            }
          } else {
            // REGISTRATION FORM
            OutlinedTextField(
              value = ownerName,
              onValueChange = { ownerName = it },
              label = { Text("মালিকের নাম *") },
              leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = FarmGreenPrimary) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FarmGreenPrimary,
                focusedLabelColor = FarmGreenPrimary
              )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = farmName,
              onValueChange = { farmName = it },
              label = { Text("ফার্মের নাম *") },
              leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = FarmGreenPrimary) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FarmGreenPrimary,
                focusedLabelColor = FarmGreenPrimary
              )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = mobileNumber,
              onValueChange = { mobileNumber = it },
              label = { Text("মোবাইল নম্বর") },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FarmGreenPrimary) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FarmGreenPrimary,
                focusedLabelColor = FarmGreenPrimary
              )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = address,
              onValueChange = { address = it },
              label = { Text("ফার্মের ঠিকানা") },
              leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = FarmGreenPrimary) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FarmGreenPrimary,
                focusedLabelColor = FarmGreenPrimary
              )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = email,
              onValueChange = { email = it },
              label = { Text("ইমেইল ঠিকানা *") },
              leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = FarmGreenPrimary) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FarmGreenPrimary,
                focusedLabelColor = FarmGreenPrimary
              )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = password,
              onValueChange = { password = it },
              label = { Text("পাসওয়ার্ড (কমপক্ষে ৬ অক্ষর) *") },
              leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = FarmGreenPrimary) },
              visualTransformation = PasswordVisualTransformation(),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FarmGreenPrimary,
                focusedLabelColor = FarmGreenPrimary
              )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
              onClick = {
                onRegister(email, password, ownerName, farmName, mobileNumber, address)
              },
              enabled = !isLoading,
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("register_submit_button"),
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
            ) {
              if (isLoading) {
                CircularProgressIndicator(
                  color = Color.White,
                  strokeWidth = 2.dp,
                  modifier = Modifier.size(22.dp)
                )
              } else {
                Text("একাউন্ট তৈরি করুন", fontSize = 16.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
      Text(
        text = "ফায়ারবেস ক্লাউড সুরক্ষিত ডাটাবেজ",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        textAlign = TextAlign.Center
      )
    }

    // Password Reset Dialog
    if (showForgotDialog) {
      androidx.compose.material3.AlertDialog(
        onDismissRequest = { showForgotDialog = false },
        title = { Text("পাসওয়ার্ড রিসেট", fontWeight = FontWeight.Bold) },
        text = {
          Column {
            Text(
              "আপনার রেজিস্টার্ড ইমেইল লিখুন। পাসওয়ার্ড রিসেট করার লিংক পাঠানো হবে।",
              fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
              value = resetEmail,
              onValueChange = { resetEmail = it },
              label = { Text("ইমেইল") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              onResetPassword(resetEmail)
              showForgotDialog = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
          ) {
            Text("লিংক পাঠান")
          }
        },
        dismissButton = {
          TextButton(onClick = { showForgotDialog = false }) {
            Text("বাতিল")
          }
        }
      )
    }
  }
}

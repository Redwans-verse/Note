package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.FarmGreenPrimary

@Composable
fun SplashScreen(
  onAnimationFinished: () -> Unit
) {
  val scale = remember { Animatable(0.6f) }

  LaunchedEffect(key1 = true) {
    scale.animateTo(
      targetValue = 1f,
      animationSpec = tween(
        durationMillis = 800,
        easing = FastOutSlowInEasing
      )
    )
    kotlinx.coroutines.delay(600)
    onAnimationFinished()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(FarmGreenPrimary)
      .testTag("splash_screen"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
        .padding(24.dp)
        .scale(scale.value)
    ) {
      Box(
        modifier = Modifier
          .size(110.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_kazi_logo),
          contentDescription = "কাজী এগ্রোটেক লোগো",
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "কাজী এগ্রোটেক",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Layer Poultry Farm Management System",
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = Color.White.copy(alpha = 0.85f),
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(40.dp))

      CircularProgressIndicator(
        color = Color.White,
        modifier = Modifier.size(32.dp),
        strokeWidth = 3.dp
      )
    }
  }
}

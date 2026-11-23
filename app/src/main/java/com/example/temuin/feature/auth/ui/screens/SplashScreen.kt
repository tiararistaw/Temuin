package com.example.temuin.feature.auth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temuin.feature.auth.ui.viewmodel.AuthViewModel
import com.example.temuin.feature.review.ui.components.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: AuthViewModel,
    onNavigateHome: () -> Unit,
    onNavigateLogin: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        delay(1200)
        if (currentUser != null) {
            onNavigateHome()
        } else {
            onNavigateLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TemuinTeal),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                modifier = Modifier.size(96.dp),
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("T", color = TemuinTeal, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = "Temuin",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontSize = 36.sp
            )

            Text(
                text = "Platform Lost & Found Kampus UB",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(Modifier.height(24.dp))

            CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
        }
    }
}

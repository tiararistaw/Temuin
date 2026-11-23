package com.example.temuin.feature.auth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temuin.feature.auth.ui.viewmodel.AuthViewModel
import com.example.temuin.feature.review.ui.components.*

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateRegister: () -> Unit
) {
    var email by remember { mutableStateOf("trisha.malina@student.ub.ac.id") }
    var password by remember { mutableStateOf("trisha123") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TemuinBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = TemuinTeal,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("T", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Selamat Datang di Temuin",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TemuinDark
            )
            Text(
                text = "Masuk untuk melanjutkan bantu warga kampus.",
                style = MaterialTheme.typography.bodyMedium,
                color = TemuinMuted
            )

            Spacer(Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = null },
                        label = { Text("Email Kampus") },
                        leadingIcon = { Icon(Icons.Outlined.Mail, null, tint = TemuinTeal) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TemuinTeal,
                            unfocusedBorderColor = TemuinBorder
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = TemuinTeal) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TemuinTeal,
                            unfocusedBorderColor = TemuinBorder
                        )
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFC62828),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Email dan password tidak boleh kosong."
                                return@Button
                            }
                            isLoading = true
                            val result = viewModel.login(email, password)
                            isLoading = false
                            if (result.isSuccess) {
                                onLoginSuccess()
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Gagal masuk."
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TemuinTeal),
                        enabled = !isLoading
                    ) {
                        Text("Masuk", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Belum punya akun?", color = TemuinMuted, fontSize = 13.sp)
                TextButton(onClick = onNavigateRegister) {
                    Text("Daftar sekarang", color = TemuinTeal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

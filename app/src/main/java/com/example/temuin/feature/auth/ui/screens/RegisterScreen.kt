
package com.example.temuin.feature.auth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
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
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val registerResult by viewModel.registerResult.collectAsState()

    LaunchedEffect(registerResult) {
        val result = registerResult ?: return@LaunchedEffect

        isLoading = false

        result.onSuccess {
            successMessage =
                "Registrasi berhasil! Silakan masuk menggunakan akun kamu."
            errorMessage = null
        }.onFailure { error ->
            errorMessage = error.message ?: "Gagal mendaftar."
        }

        viewModel.clearRegisterResult()
    }

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
                    Text(
                        "T",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Buat Akun Baru",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TemuinDark
            )

            Text(
                text = "Daftar untuk bergabung dengan komunitas Temuin Kampus UB.",
                style = MaterialTheme.typography.bodyMedium,
                color = TemuinMuted
            )

            Spacer(Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            errorMessage = null
                            successMessage = null
                        },
                        label = { Text("Nama Lengkap") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Person, null, tint = TemuinTeal)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TemuinTeal,
                            unfocusedBorderColor = TemuinBorder
                        )
                    )

                    OutlinedTextField(
                        value = nim,
                        onValueChange = {
                            nim = it
                            errorMessage = null
                            successMessage = null
                        },
                        label = { Text("NIM Mahasiswa") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Badge, null, tint = TemuinTeal)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TemuinTeal,
                            unfocusedBorderColor = TemuinBorder
                        )
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                            successMessage = null
                        },
                        label = { Text("Email Kampus (@student.ub.ac.id)") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Mail, null, tint = TemuinTeal)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TemuinTeal,
                            unfocusedBorderColor = TemuinBorder
                        )
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            errorMessage = null
                            successMessage = null
                        },
                        label = { Text("Nomor HP / WhatsApp") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Call, null, tint = TemuinTeal)
                        },
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
                        onValueChange = {
                            password = it
                            errorMessage = null
                            successMessage = null
                        },
                        label = { Text("Password (min. 6 karakter)") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, null, tint = TemuinTeal)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TemuinTeal,
                            unfocusedBorderColor = TemuinBorder
                        )
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            errorMessage = null
                            successMessage = null
                        },
                        label = { Text("Konfirmasi Password") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, null, tint = TemuinTeal)
                        },
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

                    if (successMessage != null) {
                        Text(
                            text = successMessage!!,
                            color = Color(0xFF2E7D32),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Button(
                        onClick = {
                            if (
                                name.isBlank() ||
                                nim.isBlank() ||
                                email.isBlank() ||
                                phone.isBlank() ||
                                password.isBlank() ||
                                confirmPassword.isBlank()
                            ) {
                                errorMessage = "Semua field wajib diisi."
                                return@Button
                            }

                            val trimmedEmail = email.trim()

                            if (
                                !trimmedEmail.contains("@") ||
                                !trimmedEmail.endsWith(
                                    "@student.ub.ac.id",
                                    ignoreCase = true
                                )
                            ) {
                                errorMessage =
                                    "Gunakan email mahasiswa UB (@student.ub.ac.id)."
                                return@Button
                            }

                            if (password.length < 6) {
                                errorMessage =
                                    "Password minimal 6 karakter."
                                return@Button
                            }

                            if (password != confirmPassword) {
                                errorMessage =
                                    "Konfirmasi password tidak cocok."
                                return@Button
                            }

                            errorMessage = null
                            successMessage = null
                            isLoading = true

                            viewModel.clearRegisterResult()

                            viewModel.register(
                                name = name,
                                email = trimmedEmail,
                                password = password,
                                nim = nim,
                                phone = phone
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TemuinTeal
                        ),
                        enabled = !isLoading && successMessage == null
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Daftar Akun",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Sudah punya akun?",
                    color = TemuinMuted,
                    fontSize = 13.sp
                )

                TextButton(onClick = onNavigateLogin) {
                    Text(
                        "Masuk",
                        color = TemuinTeal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

package com.example.temuin.feature.profile.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.temuin.feature.profile.ui.viewmodel.ProfileViewModel
import com.example.temuin.feature.profile.util.readAndCompress
import com.example.temuin.feature.review.ui.components.*
import androidx.compose.ui.graphics.Color

@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    userId: String,
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val profile = state.profile

    var name by remember(profile) { mutableStateOf(profile?.fullName ?: "") }
    var nim by remember(profile) { mutableStateOf(profile?.nim ?: "") }
    var study by remember(profile) { mutableStateOf(profile?.studyProgram ?: "") }
    var phone by remember(profile) { mutableStateOf(profile?.phone ?: "") }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            readAndCompress(context, it)?.let { bytes -> viewModel.uploadAvatar(userId, bytes) }
        }
    }

    LaunchedEffect(userId) { if (profile == null) viewModel.load(userId) }
    LaunchedEffect(state.saved) {
        if (state.saved) {
            viewModel.consumeSaved()
            onDone()
        }
    }

    Column(Modifier.fillMaxSize().background(TemuinBackground)) {
        ReviewTopBar("Edit Profil", onBack)

        if (profile == null) {
            Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = TemuinTeal) }
            return@Column
        }

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(Modifier.clickable {
                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) { ProfileAvatar(profile, 96) }

            TextButton(onClick = {
                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) { Text("Ganti Foto", color = TemuinTeal) }

            ProfileField("Nama Lengkap", name) { name = it }
            ProfileField("NIM", nim) { nim = it }
            ProfileField("Program Studi", study) { study = it }
            ProfileField("No. WhatsApp", phone) { phone = it }

            state.error?.let { Text(it, color = Color(0xFFB84B4B), style = MaterialTheme.typography.bodySmall) }
        }

    }
    Surface(color = androidx.compose.ui.graphics.Color.White, shadowElevation = 8.dp)  {
        Button(
            onClick = { viewModel.save(name, nim, study, phone) },
            enabled = !state.isLoading && name.isNotBlank(),
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = TemuinTeal)
        ) { Text("Simpan Perubahan") }
    }
}

@Composable
private fun ProfileField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TemuinTeal,
            unfocusedBorderColor = TemuinBorder
        )
    )
}
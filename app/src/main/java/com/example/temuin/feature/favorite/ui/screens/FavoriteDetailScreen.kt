package com.example.temuin.feature.favorite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.temuin.feature.favorite.data.model.ReportType
import com.example.temuin.feature.favorite.ui.components.TemuinLostRed
import com.example.temuin.feature.favorite.ui.viewmodel.FavoriteViewModel
import com.example.temuin.feature.review.ui.components.ReviewTopBar
import com.example.temuin.feature.review.ui.components.TemuinBackground
import com.example.temuin.feature.review.ui.components.TemuinMuted
import com.example.temuin.feature.review.ui.components.TemuinPaleBlue
import com.example.temuin.feature.review.ui.components.TemuinTeal

@Composable
fun FavoriteDetailScreen(
    viewModel: FavoriteViewModel,
    userId: String,
    reportId: String,
    onBack: () -> Unit,
    onClaim: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(userId) { if (state.items.isEmpty()) viewModel.load(userId) }

    val item = state.items.find { it.reportId == reportId }

    Column(Modifier.fillMaxSize().background(TemuinBackground)) {
        ReviewTopBar("Detail Favorit", onBack)

        if (item == null) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("Laporan tidak ditemukan.", color = TemuinMuted)
            }
            return@Column
        }

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                Modifier.fillMaxWidth().height(200.dp)
                    .background(TemuinPaleBlue, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (item.photoUrl != null) {
                    AsyncImage(
                        model = item.photoUrl, contentDescription = item.itemName,
                        modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Image, null, tint = TemuinMuted, modifier = Modifier.size(48.dp))
                }
            }

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = if (item.type == ReportType.FOUND) TemuinTeal else TemuinLostRed,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            item.type.label, color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Text(item.itemName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    InfoRow("Kategori", item.category)
                    InfoRow("Lokasi", item.location)
                    InfoRow("Waktu", item.timeLabel)
                    InfoRow("Pelapor", item.reporterName)
                    InfoRow("No. Telepon", if (item.contact.isNotBlank()) item.contact else "-")
                    HorizontalDivider(color = Color(0xFFEFEFEF))
                    Text("Deskripsi", fontWeight = FontWeight.SemiBold)
                    Text(item.description, color = TemuinMuted)
                }
            }
        }

        Surface(color = Color.White, shadowElevation = 8.dp) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (item.type == ReportType.FOUND) {
                    Button(
                        onClick = { onClaim(item.reportId) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = TemuinTeal)
                    ) { Text("Ajukan Klaim") }
                }
                OutlinedButton(
                    onClick = {
                        viewModel.remove(userId, item.reportId)
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(50)
                ) { Text("Hapus dari Favorit", color = TemuinLostRed) }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row {
        Text(label, color = TemuinMuted, modifier = Modifier.width(80.dp), style = MaterialTheme.typography.bodySmall)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}
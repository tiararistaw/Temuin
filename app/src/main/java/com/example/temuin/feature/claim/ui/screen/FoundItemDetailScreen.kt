package com.example.temuin.feature.claim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temuin.feature.review.ui.components.*

@Composable
fun FoundItemDetailScreen(
    itemName: String = "Kunci Motor Honda Vario & Gantungan Dino",
    finderName: String = "Rian Pratama",
    finderPhone: String = "0812-9844-0000",
    onBack: () -> Unit = {},
    onClaimClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TemuinBackground)
    ) {
        TemuinTopBar(title = "Detail Barang Temuan", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = TemuinLavender)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("Foto Barang Temuan", color = TemuinTeal, fontWeight = FontWeight.Bold)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(itemName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TemuinDark)
                    Text("Kategori: Kendaraan • Ditemukan di Gedung F FILKOM", fontSize = 13.sp, color = TemuinMuted)
                    HorizontalDivider(color = TemuinBorder)
                    Text("Informasi Penemu", fontWeight = FontWeight.SemiBold, color = TemuinDark)
                    Text("Penemu: $finderName", fontSize = 13.sp, color = TemuinMuted)
                    Text("No. HP / WA Penemu: $finderPhone", fontSize = 13.sp, color = TemuinMuted, fontWeight = FontWeight.Medium)
                    HorizontalDivider(color = TemuinBorder)
                    Text("Deskripsi Penemuan", fontWeight = FontWeight.SemiBold, color = TemuinDark)
                    Text("Ditemukan di atas meja gazebo lantai 2 oleh petugas kebersihan.", fontSize = 13.sp, color = TemuinMuted)
                }
            }

            Button(
                onClick = onClaimClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = TemuinTeal),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Ajukan Klaim Barang Ini", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

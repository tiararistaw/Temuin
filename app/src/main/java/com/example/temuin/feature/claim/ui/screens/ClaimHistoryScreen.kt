package com.example.temuin.feature.claim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temuin.feature.review.ui.components.*

private data class HistoryClaim(
    val id: String,
    val itemName: String,
    val date: String,
    val location: String,
    val status: String,
    val footerMessage: String
)

@Composable
fun ClaimHistoryScreen(
    onClaimClick: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var selectedTab by remember {
        mutableStateOf("Semua")
    }

    val claims = listOf(
        HistoryClaim(
            "CLM-2041",
            "Dompet Kulit Hitam",
            "5 Nov 2026",
            "Perpustakaan FILKOM",
            "Menunggu",
            "Pengajuan berhasil dikirim"
        ),
        HistoryClaim(
            "CLM-2038",
            "Tumbler Biru",
            "3 Nov 2026",
            "Kantin Baru FILKOM",
            "Diproses",
            "Menunggu tanggapan pelapor temuan"
        ),
        HistoryClaim(
            "CLM-2029",
            "Earbuds Putih",
            "1 Nov 2026",
            "Gedung F Lantai 3",
            "Selesai",
            "Barang telah dikembalikan"
        ),
        HistoryClaim(
            "CLM-2025",
            "Kunci Motor Vario",
            "28 Okt 2026",
            "Gazebo Gedung F",
            "Ditolak",
            "Pengajuan ditolak oleh pelapor"
        )
    )

    val filteredClaims = if (selectedTab == "Semua") {
        claims
    } else {
        claims.filter { it.status == selectedTab }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TemuinBackground)
    ) {
        TemuinTopBar(title = "Riwayat Klaim", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            Text(
                text = "Pantau perkembangan pengajuan klaim barangmu.",
                fontSize = 13.sp,
                color = TemuinMuted
            )

            // Summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = TemuinTeal
                ),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "TOTAL PENGAJUAN KLAIM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "${claims.size}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SummaryItem(
                            "Menunggu",
                            claims.count { it.status == "Menunggu" }
                        )

                        SummaryItem(
                            "Diproses",
                            claims.count { it.status == "Diproses" }
                        )

                        SummaryItem(
                            "Selesai",
                            claims.count { it.status == "Selesai" }
                        )
                    }
                }
            }

            // Claim list title
            Text(
                text = "Daftar Pengajuan",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TemuinDark
            )

            // Status tabs rendered horizontally (LazyRow).
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf("Semua", "Menunggu", "Diproses", "Selesai", "Ditolak")) { tab ->
                    TemuinFilterChip(
                        label = tab,
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab }
                    )
                }
            }

            // Claim list
            if (filteredClaims.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada pengajuan klaim.",
                        color = TemuinMuted,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(filteredClaims) { claim ->
                        ClaimHistoryCard(
                            claim = claim,
                            onClick = {
                                onClaimClick(claim.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

// Summary item
@Composable
private fun SummaryItem(
    title: String,
    count: Int
) {
    Column {
        Text(
            text = "$count",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Text(
            text = title,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 10.sp
        )
    }
}

// Claim history card
@Composable
private fun ClaimHistoryCard(
    claim: HistoryClaim,
    onClick: () -> Unit
) {
    val statusColor = when (claim.status) {
        "Menunggu" -> Color(0xFFB7791F)
        "Diproses" -> Color(0xFF4569B0)
        "Selesai" -> TemuinTeal
        else -> Color(0xFFB54747)
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${claim.id}",
                    color = TemuinMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = claim.status.uppercase(),
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = claim.itemName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TemuinDark
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Diajukan ${claim.date}",
                fontSize = 12.sp,
                color = TemuinMuted
            )

            Spacer(Modifier.height(12.dp))

            HorizontalDivider(color = TemuinLavender)

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(TemuinTeal, CircleShape)
                    )
                    Text(
                        text = claim.footerMessage,
                        color = TemuinDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = "→",
                    color = TemuinTeal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

package com.example.temuin.feature.claim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temuin.feature.claim.data.model.Claim
import com.example.temuin.feature.review.ui.components.*

@Composable
fun ClaimDetailScreen(
    claim: Claim? = null,
    itemName: String = claim?.itemName ?: "Barang Temuan",
    claimDate: String = claim?.claimDate ?: "Hari ini",
    status: String = claim?.status ?: "Menunggu",
    description: String = claim?.claimDescription ?: "-",
    onConfirmReceipt: () -> Unit = {},
    onRate: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TemuinBackground)
    ) {
        TemuinTopBar(title = "Progres & Detail Klaim", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Card (Stretch Full Width)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("STATUS TERKINI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TemuinTeal)
                        Box(Modifier.size(8.dp).background(if (status == "Ditolak") Color(0xFFB54747) else TemuinTeal, CircleShape))
                    }
                    Text(
                        when (status) {
                            "Menunggu" -> "Menunggu Peninjauan"
                            "Diproses" -> "Klaim Disetujui"
                            "Selesai" -> "Selesai"
                            "Ditolak" -> "Pengajuan Ditolak"
                            else -> status
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TemuinDark
                    )
                    Text(
                        when (status) {
                            "Menunggu" -> "Pengajuan klaimmu telah terkirim dan sedang ditinjau oleh penemu barang."
                            "Diproses" -> "Klaim telah disetujui! Lanjutkan serah terima barang secara langsung lalu konfirmasi."
                            "Selesai" -> "Proses serah terima selesai. Terima kasih telah menggunakan Temuin."
                            "Ditolak" -> "Pengajuan klaim belum dapat disetujui oleh pelapor temuan."
                            else -> "Status pengajuan klaim terbaru."
                        },
                        fontSize = 12.sp,
                        color = TemuinMuted
                    )
                }
            }

            // Perjalanan Klaim Timeline Card (Stretch Full Width)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Perjalanan Klaim", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TemuinDark)

                    val steps = listOf(
                        Triple(
                            "Pengajuan Klaim Dibuat",
                            "Diajukan $claimDate",
                            true
                        ),
                        Triple(
                            "Menunggu Peninjauan Pelapor",
                            if (status == "Menunggu") "Status saat ini" else "Selesai ditinjau",
                            true
                        ),
                        Triple(
                            "Serah Terima Disepakati",
                            when (status) {
                                "Diproses", "Selesai" -> "Disepakati oleh penemu"
                                "Ditolak" -> "Pengajuan ditolak"
                                else -> "Menunggu tahap sebelumnya"
                            },
                            status == "Diproses" || status == "Selesai"
                        ),
                        Triple(
                            "Selesai",
                            if (status == "Selesai") "Barang diterima & dikembalikan" else "Menunggu tahap sebelumnya",
                            status == "Selesai"
                        )
                    )

                    steps.forEachIndexed { index, step ->
                        Row(verticalAlignment = Alignment.Top) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (step.third) TemuinTeal else Color(0xFFE5E7EB),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (step.third) {
                                            Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        } else {
                                            Text("${index + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TemuinMuted)
                                        }
                                    }
                                }
                                if (index != steps.lastIndex) {
                                    Box(
                                        Modifier
                                            .width(2.dp)
                                            .height(34.dp)
                                            .background(if (step.third) TemuinTeal else Color(0xFFE5E7EB))
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.padding(top = 2.dp)) {
                                Text(
                                    step.first,
                                    fontWeight = if (step.third) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (step.third) TemuinDark else TemuinMuted,
                                    fontSize = 14.sp
                                )
                                Text(step.second, fontSize = 11.sp, color = TemuinMuted)
                            }
                        }
                    }
                }
            }

            // Alasan Penolakan (if rejected)
            if (status == "Ditolak") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE8E7)),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Alasan Penolakan", fontWeight = FontWeight.Bold, color = Color(0xFFBA1A1A), fontSize = 14.sp)
                        Text(
                            claim?.rejectionNote.orEmpty().ifBlank { "Bukti kepemilikan belum sesuai dengan deskripsi barang." },
                            color = Color(0xFFBA1A1A),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Detail Informasi Card (Stretch Full Width)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Informasi Pengajuan Klaim",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TemuinDark
                    )
                    HorizontalDivider(color = TemuinBorder)
                    Text("ID Pengajuan: ${claim?.id ?: "CLM-2026-001"}", fontSize = 12.sp, color = TemuinMuted)
                    Text("Nama Barang: $itemName", fontWeight = FontWeight.SemiBold, color = TemuinDark, fontSize = 14.sp)
                    Text("Tanggal Pengajuan: $claimDate", fontSize = 13.sp, color = TemuinMuted)
                    Text("No. WhatsApp / Telepon: ${claim?.whatsapp.orEmpty().ifBlank { "-" }}", fontSize = 13.sp, color = TemuinMuted)
                    Text("Detail Bukti Kepemilikan:", fontWeight = FontWeight.SemiBold, color = TemuinDark, fontSize = 13.sp)
                    Surface(shape = RoundedCornerShape(12.dp), color = TemuinBackground, modifier = Modifier.fillMaxWidth()) {
                        Text(description, modifier = Modifier.padding(12.dp), fontSize = 12.sp, color = TemuinDark)
                    }
                    if (claim?.reportOwnerName?.isNotBlank() == true) {
                        Text("Pemilik Laporan Temuan: ${claim.reportOwnerName}", fontSize = 13.sp, color = TemuinMuted)
                    }
                }
            }

            // Action section when status == "Diproses"
            if (status == "Diproses") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("KONFIRMASI SERAH TERIMA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TemuinTeal)
                        Text("Jika barang sudah kamu terima dari penemu, silakan selesaikan pengajuan ini.", fontSize = 12.sp, color = TemuinMuted)
                        Button(
                            onClick = { showConfirmDialog = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TemuinTeal),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Konfirmasi Barang Diterima", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Completion section when status == "Selesai"
            if (status == "Selesai") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(shape = CircleShape, color = TemuinTeal, modifier = Modifier.size(32.dp)) {
                                Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(18.dp)) }
                            }
                            Column {
                                Text("PROSES SELESAI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TemuinTeal)
                                Text("Barang telah diserahkan", fontWeight = FontWeight.Bold, color = TemuinDark)
                            }
                        }
                        Text("Serah terima barang telah dikonfirmasi dan tersimpan di Riwayat Klaim.", fontSize = 12.sp, color = TemuinMuted)
                        Button(
                            onClick = onRate,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TemuinTeal),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Outlined.Star, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Beri Rating Pelapor", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Konfirmasi Penerimaan Barang") },
            text = { Text("Apakah kamu yakin sudah menerima barang ini dengan baik dari penemu?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    onConfirmReceipt()
                }) {
                    Text("Ya, Selesai", color = TemuinTeal, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

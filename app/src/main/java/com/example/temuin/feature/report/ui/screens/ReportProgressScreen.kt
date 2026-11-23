package com.example.temuin.feature.report.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temuin.feature.claim.ui.viewmodel.ClaimViewModel
import com.example.temuin.feature.report.data.model.Report
import com.example.temuin.feature.report.data.model.ReportStatus
import com.example.temuin.feature.report.data.model.ReportType
import com.example.temuin.feature.report.ui.viewmodel.ReportViewModel
import com.example.temuin.feature.review.ui.components.*
import com.example.temuin.feature.review.ui.viewmodel.ReviewViewModel

@Composable
fun StatusPill(status: String) {
    val color = when (status) {
        "Menunggu" -> Color(0xFF9A5B00)
        "Diproses" -> Color(0xFF4569B0)
        "Selesai" -> TemuinTeal
        else -> Color(0xFFB54747)
    }
    Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = .12f)) {
        Text(status.uppercase(), color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp))
    }
}

@Composable
fun ReportProgressScreen(
    viewModel: ReportViewModel,
    claimViewModel: ClaimViewModel,
    reviewViewModel: ReviewViewModel,
    reportId: String,
    ownerId: String,
    onBack: () -> Unit,
    onRate: (Report) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val report = state.reports.firstOrNull { it.id == reportId }
    val claims by claimViewModel.incomingClaims
    val reviewState by reviewViewModel.uiState.collectAsState()
    val alreadyReviewed = reviewState.writtenReviews.any { it.reportId == reportId }
    var showRejectDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var rejectionNote by remember { mutableStateOf("") }

    LaunchedEffect(reportId, ownerId) {
        claimViewModel.loadIncomingClaims(ownerId)
    }

    val incomingClaim = claims.firstOrNull { it.itemId == reportId }
    val status = report?.status
    val claimStatus = incomingClaim?.status

    Column(Modifier.fillMaxSize().background(TemuinBackground)) {
        TemuinTopBar(title = "Progres Laporan", onBack = onBack)

        if (report == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Laporan tidak ditemukan", color = TemuinMuted)
            }
            return@Column
        }

        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("STATUS TERKINI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TemuinTeal)
                        Box(Modifier.size(8.dp).background(TemuinTeal, CircleShape))
                    }
                    Text(status?.label ?: "Aktif", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TemuinDark)
                    Text(
                        when (status) {
                            ReportStatus.COMPLETED -> "Laporan selesai! Terima kasih telah membantu proses pengembalian barang."
                            ReportStatus.HANDOVER_PENDING -> "Klaim telah disetujui. Lanjutkan serah terima barang dengan pengaju klaim."
                            ReportStatus.CLAIM_PENDING -> "Ada pengajuan kepemilikan yang perlu kamu tinjau."
                            else -> if (report.type == ReportType.LOST) "Laporanmu aktif dan dapat dilihat komunitas." else "Laporan barang temuanmu aktif dan menunggu pengajuan klaim."
                        },
                        fontSize = 12.sp, color = TemuinMuted
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Perjalanan Laporan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TemuinDark)
                    val steps = if (report.type == ReportType.FOUND) {
                        listOf(
                            Triple("Laporan Dibuat", "1 November 2026 • 9.20 WIB", true),
                            Triple("Laporan Dipublikasikan", "2 November 2026 • 10.20 WIB", true),
                            Triple("Pengajuan Klaim Masuk", if (claimStatus != null) "Status saat ini" else "Menunggu pengaju klaim", claimStatus != null),
                            Triple("Serah Terima Disepakati", if (status == ReportStatus.HANDOVER_PENDING || status == ReportStatus.COMPLETED) "Disepakati" else "Menunggu tahap sebelumnya", status == ReportStatus.HANDOVER_PENDING || status == ReportStatus.COMPLETED),
                            Triple("Selesai", if (status == ReportStatus.COMPLETED) "Selesai" else "Menunggu tahap sebelumnya", status == ReportStatus.COMPLETED)
                        )
                    } else {
                        listOf(
                            Triple("Laporan Dibuat", "1 November 2026 • 9.20 WIB", true),
                            Triple("Laporan Dipublikasikan", "2 November 2026 • 10.20 WIB", true),
                            Triple("Menunggu Informasi", if (status == ReportStatus.CLAIM_PENDING || status == ReportStatus.HANDOVER_PENDING || status == ReportStatus.COMPLETED) "Status saat ini" else "Menunggu penemuan", status == ReportStatus.CLAIM_PENDING || status == ReportStatus.HANDOVER_PENDING || status == ReportStatus.COMPLETED),
                            Triple("Selesai atau Ditutup", if (status == ReportStatus.COMPLETED) "Selesai" else "Menunggu tahap sebelumnya", status == ReportStatus.COMPLETED)
                        )
                    }
                    steps.forEachIndexed { index, step ->
                        Row(verticalAlignment = Alignment.Top) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(shape = CircleShape, color = if (step.third) TemuinTeal else Color(0xFFE5E7EB), modifier = Modifier.size(28.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (step.third) Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        else Text("${index + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TemuinMuted)
                                    }
                                }
                                if (index != steps.lastIndex) Box(Modifier.width(2.dp).height(34.dp).background(if (step.third) TemuinTeal else Color(0xFFE5E7EB)))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.padding(top = 2.dp)) {
                                Text(step.first, fontWeight = if (step.third) FontWeight.SemiBold else FontWeight.Normal, color = if (step.third) TemuinDark else TemuinMuted, fontSize = 14.sp)
                                Text(step.second, fontSize = 11.sp, color = TemuinMuted)
                            }
                        }
                    }
                }
            }

            if (report.type == ReportType.FOUND && incomingClaim != null && status != ReportStatus.COMPLETED) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("PENGAJUAN KLAIM MASUK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TemuinTeal)
                    Text("Tinjau bukti kepemilikan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TemuinDark)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = CircleShape, color = TemuinLavender, modifier = Modifier.size(38.dp)) {
                                    Box(contentAlignment = Alignment.Center) { Text("AN", fontWeight = FontWeight.Bold, color = TemuinTeal, fontSize = 13.sp) }
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(incomingClaim.claimantName.ifBlank { "Pengaju Klaim" }, fontWeight = FontWeight.Bold, color = TemuinDark)
                                    Text("${incomingClaim.claimDate} • #${incomingClaim.id}", fontSize = 11.sp, color = TemuinMuted)
                                }
                                StatusPill(claimStatus ?: "Menunggu")
                            }
                            Surface(shape = RoundedCornerShape(12.dp), color = TemuinBackground, modifier = Modifier.fillMaxWidth()) {
                                Text(incomingClaim.claimDescription, modifier = Modifier.padding(12.dp), fontSize = 12.sp, color = TemuinDark)
                            }
                            when (claimStatus) {
                                "Menunggu" -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    OutlinedButton(onClick = { showRejectDialog = true }, modifier = Modifier.weight(1f).height(46.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB54747))) {
                                        Text("Tolak", fontWeight = FontWeight.Bold)
                                    }
                                    Button(onClick = {
                                        claimViewModel.respondToClaim(incomingClaim.id, approved = true)
                                        viewModel.updateStatus(report.id, ReportStatus.HANDOVER_PENDING)
                                    }, modifier = Modifier.weight(1f).height(46.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = TemuinTeal)) {
                                        Text("Setujui Klaim", fontWeight = FontWeight.Bold)
                                    }
                                }
                                "Diproses" -> {
                                    Text("Klaim disetujui. Setelah barang diserahkan secara langsung, konfirmasikan penyelesaian di bawah ini.", color = TemuinMuted, fontSize = 12.sp)
                                    Button(onClick = {
                                        claimViewModel.confirmHandover(incomingClaim.id)
                                        viewModel.markCompleted(report.id)
                                    }, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = TemuinTeal)) {
                                        Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Konfirmasi Serah Terima")
                                    }
                                }
                                "Ditolak" -> Text("Pengajuan klaim ditolak. Alasan: ${incomingClaim.rejectionNote.orEmpty().ifBlank { "Bukti kepemilikan belum sesuai." }}", color = Color(0xFFB54747), fontSize = 12.sp)
                                "Selesai" -> Text("Serah terima telah dikonfirmasi.", color = TemuinTeal, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            } else if (report.type == ReportType.FOUND && status != ReportStatus.COMPLETED) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Belum ada pengajuan klaim", fontWeight = FontWeight.Bold, color = TemuinDark)
                        Text("Pengajuan dari pengguna lain akan muncul di halaman progres laporan ini.", color = TemuinMuted, fontSize = 12.sp)
                    }
                }
            }

            if (report.type == ReportType.LOST && status != ReportStatus.COMPLETED) {
                Button(onClick = { viewModel.markCompleted(report.id) }, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = TemuinTeal)) {
                    Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Tandai Barang Telah Ditemukan")
                }
            }

            if (status == ReportStatus.COMPLETED) {
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
                        Text("Serah terima telah selesai dan tersimpan di Riwayat Laporan.", fontSize = 12.sp, color = TemuinMuted)
                        if (alreadyReviewed) {
                            OutlinedButton(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(14.dp)) {
                                Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Ulasan Terkirim", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(onClick = { onRate(report) }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = TemuinTeal), shape = RoundedCornerShape(14.dp)) {
                                Icon(Icons.Outlined.Star, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Beri Rating", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            if (status == ReportStatus.ACTIVE && report.ownerId == ownerId) {
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFB54747)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB54747))
                ) {
                    Icon(Icons.Outlined.Delete, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Hapus Laporan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Laporan", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah kamu yakin ingin menghapus laporan \"${report?.itemName ?: "ini"}\"? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteReport(reportId, onSuccess = onBack)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB54747))
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Tolak Pengajuan Klaim") },
            text = { OutlinedTextField(value = rejectionNote, onValueChange = { rejectionNote = it }, label = { Text("Alasan penolakan") }, minLines = 2) },
            confirmButton = {
                TextButton(onClick = {
                    claimViewModel.respondToClaim(incomingClaim?.id.orEmpty(), approved = false, rejectionNote = rejectionNote)
                    viewModel.updateStatus(reportId, ReportStatus.ACTIVE)
                    showRejectDialog = false
                }) { Text("Kirim Penolakan") }
            },
            dismissButton = { TextButton(onClick = { showRejectDialog = false }) { Text("Batal") } }
        )
    }
}

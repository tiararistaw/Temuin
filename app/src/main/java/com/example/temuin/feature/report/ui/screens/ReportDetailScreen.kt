package com.example.temuin.feature.report.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temuin.feature.report.data.model.Report
import com.example.temuin.feature.report.data.model.ReportStatus
import com.example.temuin.feature.report.data.model.ReportType
import com.example.temuin.feature.report.ui.components.IconInfoRow
import com.example.temuin.feature.report.ui.components.ReportDark
import com.example.temuin.feature.report.ui.components.ReportDarkTeal
import com.example.temuin.feature.report.ui.components.ReportLostRed
import com.example.temuin.feature.report.ui.components.ReportPhoto
import com.example.temuin.feature.report.ui.components.ReportStatusBadge
import com.example.temuin.feature.report.ui.components.ReportTopBar
import com.example.temuin.feature.report.ui.components.ReportTypeBadge
import com.example.temuin.feature.report.ui.components.ThinDivider
import com.example.temuin.feature.report.ui.viewmodel.ReportViewModel
import com.example.temuin.feature.report.util.formatDate
import com.example.temuin.feature.report.util.formatTime
import com.example.temuin.feature.report.util.millisToDateTime
import com.example.temuin.feature.report.util.relativeTime
import com.example.temuin.feature.review.ui.components.InitialAvatar
import com.example.temuin.feature.review.ui.components.TemuinBackground
import com.example.temuin.feature.review.ui.components.TemuinBorder
import com.example.temuin.feature.review.ui.components.TemuinMuted
import com.example.temuin.feature.review.ui.components.TemuinTeal
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
/**
 * Halaman 2 - Detail Laporan.
 * Tombol edit hanya muncul untuk laporan milik pengguna yang sedang login.
 */
@Composable
fun ReportDetailScreen(
    viewModel: ReportViewModel,
    userId: String,
    reportId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onClaim: (Report) -> Unit = {},
    onRate: (Report) -> Unit = {},
    onIncomingClaims: (Report) -> Unit = {},
    isFavorite: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null
) {
    val state by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    LaunchedEffect(userId) { if (state.reports.isEmpty()) viewModel.load(userId) }

    val report = state.reports.find { it.id == reportId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TemuinBackground)
    ) {
        ReportTopBar(title = "Detail Laporan", onBack = onBack)

        if (report == null) {
            Box(Modifier.weight(1f).fillMaxWidth(), Alignment.Center) {
                if (state.isLoading) CircularProgressIndicator(color = TemuinTeal)
                else Text("Laporan tidak ditemukan.", color = TemuinMuted)
            }
            return@Column
        }

        val isOwner = report.ownerId == userId
        val dateTime = millisToDateTime(report.occurredAtMillis)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            PhotoSection(report)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, TemuinBorder)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ReportTypeBadge(report.type)
                        ReportStatusBadge(report.status)
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "#${report.id}",
                            fontSize = 11.sp,
                            color = TemuinMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = report.itemName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ReportDark
                    )
                    Text(
                        text = report.category,
                        fontSize = 12.sp,
                        color = TemuinMuted
                    )

                    ThinDivider()

                    IconInfoRow(
                        icon = Icons.Outlined.LocationOn,
                        text = report.location,
                        iconTint = ReportLostRed
                    )
                    IconInfoRow(
                        icon = Icons.Outlined.CalendarMonth,
                        text = formatDate(dateTime.toLocalDate())
                    )
                    IconInfoRow(
                        icon = Icons.Outlined.Schedule,
                        text = "${formatTime(dateTime.toLocalTime())} WIB"
                    )
                }
            }

            // Deskripsi
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, TemuinBorder)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Deskripsi & Ciri Khusus", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ReportDark)
                    Text(
                        text = report.description,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFF3A4553)
                    )
                }
            }

            // Pelapor & Kontak
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, TemuinBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val hideName = report.isAnonymous && !isOwner
                        val shownName = if (hideName) "Pengguna Anonim" else report.ownerName

                        InitialAvatar(shownName, 42)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (report.type == ReportType.FOUND) "Ditemukan oleh" else "Dilaporkan oleh",
                                fontSize = 11.sp,
                                color = TemuinMuted
                            )
                            Text(
                                text = if (report.isAnonymous && isOwner) "$shownName (Anonim)" else shownName,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = ReportDark
                            )
                            Text(
                                text = "Dipublikasikan ${relativeTime(report.createdAtMillis).lowercase()}",
                                fontSize = 11.sp,
                                color = TemuinMuted
                            )
                        }
                    }

                    ThinDivider()

                    IconInfoRow(
                        icon = Icons.Outlined.Call,
                        text = "No. HP / WA: ${report.contact.ifBlank { "-" }}",
                        iconTint = TemuinTeal
                    )
                }
            }
        }

        // Aksi bawah (bergantung peran & status)
        val showClaim = !isOwner && report.type == ReportType.FOUND && report.status == ReportStatus.ACTIVE
        val showOwnerActions = isOwner

        if (showClaim || showOwnerActions) {
            Surface(color = Color.White, shadowElevation = 8.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (showClaim) {
                        Button(
                            onClick = { onClaim(report) },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ReportDarkTeal)
                        ) { Text("Ajukan Klaim", fontWeight = FontWeight.SemiBold) }
                    }

                    if (showOwnerActions) {
                        if (report.type == ReportType.FOUND) {
                            OutlinedButton(
                                onClick = { onIncomingClaims(report) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(14.dp)
                            ) { Text("Lihat Pengajuan Klaim", color = TemuinTeal) }
                        }

                        Button(
                            onClick = { onEdit(report.id) },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ReportDarkTeal)
                        ) {
                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Edit Laporan", fontWeight = FontWeight.SemiBold)
                        }

                        if (report.status == ReportStatus.ACTIVE) {
                            OutlinedButton(
                                onClick = { viewModel.markCompleted(report.id) },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, TemuinBorder)
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = TemuinTeal, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Tandai Selesai", color = TemuinTeal, fontWeight = FontWeight.SemiBold)
                            }

                            OutlinedButton(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Color(0xFFB54747)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB54747))
                            ) {
                                Icon(Icons.Outlined.Delete, null, tint = Color(0xFFB54747), modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Hapus Laporan", color = Color(0xFFB54747), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && report != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Laporan", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah kamu yakin ingin menghapus laporan \"${report.itemName}\"? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteReport(report.id, onSuccess = onBack)
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
}

/** Foto barang: pager geser bila lebih dari satu foto, plus indikator titik. */
@Composable
private fun PhotoSection(report: Report) {
    val photos = report.photoUrls

    if (photos.isEmpty()) {
        ReportPhoto(
            url = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(18.dp))
        )
        return
    }

    val pagerState = rememberPagerState(pageCount = { photos.size })

    Box {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(18.dp))
        ) { page ->
            ReportPhoto(
                url = photos[page],
                modifier = Modifier.fillMaxSize(),
                contentDescription = report.itemName
            )
        }

        if (photos.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(photos.size) { index ->
                    Surface(
                        shape = CircleShape,
                        color = if (pagerState.currentPage == index) Color.White else Color(0x80FFFFFF),
                        modifier = Modifier.size(8.dp)
                    ) {}
                }
            }
        }
    }
}
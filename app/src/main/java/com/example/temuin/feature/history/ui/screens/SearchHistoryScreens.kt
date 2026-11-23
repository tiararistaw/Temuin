package com.example.temuin.feature.history.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temuin.feature.report.data.model.ReportType
import com.example.temuin.feature.report.data.model.Report
import com.example.temuin.feature.report.data.model.ReportStatus
import coil3.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.temuin.feature.report.ui.viewmodel.ReportViewModel
import com.example.temuin.feature.claim.ui.viewmodel.ClaimViewModel
import com.example.temuin.feature.review.ui.components.*

@Composable
fun SearchScreen(viewModel: ReportViewModel, userId: String, onReportClick: (String) -> Unit) {
    val state by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("Semua") }
    LaunchedEffect(userId) { viewModel.load(userId) }
    val reports = state.reports.filter { r ->
        (filter == "Semua" || (filter == "Barang Hilang" && r.type == ReportType.LOST) || (filter == "Barang Ditemukan" && r.type == ReportType.FOUND)) &&
            (query.isBlank() || r.itemName.contains(query, true) || r.location.contains(query, true) || r.category.contains(query, true))
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(TemuinBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Cari Barang", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TemuinDark)
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cari nama barang atau lokasi") },
            leadingIcon = { Icon(Icons.Outlined.Search, null, tint = TemuinMuted) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TemuinTeal,
                unfocusedBorderColor = TemuinBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("Semua", "Barang Hilang", "Barang Ditemukan")) { item ->
                TemuinFilterChip(
                    label = item,
                    selected = filter == item,
                    onClick = { filter = item }
                )
            }
        }
        if (reports.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tidak ada laporan yang sesuai.", color = TemuinMuted)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(reports, key = { it.id }) { report ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onReportClick(report.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (report.type == ReportType.FOUND) TemuinPaleBlue else TemuinLavender
                                ) {
                                    Text(
                                        text = if (report.type == ReportType.FOUND) "Ditemukan" else "Hilang",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TemuinTeal
                                    )
                                }
                                Spacer(Modifier.weight(1f))
                                Text(report.id, style = MaterialTheme.typography.labelSmall, color = TemuinMuted)
                            }
                            Text(report.itemName, fontWeight = FontWeight.SemiBold, color = TemuinDark)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.LocationOn, null, tint = TemuinTeal, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(report.location, style = MaterialTheme.typography.bodySmall, color = TemuinMuted)
                            }
                            Text("Lihat detail →", color = TemuinTeal, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(reportViewModel: ReportViewModel, claimViewModel: ClaimViewModel, userId: String,
                  onReportClick: (String) -> Unit, onClaimClick: (String) -> Unit,
                  initialTab: String = "Klaim") {
    val reportState by reportViewModel.uiState.collectAsState()
    val claims by claimViewModel.claims
    var tab by remember(initialTab) { mutableStateOf(if (initialTab == "Laporan") "Laporan" else "Klaim") }
    var filter by remember { mutableStateOf("Semua") }
    LaunchedEffect(userId) {
        reportViewModel.load(userId)
        claimViewModel.loadClaims(userId)
    }

    val ownReports = reportState.reports.filter { it.ownerId == userId }
    val filteredReports = ownReports.filter { report ->
        filter == "Semua" ||
            (filter == "Barang Hilang" && report.type == ReportType.LOST) ||
            (filter == "Barang Ditemukan" && report.type == ReportType.FOUND)
    }
    val filteredClaims = claims.filter { filter == "Semua" || it.status == filter }

    Column(
        Modifier.fillMaxSize().background(TemuinBackground).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Riwayat", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TemuinDark)
            Text("Semua aktivitasmu, tersusun rapi.", color = TemuinMuted, fontSize = 13.sp)
        }
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            listOf("Klaim", "Laporan").forEachIndexed { index, value ->
                SegmentedButton(
                    selected = tab == value,
                    onClick = { tab = value; filter = "Semua" },
                    shape = SegmentedButtonDefaults.itemShape(index, 2),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = TemuinTeal,
                        activeContentColor = Color.White,
                        inactiveContainerColor = Color.White,
                        inactiveContentColor = TemuinDark
                    )
                ) { Text("Riwayat $value", fontWeight = FontWeight.SemiBold) }
            }
        }
        if (tab == "Laporan") {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Laporan barangmu", fontWeight = FontWeight.Bold, color = TemuinDark, fontSize = 17.sp)
                Text("Pantau laporan hilang dan temuan yang kamu publikasikan.", color = TemuinMuted, fontSize = 12.sp)
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("Semua", "Barang Hilang", "Barang Ditemukan")) { value ->
                    TemuinFilterChip(label = value, selected = filter == value, onClick = { filter = value })
                }
            }
            if (filteredReports.isEmpty()) {
                EmptyHistory("Belum ada laporan", "Laporan yang kamu buat akan muncul di sini.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
                    items(filteredReports, key = { it.id }) { report ->
                        HistoryReportCard(report = report, onClick = { onReportClick(report.id) })
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Pengajuan klaimmu", fontWeight = FontWeight.Bold, color = TemuinDark, fontSize = 17.sp)
                Text("Pantau perkembangan klaim barang.", color = TemuinMuted, fontSize = 12.sp)
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("Semua", "Menunggu", "Diproses", "Selesai", "Ditolak")) { value ->
                    TemuinFilterChip(label = value, selected = filter == value, onClick = { filter = value })
                }
            }
            if (filteredClaims.isEmpty()) {
                EmptyHistory("Belum ada pengajuan klaim", "Klaim yang kamu ajukan akan muncul di sini.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
                    items(filteredClaims, key = { it.id }) { claim ->
                        HistoryClaimCard(claim = claim, onClick = { onClaimClick(claim.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryReportCard(report: Report, onClick: () -> Unit) {
    val statusColor = when (report.status) {
        ReportStatus.ACTIVE -> TemuinTeal
        ReportStatus.CLAIM_PENDING -> Color(0xFFB7791F)
        ReportStatus.HANDOVER_PENDING -> Color(0xFF4569B0)
        ReportStatus.COMPLETED -> TemuinTeal
    }
    val date = remember(report.createdAtMillis) {
        SimpleDateFormat("d MMM yyyy", Locale("id", "ID")).format(Date(report.createdAtMillis))
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (report.photoUrls.firstOrNull() != null) {
                AsyncImage(model = report.photoUrls.first(), contentDescription = report.itemName,
                    modifier = Modifier.size(84.dp).background(TemuinBackground, RoundedCornerShape(12.dp)))
            } else {
                Surface(modifier = Modifier.size(84.dp), shape = RoundedCornerShape(12.dp), color = TemuinBackground) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Image, null, tint = TemuinMuted, modifier = Modifier.size(30.dp))
                    }
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(shape = RoundedCornerShape(50), color = if (report.type == ReportType.FOUND) TemuinPaleBlue else TemuinLavender) {
                        Text(if (report.type == ReportType.FOUND) "Ditemukan" else "Hilang", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = TemuinTeal, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.weight(1f))
                    Text(report.id, color = TemuinMuted, fontSize = 9.sp)
                }
                Text(report.itemName, color = TemuinDark, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2)
                Text("Dibuat $date", color = TemuinMuted, fontSize = 11.sp)
                Surface(shape = RoundedCornerShape(50), color = statusColor.copy(alpha = .10f)) {
                    Text(report.status.label.uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = statusColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = TemuinTeal)
        }
    }
}

@Composable
private fun HistoryClaimCard(claim: com.example.temuin.feature.claim.data.model.Claim, onClick: () -> Unit) {
    val statusColor = when (claim.status) {
        "Menunggu" -> Color(0xFFB7791F)
        "Diproses" -> Color(0xFF4569B0)
        "Selesai" -> TemuinTeal
        else -> Color(0xFFB54747)
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(15.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "#${claim.id}", color = TemuinMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                Surface(color = statusColor.copy(alpha = 0.12f), shape = RoundedCornerShape(50.dp)) {
                    Text(text = claim.status.uppercase(), color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(claim.itemName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TemuinDark)
            Spacer(Modifier.height(4.dp))
            Text("Diajukan ${claim.claimDate}", fontSize = 12.sp, color = TemuinMuted)
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = TemuinLavender)
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(6.dp).background(TemuinTeal, CircleShape))
                    Text(
                        when (claim.status) {
                            "Menunggu" -> "Pengajuan berhasil dikirim"
                            "Diproses" -> "Menunggu tanggapan pelapor temuan"
                            "Selesai" -> "Barang telah dikembalikan"
                            else -> "Pengajuan ditolak"
                        },
                        color = TemuinDark, fontSize = 12.sp, fontWeight = FontWeight.Medium
                    )
                }
                Text("→", color = TemuinTeal, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun EmptyHistory(title: String, message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Outlined.History, null, tint = TemuinMuted, modifier = Modifier.size(48.dp))
            Text(title, fontWeight = FontWeight.SemiBold, color = TemuinDark)
            Text(message, color = TemuinMuted, fontSize = 13.sp)
        }
    }
}

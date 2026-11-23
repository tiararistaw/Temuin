package com.example.temuin.feature.home.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temuin.feature.report.data.model.Report
import com.example.temuin.feature.report.data.model.ReportType
import com.example.temuin.feature.report.data.model.ReportStatus
import com.example.temuin.feature.report.ui.components.ReportDark
import com.example.temuin.feature.report.ui.components.ReportDarkTeal
import com.example.temuin.feature.report.ui.components.ReportFoundGreenSoft
import com.example.temuin.feature.report.ui.components.ReportLostRed
import com.example.temuin.feature.report.ui.components.ReportLostRedSoft
import com.example.temuin.feature.report.ui.components.ReportPhoto
import com.example.temuin.feature.report.ui.viewmodel.ReportFilter
import com.example.temuin.feature.report.ui.viewmodel.ReportViewModel
import com.example.temuin.feature.report.util.millisToDateTime
import com.example.temuin.feature.report.util.relativeTime
import com.example.temuin.feature.review.ui.components.TemuinBackground
import com.example.temuin.feature.review.ui.components.TemuinBorder
import com.example.temuin.feature.review.ui.components.TemuinDark
import com.example.temuin.feature.review.ui.components.TemuinLavender
import com.example.temuin.feature.review.ui.components.TemuinMuted
import com.example.temuin.feature.review.ui.components.TemuinPaleBlue
import com.example.temuin.feature.review.ui.components.TemuinTeal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Warna khusus beranda
private val HeroStart = Color(0xFF0B6B63)
private val HeroEnd = Color(0xFF72D9CA)
private val OwnChipBg = Color(0xFFCFF3EC)

private data class HomeFilterChip(
    val filter: ReportFilter,
    val label: String,
    val icon: ImageVector
)

private val HomeFilters = listOf(
    HomeFilterChip(ReportFilter.ALL, "Semua", Icons.Outlined.GridView),
    HomeFilterChip(ReportFilter.LOST, "Barang Hilang", Icons.Outlined.ErrorOutline),
    HomeFilterChip(ReportFilter.FOUND, "Barang Ditemukan", Icons.Outlined.CheckCircle),
    HomeFilterChip(ReportFilter.MINE, "Laporan Saya", Icons.Outlined.Person)
)

/**
 * Beranda - daftar laporan barang hilang & ditemukan.
 *
 * @param onReportClick  buka halaman detail laporan
 * @param onEditClick    buka form edit (hanya muncul di laporan milik sendiri)
 * @param onCreateClick  buka form lapor barang (tombol "Buat Laporan Cepat" dan FAB)
 */
@Composable
fun HomeScreen(
    viewModel: ReportViewModel,
    userId: String,
    userName: String,
    avatarUrl: String?,
    onReportClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    onProgressClick: (String) -> Unit = onReportClick,
    onProfileClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) { viewModel.load(userId) }

    val firstName = userName.substringBefore(" ")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TemuinBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            HomeTopBar(userName = userName, avatarUrl = avatarUrl, onProfileClick = onProfileClick)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 14.dp,
                    bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item { HeroBanner(firstName = firstName) }

                item {
                    val activeReports = state.reports
                        .filter { it.ownerId == userId && it.status != ReportStatus.COMPLETED }
                        .take(2)
                    ActiveReportProgressSection(reports = activeReports, onProgressClick = onProgressClick)
                }

                item {
                    SearchRow(
                        query = state.query,
                        onQueryChange = viewModel::setQuery,
                        onReset = {
                            viewModel.setQuery("")
                            viewModel.setFilter(ReportFilter.ALL)
                        }
                    )
                }

                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(HomeFilters) { chip ->
                            FilterChipItem(
                                label = "${chip.label} (${state.count(chip.filter)})",
                                icon = chip.icon,
                                selected = state.filter == chip.filter,
                                onClick = { viewModel.setFilter(chip.filter) }
                            )
                        }
                    }
                }

                if (state.isLoading && state.reports.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = TemuinTeal)
                        }
                    }
                } else if (state.filtered.isEmpty()) {
                    item {
                        Text(
                            text = "Belum ada laporan yang sesuai.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            color = TemuinMuted
                        )
                    }
                }

                items(items = state.filtered, key = { it.id }) { report ->
                    if (report.ownerId == userId) {
                        OwnReportCard(
                            report = report,
                            onDetailClick = { onReportClick(report.id) },
                            onEditClick = { onEditClick(report.id) }
                        )
                    } else {
                        OtherReportCard(
                            report = report,
                            onDetailClick = { onReportClick(report.id) }
                        )
                    }
                }
            }
        }

    }
}

// ----------------------------------------------------------------------------
// Top bar
// ----------------------------------------------------------------------------

@Composable
private fun HomeTopBar(userName: String, avatarUrl: String?, onProfileClick: () -> Unit) {
    Surface(color = Color.White, shadowElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo sementara; ganti dengan drawable logo Temuin bila sudah ada
            Surface(
                shape = CircleShape,
                color = TemuinLavender,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.Search, null,
                        tint = TemuinTeal, modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Temuin",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ReportDarkTeal
                )
                Text(
                    text = "Beranda",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ReportDark
                )
            }

            IconButton(onClick = { /* TODO: halaman notifikasi */ }) {
                Icon(Icons.Outlined.Notifications, "Notifikasi", tint = ReportDark)
            }

            Spacer(Modifier.width(4.dp))

            IconButton(onClick = onProfileClick) {
                Avatar(name = userName, url = avatarUrl, size = 38)
            }
        }
    }
}

// ----------------------------------------------------------------------------
// Aktivitas yang sedang berjalan (mengikuti alur Beranda prototype)
// ----------------------------------------------------------------------------

@Composable
private fun ActiveReportProgressSection(reports: List<Report>, onProgressClick: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("SEDANG BERJALAN", color = TemuinTeal, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = .5.sp)
                    Text("Progres aktivitasmu", color = TemuinDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Text("${reports.size} aktivitas", color = TemuinMuted, fontSize = 11.sp)
            }
            if (reports.isEmpty()) {
                Text("Belum ada laporan yang sedang berjalan.", color = TemuinMuted, fontSize = 12.sp)
            } else {
                reports.forEach { report ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(TemuinBackground)
                            .clickable { onProgressClick(report.id) }.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(shape = RoundedCornerShape(10.dp), color = TemuinLavender, modifier = Modifier.size(36.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(if (report.type == ReportType.FOUND) Icons.Outlined.CheckCircle else Icons.Outlined.ErrorOutline, null, tint = TemuinTeal, modifier = Modifier.size(19.dp))
                            }
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(if (report.type == ReportType.FOUND) "Barang Ditemukan" else "Barang Hilang", color = TemuinMuted, fontSize = 10.sp)
                            Text(report.itemName, color = TemuinDark, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(report.status.label, color = TemuinTeal, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TemuinTeal)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// Banner sapaan
// ----------------------------------------------------------------------------

@Composable
private fun HeroBanner(firstName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(HeroStart, HeroEnd)))
    ) {
        Icon(
            imageVector = Icons.Outlined.TravelExplore,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.18f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(76.dp)
        )

        Column(modifier = Modifier.padding(20.dp)) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.22f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.School, null,
                        tint = Color.White, modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "KAMPUS FILKOM UB",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Halo, $firstName! \uD83D\uDC4B",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Kehilangan atau menemukan sesuatu hari ini? Jangan panik, yuk saling bantu warga kampus.",
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

        }
    }
}

// ----------------------------------------------------------------------------
// Pencarian & filter
// ----------------------------------------------------------------------------

@Composable
private fun SearchRow(
    query: String,
    onQueryChange: (String) -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Cari kunci, KTM, jaket, botol...", fontSize = 13.sp, maxLines = 1) },
            leadingIcon = { Icon(Icons.Outlined.Search, null, tint = TemuinMuted) },
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TemuinTeal,
                unfocusedBorderColor = TemuinBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(Modifier.width(10.dp))

        // Tombol reset pencarian & filter
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = TemuinLavender,
            modifier = Modifier
                .size(54.dp)
                .clickable { onReset() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Tune, "Reset filter", tint = TemuinTeal)
            }
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) TemuinTeal else Color.White,
        border = if (selected) null else BorderStroke(1.dp, TemuinBorder),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color.White else TemuinDark,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                color = if (selected) Color.White else TemuinDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ----------------------------------------------------------------------------
// Kartu laporan milik sendiri (ada aksen kiri + tombol Edit)
// ----------------------------------------------------------------------------

@Composable
private fun OwnReportCard(
    report: Report,
    onDetailClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column {
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                // aksen teal di sisi kiri
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .fillMaxHeight()
                        .background(ReportDarkTeal)
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportPhoto(
                        url = report.photoUrls.firstOrNull(),
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentDescription = report.itemName
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TypeChip(report.type)
                            OwnerChip()
                        }

                        Text(
                            text = report.itemName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ReportDark,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "Ciri: ${report.description}",
                            fontSize = 12.sp,
                            color = TemuinMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                Icons.Outlined.LocationOn, null,
                                tint = ReportDarkTeal, modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = report.location,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ReportDark,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        TimeRow(report.occurredAtMillis)
                    }
                }
            }

            // baris aksi
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TemuinPaleBlue)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, TemuinBorder),
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = ReportDark
                    )
                ) {
                    Icon(Icons.Outlined.EditNote, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Edit Laporan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = onDetailClick,
                    modifier = Modifier.height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ReportDarkTeal)
                ) {
                    Text("Lihat Detail", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward, null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// Kartu laporan orang lain (avatar pelapor + bookmark)
// ----------------------------------------------------------------------------

@Composable
private fun OtherReportCard(
    report: Report,
    onDetailClick: () -> Unit
) {
    // Bookmark baru berupa tampilan lokal; sambungkan ke fitur Favorit nanti.
    var saved by rememberSaveable(report.id) { mutableStateOf(false) }
    val ownerName = if (report.isAnonymous) "Anonim" else report.ownerName

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // header: pelapor + jenis + bookmark
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(
                    name = ownerName,
                    url = null,
                    size = 40,
                    anonymous = report.isAnonymous
                )

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ownerName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ReportDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = relativeTime(report.createdAtMillis),
                        fontSize = 12.sp,
                        color = TemuinMuted
                    )
                }

                TypeChip(report.type)

                IconButton(onClick = { saved = !saved }) {
                    Icon(
                        imageVector = if (saved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Simpan",
                        tint = if (saved) Color(0xFFB84B4B) else TemuinMuted
                    )
                }
            }

            // isi: foto + judul + deskripsi + waktu
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, TemuinBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportPhoto(
                        url = report.photoUrls.firstOrNull(),
                        modifier = Modifier
                            .size(88.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentDescription = report.itemName
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = report.itemName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ReportDark,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = report.description,
                            fontSize = 12.sp,
                            color = TemuinMuted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        TimeRow(report.occurredAtMillis)
                    }
                }
            }

            // footer: lokasi + tombol detail
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.LocationOn, null,
                    tint = ReportDarkTeal, modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = report.location,
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReportDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = TemuinLavender,
                    modifier = Modifier.clickable { onDetailClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Lihat Detail",
                            color = TemuinTeal,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight, null,
                            tint = TemuinTeal, modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// Komponen kecil
// ----------------------------------------------------------------------------

/** Chip "Hilang" / "Ditemukan" versi ringkas. */
@Composable
private fun TypeChip(type: ReportType) {
    val found = type == ReportType.FOUND
    val color = if (found) TemuinTeal else ReportLostRed

    Surface(
        shape = RoundedCornerShape(50),
        color = if (found) ReportFoundGreenSoft else ReportLostRedSoft
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (found) Icons.Outlined.CheckCircle else Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = if (found) "Ditemukan" else "Hilang",
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/** Chip "Laporan Saya" untuk laporan milik pengguna yang sedang login. */
@Composable
private fun OwnerChip() {
    Surface(shape = RoundedCornerShape(50), color = OwnChipBg) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                Icons.Filled.Person, null,
                tint = ReportDarkTeal, modifier = Modifier.size(12.dp)
            )
            Text(
                text = "Laporan Saya",
                color = ReportDarkTeal,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TimeRow(millis: Long) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Outlined.Schedule, null,
            tint = TemuinMuted, modifier = Modifier.size(13.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = dayTimeLabel(millis),
            fontSize = 11.sp,
            color = TemuinMuted
        )
    }
}

/** Foto profil bila ada, kalau tidak: lingkaran berisi inisial (atau ikon untuk anonim). */
@Composable
private fun Avatar(
    name: String,
    url: String?,
    size: Int,
    anonymous: Boolean = false
) {
    if (url != null) {
        ReportPhoto(
            url = url,
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape),
            contentDescription = name
        )
    } else {
        Surface(
            shape = CircleShape,
            color = TemuinLavender,
            modifier = Modifier.size(size.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (anonymous) {
                    Icon(
                        Icons.Filled.Person, null,
                        tint = TemuinTeal, modifier = Modifier.size((size / 2).dp)
                    )
                } else {
                    Text(
                        text = initialsOf(name),
                        color = TemuinTeal,
                        fontSize = (size / 2.6f).sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun initialsOf(name: String): String =
    name.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

/** Contoh: "Hari ini, 09:30 WIB", "Kemarin, 14:15 WIB", "23 Mei 2024, 10:00 WIB". */
private fun dayTimeLabel(millis: Long): String {
    val dt = millisToDateTime(millis)
    val today = LocalDate.now()
    val day = when (dt.toLocalDate()) {
        today -> "Hari ini"
        today.minusDays(1) -> "Kemarin"
        else -> dt.format(
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("id-ID"))
        )
    }
    return "$day, ${dt.format(DateTimeFormatter.ofPattern("HH:mm"))} WIB"
}
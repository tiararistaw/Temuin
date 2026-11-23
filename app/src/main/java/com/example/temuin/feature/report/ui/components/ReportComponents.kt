package com.example.temuin.feature.report.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.temuin.feature.report.data.model.Report
import com.example.temuin.feature.report.data.model.ReportStatus
import com.example.temuin.feature.report.data.model.ReportType
import com.example.temuin.feature.report.util.formatDateTimeWib
import com.example.temuin.feature.report.util.relativeTime
import com.example.temuin.feature.review.ui.components.TemuinBackground
import com.example.temuin.feature.review.ui.components.TemuinBorder
import com.example.temuin.feature.review.ui.components.TemuinLavender
import com.example.temuin.feature.review.ui.components.TemuinMuted
import com.example.temuin.feature.review.ui.components.TemuinPaleBlue
import com.example.temuin.feature.review.ui.components.TemuinTeal

// Warna tambahan khusus fitur Lapor Barang (selaras desain)
val ReportDarkTeal = Color(0xFF134E4A)
val ReportDark = Color(0xFF131B2E)
val ReportLostRed = Color(0xFFBA1A1A)
val ReportLostRedSoft = Color(0xFFFFE8E7)
val ReportFoundGreenSoft = Color(0xFFE0F2F1)
val ReportInfoBlue = Color(0xFF1D4ED8)
val ReportInfoBlueSoft = Color(0xFFEFF4FF)

/** Top bar sederhana: panah kembali + judul. */
@Composable
fun ReportTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TemuinBackground)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = ReportDark)
            }
        } else {
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = ReportDark,
            modifier = Modifier.weight(1f)
        )
        trailing?.invoke()
    }
}

/** Chip kecil jenis laporan (Hilang / Ditemukan). */
@Composable
fun ReportTypeBadge(type: ReportType) {
    val found = type == ReportType.FOUND
    Surface(
        shape = RoundedCornerShape(50),
        color = if (found) ReportFoundGreenSoft else ReportLostRedSoft
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (found) Icons.Filled.CheckCircle else Icons.Filled.PriorityHigh,
                contentDescription = null,
                tint = if (found) TemuinTeal else ReportLostRed,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = type.label,
                color = if (found) TemuinTeal else ReportLostRed,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ReportStatusBadge(status: ReportStatus) {
    val done = status == ReportStatus.COMPLETED
    Surface(
        shape = RoundedCornerShape(50),
        color = if (done) TemuinLavender else ReportFoundGreenSoft
    ) {
        Text(
            text = status.label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            color = if (done) TemuinMuted else TemuinTeal,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** Baris ikon + teks (lokasi, waktu, dll). */
@Composable
fun IconInfoRow(
    icon: ImageVector,
    text: String,
    iconTint: Color = TemuinMuted,
    textColor: Color = Color(0xFF3A4553)
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, color = textColor, fontSize = 13.sp)
    }
}

/** Kotak foto dengan fallback ikon bila belum ada foto. */
@Composable
fun ReportPhoto(
    url: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier.background(TemuinPaleBlue),
        contentAlignment = Alignment.Center
    ) {
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(Icons.Filled.Image, null, tint = TemuinMuted, modifier = Modifier.size(32.dp))
        }
    }
}

/** Kartu laporan untuk LazyColumn pada halaman daftar. */
@Composable
fun ReportCard(
    report: Report,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReportPhoto(
                url = report.photoUrls.firstOrNull(),
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentDescription = report.itemName
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ReportTypeBadge(report.type)
                    if (report.status == ReportStatus.COMPLETED) {
                        ReportStatusBadge(report.status)
                    }
                }

                Text(
                    text = report.itemName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = ReportDark
                )

                Text(
                    text = report.category,
                    fontSize = 11.sp,
                    color = TemuinMuted
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.LocationOn, null,
                        tint = ReportLostRed, modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = report.location,
                        fontSize = 12.sp,
                        color = TemuinMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Schedule, null,
                        tint = TemuinMuted, modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = relativeTime(report.createdAtMillis),
                        fontSize = 11.sp,
                        color = TemuinMuted
                    )
                }
            }
        }
    }
}

/** Ringkasan lokasi + waktu kejadian (dipakai di Status Publikasi). */
@Composable
fun ReportSummaryRow(report: Report) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        IconInfoRow(Icons.Outlined.LocationOn, report.location, iconTint = ReportLostRed)
        IconInfoRow(Icons.Outlined.Schedule, formatDateTimeWib(report.occurredAtMillis))
    }
}

/** Garis pemisah tipis konsisten. */
@Composable
fun ThinDivider() {
    HorizontalDivider(color = TemuinBorder, thickness = 1.dp)
}

/** Lingkaran bernomor untuk daftar langkah. */
@Composable
fun StepNumber(number: Int) {
    Surface(
        shape = CircleShape,
        color = ReportFoundGreenSoft,
        modifier = Modifier.size(22.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = number.toString(),
                color = TemuinTeal,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
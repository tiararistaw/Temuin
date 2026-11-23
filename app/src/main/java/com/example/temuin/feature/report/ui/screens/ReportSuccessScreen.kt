package com.example.temuin.feature.report.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temuin.feature.report.data.model.ReportType
import com.example.temuin.feature.report.ui.components.ReportDark
import com.example.temuin.feature.report.ui.components.ReportDarkTeal
import com.example.temuin.feature.report.ui.components.ReportFoundGreenSoft
import com.example.temuin.feature.report.ui.components.ReportInfoBlue
import com.example.temuin.feature.report.ui.components.ReportInfoBlueSoft
import com.example.temuin.feature.report.ui.components.ReportSummaryRow
import com.example.temuin.feature.report.ui.components.ReportTopBar
import com.example.temuin.feature.report.ui.components.ReportTypeBadge
import com.example.temuin.feature.report.ui.components.StepNumber
import com.example.temuin.feature.report.ui.components.ThinDivider
import com.example.temuin.feature.report.ui.viewmodel.ReportViewModel
import com.example.temuin.feature.report.util.relativeTime
import com.example.temuin.feature.review.ui.components.TemuinBackground
import com.example.temuin.feature.review.ui.components.TemuinBorder
import com.example.temuin.feature.review.ui.components.TemuinMuted
import com.example.temuin.feature.review.ui.components.TemuinTeal

/**
 * Halaman 4 - Status Publikasi (tampil setelah laporan berhasil dibuat).
 */
@Composable
fun ReportSuccessScreen(
    viewModel: ReportViewModel,
    userId: String,
    reportId: String,
    onViewDetail: () -> Unit,
    onHome: () -> Unit,
    onHistory: () -> Unit = onHome
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(userId) { if (state.reports.isEmpty()) viewModel.load(userId) }

    // Tombol back sistem diarahkan ke beranda agar tidak kembali ke form.
    BackHandler(onBack = onHome)

    val report = state.reports.find { it.id == reportId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TemuinBackground)
    ) {
        ReportTopBar(
            title = "Status Publikasi",
            trailing = {
                IconButton(onClick = onHome) {
                    Icon(Icons.Default.Close, "Tutup", tint = TemuinMuted)
                }
            }
        )

        if (report == null) {
            Box(Modifier.weight(1f).fillMaxWidth(), Alignment.Center) {
                Text("Laporan tidak ditemukan.", color = TemuinMuted)
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ikon centang
            Surface(shape = CircleShape, color = Color(0xFFD5F5EF), modifier = Modifier.size(84.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        shape = CircleShape,
                        color = ReportDarkTeal,
                        border = BorderStroke(3.dp, Color(0xFF9FE7DB)),
                        modifier = Modifier.size(58.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(30.dp))
                        }
                    }
                }
            }

            Text(
                text = "Laporan Berhasil Dipublikasikan!",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ReportDark,
                textAlign = TextAlign.Center
            )

            Text(
                text = buildAnnotatedString {
                    append("Laporanmu sudah aktif dan dapat dilihat oleh seluruh sivitas kampus di feed ")
                    withStyle(SpanStyle(color = TemuinTeal, fontWeight = FontWeight.Bold)) { append("Temuin") }
                    append(".")
                },
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = TemuinMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // Kartu ringkasan laporan
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, TemuinBorder)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ReportFoundGreenSoft
                        ) {
                            Text(
                                text = "#${report.id}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = ReportDarkTeal
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "• ${relativeTime(report.createdAtMillis)}",
                            fontSize = 11.sp,
                            color = TemuinMuted
                        )
                        Spacer(Modifier.weight(1f))
                        ReportTypeBadge(report.type)
                    }

                    ThinDivider()

                    Text(
                        text = report.category.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TemuinMuted,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = report.itemName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = ReportDark
                    )

                    ReportSummaryRow(report)

                    // Info sinkronisasi
                    Surface(shape = RoundedCornerShape(12.dp), color = ReportInfoBlueSoft) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.Sync, null,
                                tint = ReportInfoBlue, modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("Tersinkronisasi Otomatis")
                                    }
                                    append(" dengan Pos Satpam & Komunitas Kampus FILKOM UB.")
                                },
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = Color(0xFF1E3A8A)
                            )
                        }
                    }
                }
            }

            // Langkah selanjutnya
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, TemuinBorder)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null, tint = TemuinTeal, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "APA LANGKAH SELANJUTNYA?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ReportDark,
                            letterSpacing = 0.5.sp
                        )
                    }

                    val steps = if (report.type == ReportType.FOUND) {
                        listOf(
                            "Pantau notifikasi jika ada mahasiswa yang mengajukan klaim atas barang ini.",
                            "Cocokkan detail bukti khusus sebelum serah terima di titik temu kampus."
                        )
                    } else {
                        listOf(
                            "Pantau notifikasi jika ada yang menemukan dan menghubungi kamu terkait barang ini.",
                            "Siapkan ciri khusus barang sebagai bukti kepemilikan saat proses pengembalian."
                        )
                    }

                    steps.forEachIndexed { index, step ->
                        Row(verticalAlignment = Alignment.Top) {
                            StepNumber(index + 1)
                            Spacer(Modifier.width(10.dp))
                            Text(step, fontSize = 13.sp, lineHeight = 19.sp, color = Color(0xFF3A4553))
                        }
                    }
                }
            }
        }

        // Aksi bawah
        Surface(color = Color.White, shadowElevation = 8.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onViewDetail,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ReportDarkTeal)
                ) {
                    Icon(Icons.Default.Description, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Lihat Detail Laporan", fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onHistory,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, TemuinBorder)
                ) {
                    Icon(Icons.Default.Home, null, tint = ReportDark, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Lihat Riwayat Laporan", color = ReportDark, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
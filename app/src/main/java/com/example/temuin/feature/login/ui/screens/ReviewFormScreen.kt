package com.example.temuin.feature.login.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temuin.feature.review.data.model.Review
import com.example.temuin.feature.review.data.model.ReviewContext
import com.example.temuin.feature.review.ui.components.*
import com.example.temuin.feature.review.ui.viewmodel.ReviewViewModel

@Composable
fun ReviewFormScreen(
    viewModel: ReviewViewModel,
    currentUserId: String,
    currentUserName: String,
    targetUserId: String,
    targetUserName: String,
    reviewContext: ReviewContext,
    existingReview: Review?,
    onBack: () -> Unit,
    onSubmitted: () -> Unit
) {

    // STATE FORM

    var rating by remember(existingReview?.id) {
        mutableIntStateOf(existingReview?.rating ?: 0)
    }

    var comment by remember(existingReview?.id) {
        mutableStateOf(existingReview?.comment ?: "")
    }

    var selectedTags by remember(existingReview?.id) {
        mutableStateOf(existingReview?.tags ?: emptyList())
    }

    var showFullName by remember(existingReview?.id) {
        mutableStateOf(!(existingReview?.isAnonymous ?: false))
    }

    val tags = listOf(
        "Sangat Responsif",
        "Tepat Waktu",
        "Jujur & Amanah",
        "Informasi Akurat",
        "Ramah & Sopan",
        "Verifikasi Teliti"
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(TemuinBackground)
    ) {

        ReviewTopBar(
            if (existingReview == null) {
                "Form Ulasan"
            } else {
                "Edit Ulasan"
            },
            onBack
        )

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // IDENTITAS PENGGUNA DAN LAPORAN

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        InitialAvatar(
                            targetUserName,
                            54
                        )

                        Column(
                            Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    targetUserName,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(Modifier.width(6.dp))
                            }

                            Text(
                                "245150400111050 • FILKOM UB",
                                color = TemuinMuted,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Text(
                        "${reviewContext.role.label}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TemuinMuted,
                        fontWeight = FontWeight.Bold
                    )


                    Surface(
                        color = TemuinPaleBlue,
                        shape = RoundedCornerShape(12.dp)
                    ) {

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(
                                Modifier.padding(start = 10.dp)
                            ) {

                                Text(
                                    "#${reviewContext.reportId} • Selesai",
                                    color = TemuinTeal,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    reviewContext.itemName,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    "Pos Satpam Gedung F",
                                    color = TemuinMuted,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // RATING BINTANG

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "Bagaimana Sikap & Integritas Pengguna?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        "Ketuk bintang untuk memberikan evaluasi objektif",
                        color = TemuinMuted,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {

                        (1..5).forEach { value ->

                            TextButton(
                                onClick = {
                                    rating = value
                                },
                                contentPadding = PaddingValues(1.dp)
                            ) {

                                Text(
                                    if (value <= rating) "★" else "☆",
                                    color = if (value <= rating) {
                                        TemuinOrange
                                    } else {
                                        Color(0xFFD5D9E0)
                                    },
                                    fontSize = 38.sp
                                )
                            }
                        }
                    }

                    if (rating > 0) {

                        Surface(
                            color = Color(0xFFFFE2C3),
                            shape = RoundedCornerShape(50)
                        ) {

                            Text(
                                "$rating.0 • ${
                                    when (rating) {
                                        1 -> "Buruk"
                                        2 -> "Perlu Ditingkatkan"
                                        3 -> "Baik"
                                        4 -> "Sangat Baik"
                                        else -> "Sangat Jujur & Terpercaya"
                                    }
                                }",
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 7.dp
                                ),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }

            // TAG PENILAIAN

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        "Pilih Penilaian Singkat",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                tags.chunked(2).forEach { rowTags ->

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {

                        rowTags.forEach { tag ->

                            FilterChip(
                                selected = tag in selectedTags,

                                onClick = {
                                    selectedTags =
                                        if (tag in selectedTags) {
                                            selectedTags - tag
                                        } else {
                                            selectedTags + tag
                                        }
                                },

                                label = {
                                    Text(
                                        tag,
                                        maxLines = 1
                                    )
                                },

                                shape = RoundedCornerShape(50)
                            )
                        }
                    }
                }
            }

            // ULASAN TERTULIS

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            "Ulasan Tertulis",
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.weight(1f))

                        Text(
                            "${comment.length} / 500",
                            color = TemuinMuted,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    OutlinedTextField(
                        value = comment,

                        onValueChange = {
                            if (it.length <= 500) {
                                comment = it
                            }
                        },

                        modifier = Modifier.fillMaxWidth(),

                        minLines = 4,

                        placeholder = {
                            Text(
                                "Ceritakan pengalaman interaksi kamu..."
                            )
                        },

                        shape = RoundedCornerShape(14.dp),

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TemuinTeal,
                            unfocusedBorderColor = TemuinBorder
                        )
                    )
                }
            }

            // PENGATURAN NAMA

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        Modifier.weight(1f)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                "Tampilkan Nama",
                                fontWeight = FontWeight.SemiBold
                            )

                            Icon(
                                Icons.Default.Visibility,
                                null,
                                tint = TemuinTeal,
                                modifier = Modifier
                                    .padding(start = 5.dp)
                                    .size(17.dp)
                            )
                        }

                        Text(
                            if (showFullName) {
                                "Tampil sebagai $currentUserName."
                            } else {
                                "Nama kamu disamarkan pada ulasan."
                            },
                            color = TemuinMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Switch(
                        checked = showFullName,
                        onCheckedChange = {
                            showFullName = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = TemuinTeal
                        )
                    )
                }
            }
        }

        // TOMBOL KIRIM ULASAN

        Surface(
            color = Color.White,
            shadowElevation = 8.dp
        ) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    )
            ) {

                Button(
                    onClick = {

                        val review = Review(
                            id = existingReview?.id
                                ?: "REV-${System.currentTimeMillis()}",

                            reviewerId = currentUserId,
                            reviewerName = currentUserName,

                            reviewedUserId = targetUserId,
                            reviewedUserName = targetUserName,

                            targetRole = reviewContext.role,
                            reportId = reviewContext.reportId,
                            itemName = reviewContext.itemName,

                            rating = rating,
                            comment = comment.trim(),
                            tags = selectedTags,

                            createdAt = existingReview?.createdAt
                                ?: "Baru saja",

                            isAnonymous = !showFullName
                        )

                        if (existingReview == null) {

                            viewModel.addReview(
                                review,
                                onSubmitted
                            )

                        } else {

                            viewModel.updateReview(
                                review,
                                onSubmitted
                            )
                        }
                    },

                    enabled = rating > 0 && comment.isNotBlank(),

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),

                    shape = RoundedCornerShape(50),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = TemuinTeal
                    )
                ) {

                    Icon(
                        Icons.Default.CheckCircle,
                        null
                    )

                    Text(
                        if (existingReview == null) {
                            "  Kirim Ulasan & Rating"
                        } else {
                            "  Simpan Perubahan"
                        }
                    )
                }

            }
        }
    }
}
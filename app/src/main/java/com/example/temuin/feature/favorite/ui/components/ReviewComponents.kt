package com.example.temuin.feature.favorite.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.temuin.feature.review.data.model.Review
import java.util.Locale

// DESIGN SYSTEM COLORS

val TemuinTeal = Color(0xFF006F68)
val TemuinBackground = Color(0xFFFAF8FF)
val TemuinMuted = Color(0xFF66716F)
val TemuinOrange = Color(0xFFFF9800)
val TemuinLavender = Color(0xFFE9EAFE)
val TemuinPaleBlue = Color(0xFFF0F2FF)
val TemuinBorder = Color(0xFFEDECF4)


// TOP BAR

@Composable
fun ReviewTopBar(
    title: String,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TemuinBackground)
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp,
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = Color(0xFF17202A)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = "ⓘ",
            modifier = Modifier.padding(end = 14.dp),
            color = TemuinMuted
        )

        Surface(
            shape = CircleShape,
            color = TemuinTeal,
            modifier = Modifier.size(34.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("♙", color = Color.White)
            }
        }

        Spacer(Modifier.width(8.dp))
    }
}


// AVATAR

@Composable
fun InitialAvatar(
    name: String,
    size: Int = 48,
) {
    val initials = name
        .trim()
        .split(" ")
        .asSequence()
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") {
            it.first().uppercase()
        }
        .ifBlank { "?" }

    Surface(
        modifier = Modifier.size(size.dp),
        shape = CircleShape,
        color = Color(0xFFDDEFEA)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = initials,
                color = TemuinTeal,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// RATING STARS

@Composable
fun RatingStars(
    rating: Int,
    starSize: Int = 18,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(5) { index ->

            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (index < rating) {
                    TemuinOrange
                } else {
                    Color(0xFFD7DCE1)
                },
                modifier = Modifier.size(starSize.dp),
            )
        }
    }
}


// RATING SUMMARY

@Composable
fun RatingSummary(
    reviews: List<Review>,
    compact: Boolean = false,
) {
    val count = reviews.size

    val average = if (count == 0) {
        0.0
    } else {
        reviews.asSequence().map { it.rating }.average()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    if (compact) 14.dp else 18.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Column(
                modifier = Modifier
                    .width(if (compact) 92.dp else 110.dp)
                    .background(
                        TemuinPaleBlue,
                        RoundedCornerShape(14.dp),
                    )
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = String.format(Locale.getDefault(), "%.1f", average),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                RatingStars(
                    rating = average.toInt().coerceIn(0, 5),
                    starSize = 14
                )

                Text(
                    text = "$count Ulasan",
                    color = TemuinMuted,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                (5 downTo 1).forEach { star ->

                    val number = reviews.count {
                        it.rating == star
                    }

                    val progress = if (count == 0) {
                        0f
                    } else {
                        number.toFloat() / count
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {

                        Text(
                            star.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = TemuinMuted
                        )

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .weight(1f)
                                .height(7.dp),
                            color = TemuinOrange,
                            trackColor = Color(0xFFE9EDFA)
                        )

                        Text(
                            number.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = TemuinMuted
                        )
                    }
                }
            }
        }
    }
}


// REVIEW CARD

@Composable
fun ReviewCard(
    review: Review,
    currentUserId: String,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    showActions: Boolean = true,
    showReviewedUser: Boolean = false,
) {

    val own = review.reviewerId == currentUserId

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // TUJUAN ULASAN
            if (showReviewedUser) {
                Surface(
                    color = TemuinPaleBlue,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 11.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Ulasan untuk",
                            color = TemuinMuted,
                            style = MaterialTheme.typography.labelMedium
                        )

                        Spacer(Modifier.width(4.dp))

                        Text(
                            text = review.reviewedUserName,
                            color = TemuinTeal,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            // IDENTITAS REVIEWER

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                InitialAvatar(
                    if (review.isAnonymous) {
                        "Pengguna Anonim"
                    } else {
                        review.reviewerName
                    },
                    42
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            if (review.isAnonymous) {
                                "Pengguna Anonim"
                            } else {
                                review.reviewerName
                            },
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(Modifier.width(4.dp))
                    }

                    Text(
                        if (review.targetRole.name == "REPORTER") {
                            "Pengaju Klaim"
                        } else {
                            "Pelapor Temuan"
                        },
                        color = TemuinMuted,
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        review.createdAt,
                        color = TemuinMuted,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        "★ ${review.rating}.0",
                        color = TemuinOrange,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // BARANG TERKAIT

            Surface(
                color = TemuinPaleBlue,
                shape = RoundedCornerShape(11.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Text(
                        "  Barang: ${review.itemName}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // KOMENTAR

            Text(
                text = "“${review.comment}”",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF202838)
            )

            // TAG PENILAIAN

            if (review.tags.isNotEmpty()) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    review.tags.take(2).forEach { tag ->

                        Surface(
                            color = TemuinLavender,
                            shape = RoundedCornerShape(8.dp)
                        ) {

                            Text(
                                tag,
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 5.dp
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF536078)
                            )
                        }
                    }
                }
            }

            // ACTIONS

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Spacer(Modifier.weight(1f))

                // ACTIONS
                if (showActions && own) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        TextButton(
                            onClick = onEdit,
                            contentPadding = PaddingValues(horizontal = 4.dp),
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )

                            Text("Edit")
                        }

                        TextButton(
                            onClick = onDelete,
                            contentPadding = PaddingValues(horizontal = 4.dp),
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color(0xFFB84B4B),
                                modifier = Modifier.size(15.dp)
                            )

                            Text(
                                "Hapus",
                                color = Color(0xFFB84B4B)
                            )
                        }
                    }
                }
            }
        }
    }
}
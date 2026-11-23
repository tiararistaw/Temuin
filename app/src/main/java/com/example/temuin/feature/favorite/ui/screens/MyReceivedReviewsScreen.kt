
package com.example.temuin.feature.favorite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.temuin.feature.review.data.model.Review
import com.example.temuin.feature.review.data.model.ReviewRole
import com.example.temuin.feature.review.ui.components.*
import com.example.temuin.feature.review.ui.viewmodel.ReviewViewModel

@Composable
fun MyReceivedReviewsScreen(
    viewModel: ReviewViewModel,
    currentUserId: String,
    currentUserName: String,
    currentNim: String,
    currentStudy: String,
    onBack: () -> Unit,
    onEditReview: (Review) -> Unit = {},
    onViewWrittenReviews: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsState()

    var selectedRating by remember {
        mutableStateOf<Int?>(null)
    }

    var selectedRole by remember {
        mutableStateOf("Semua")
    }

    var reviewToDelete by remember {
        mutableStateOf<Review?>(null)
    }

    // Mengambil ulasan pengguna yang sedang login
    LaunchedEffect(currentUserId) {
        viewModel.loadReviews(currentUserId)
    }

    LaunchedEffect(currentUserId) {
        viewModel.loadWrittenReviews(currentUserId)
    }

    val receivedReviews = uiState.reviews.filter {
        it.reviewedUserId == currentUserId
    }

    // Filter berdasarkan peran dan rating
    val filteredReviews = receivedReviews.filter { review ->

        val matchesRating =
            (selectedRating == null) ||
                    (review.rating == selectedRating)

        val matchesRole =
            (selectedRole == "Semua") ||
                    ((selectedRole == "Sebagai Pelapor") && (review.targetRole == ReviewRole.REPORTER)) ||
                    ((selectedRole == "Sebagai Pengaju") && (review.targetRole == ReviewRole.CLAIMANT))

        matchesRating && matchesRole
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TemuinBackground)
    ) {

        // TOP BAR
        ReviewTopBar(
            title = "Ulasan Saya",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 12.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // PROFIL PENGGUNA
            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    InitialAvatar(
                        currentUserName,
                        58
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = currentUserName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(Modifier.width(6.dp))
                        }

                        Text(
                            text = currentNim,
                            color = TemuinMuted,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = currentStudy,
                            color = TemuinMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // NAVIGASI ULASAN YANG DIBERIKAN
            item {
                OutlinedButton(
                    onClick = onViewWrittenReviews,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TemuinTeal
                    )
                ) {
                    Text("Lihat Ulasan yang Saya Berikan")
                }
            }

            // CARD REPUTASI KAMPUS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Reputasi Pengguna",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        RatingSummary(
                            reviews = receivedReviews,
                            compact = true
                        )

                        HorizontalDivider(
                            color = Color(0xFFEFEFEF)
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReputationBadge("Sangat Responsif")
                            ReputationBadge("Tepat Waktu")
                            ReputationBadge("Komunikasi Jelas")
                            ReputationBadge("Jujur & Amanah")
                        }
                    }
                }
            }

            // JUDUL FILTER PERAN
            item {

                Text(
                    text = "Peran",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // FILTER PERAN
            item {

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 4.dp)
                ) {

                    items(
                        listOf(
                            "Semua",
                            "Sebagai Pelapor",
                            "Sebagai Pengaju",
                        )
                    ) { label ->

                        FilterChip(
                            selected = selectedRole == label,
                            onClick = {
                                selectedRole = label
                            },
                            label = {
                                Text(
                                    text = label,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }
            }

            // JUDUL FILTER RATING
            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Rating",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "${filteredReviews.size} ulasan",
                        color = TemuinMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // FILTER RATING MENGGUNAKAN LAZYROW
            item {

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(
                        end = 8.dp
                    )
                ) {

                    val ratingOptions = listOf(
                        null, 5, 4, 3, 2, 1,
                    )

                    items(
                        items = ratingOptions,
                        key = { it ?: 0 }
                    ) { rating ->

                        FilterChip(
                            selected = selectedRating == rating,

                            onClick = {
                                selectedRating = rating
                            },

                            label = {

                                Text(
                                    text = if (rating == null) {
                                        "Semua"
                                    } else {
                                        "$rating ★"
                                    },
                                    maxLines = 1,
                                )
                            },
                        )
                    }
                }
            }

            // DAFTAR CARD ULASAN MENGGUNAKAN LAZYCOLUMN
            if (filteredReviews.isEmpty()) {

                item {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = if (receivedReviews.isEmpty()) {
                                    "Belum ada ulasan yang diterima."
                                } else {
                                    "Tidak ada ulasan dengan filter tersebut."
                                },
                                color = TemuinMuted,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

            } else {

                items(
                    items = filteredReviews,
                    key = { it.id }
                ) { review ->

                    ReviewCard(
                        review = review,
                        currentUserId = currentUserId,

                        onEdit = {
                            onEditReview(review)
                        },

                        onDelete = {
                            reviewToDelete = review
                        }
                    )
                }
            }
        }
    }

    // DIALOG KONFIRMASI HAPUS
    reviewToDelete?.let { review ->

        AlertDialog(
            onDismissRequest = {
                reviewToDelete = null
            },

            title = {
                Text("Hapus ulasan?")
            },

            text = {
                Text(
                    "Apakah kamu yakin ingin menghapus ulasan ini?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        viewModel.deleteReview(review)
                        reviewToDelete = null
                    }
                ) {

                    Text(
                        "Hapus",
                        color = Color(0xFFB84B4B)
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        reviewToDelete = null
                    }
                ) {

                    Text("Batal")
                }
            }
        )
    }
}


// BADGE REPUTASI
@Composable
private fun ReputationBadge(text: String) {

    Surface(
        color = TemuinLavender,
        shape = RoundedCornerShape(50),
    ) {

        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            color = Color(0xFF344054),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}


package com.example.temuin.feature.review.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.temuin.feature.review.data.model.Review
import com.example.temuin.feature.review.data.model.ReviewContext
import com.example.temuin.feature.review.data.model.ReviewRole
import com.example.temuin.feature.review.ui.components.*
import com.example.temuin.feature.review.ui.viewmodel.ReviewViewModel

@Composable
fun UserReviewsScreen(
    viewModel: ReviewViewModel,
    currentUserId: String,
    targetUserId: String,
    targetUserName: String,
    targetNim: String,
    targetStudy: String,
    reviewContext: ReviewContext?,
    canWriteReview: Boolean,
    onBack: () -> Unit,
    onWriteReview: (ReviewContext) -> Unit,
    onEditReview: (Review) -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    var selectedRating by remember {
        mutableStateOf<Int?>(null)
    }

    var selectedReviewScope by remember {
        mutableStateOf("Semua Ulasan")
    }

    var selectedRole by remember {
        mutableStateOf("Semua")
    }

    var reviewToDelete by remember {
        mutableStateOf<Review?>(null)
    }

    // Mengambil ulasan berdasarkan ID pengguna tujuan
    LaunchedEffect(targetUserId) {
        viewModel.loadReviews(targetUserId)
    }

    val allReviews = uiState.reviews.filter {
        it.reviewedUserId == targetUserId
    }

    // Filter berdasarkan rating dan peran
    val filtered = allReviews.filter { review ->

        val matchesScope =
            selectedReviewScope == "Semua Ulasan" ||
                    review.reviewerId == currentUserId

        val matchesRating =
            selectedRating == null ||
                    review.rating == selectedRating

        val matchesRole =
            selectedRole == "Semua" ||
                    (selectedRole == "Sebagai Pelapor" &&
                            review.targetRole == ReviewRole.REPORTER) ||
                    (selectedRole == "Sebagai Pengaju" &&
                            review.targetRole == ReviewRole.CLAIMANT)

        matchesScope && matchesRating && matchesRole
    }

    val average = if (allReviews.isEmpty()) {
        0.0
    } else {
        allReviews.map { it.rating }.average()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TemuinBackground)
    ) {

        // HEADER
        ReviewTopBar(
            "Profil Pengguna",
            onBack
        )

        // KONTEN HALAMAN
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // PROFIL PENGGUNA
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            InitialAvatar(
                                targetUserName,
                                68
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {

                                Text(
                                    targetUserName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = targetNim,
                                    color = TemuinMuted,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Text(
                                    text = targetStudy,
                                    color = TemuinMuted,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        // TOMBOL BERI ULASAN
                        if (canWriteReview && reviewContext != null) {

                            Button(
                                onClick = {
                                    onWriteReview(reviewContext)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TemuinTeal
                                )
                            ) {

                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(17.dp)
                                )

                                Spacer(Modifier.width(6.dp))

                                Text("Beri Ulasan")
                            }
                        }
                    }
                }
            }

            // REPUTASI PENGGUNA
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                "Reputasi Pengguna",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        RatingSummary(
                            allReviews,
                            compact = true
                        )
                    }
                }
            }

            // JUDUL ULASAN
            item {
                Text(
                    "Ulasan Komunitas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // FILTER JENIS ULASAN
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 8.dp)
                ) {
                    items(
                        listOf("Semua Ulasan", "Ulasan Saya")
                    ) { option ->

                        FilterChip(
                            selected = selectedReviewScope == option,
                            onClick = {
                                selectedReviewScope = option
                            },
                            label = {
                                Text(option, maxLines = 1)
                            }
                        )
                    }
                }
            }

            // FILTER PERAN
            item {

                Column(
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {

                    Text(
                        "Filter berdasarkan peran",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 8.dp)
                    ) {

                        items(
                            listOf(
                                "Semua",
                                "Sebagai Pelapor",
                                "Sebagai Pengaju"
                            )
                        ) { label ->

                            FilterChip(
                                selected = selectedRole == label,
                                onClick = {
                                    selectedRole = label
                                },
                                label = {
                                    Text(
                                        label,
                                        maxLines = 1
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // FILTER RATING
            item {

                Column(
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {

                    Text(
                        "Rating",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 8.dp)
                    ) {

                        items(
                            listOf<Int?>(null, 5, 4, 3, 2, 1)
                        ) { rating ->

                            FilterChip(
                                selected = selectedRating == rating,
                                onClick = {
                                    selectedRating = rating
                                },
                                label = {
                                    Text(
                                        if (rating == null) {
                                            "Semua"
                                        } else {
                                            "$rating ★"
                                        },
                                        maxLines = 1
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // DAFTAR ULASAN
            if (filtered.isEmpty()) {

                item {
                    Text(
                        "Belum ada ulasan untuk filter ini.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        color = TemuinMuted
                    )
                }

            } else {

                items(
                    items = filtered,
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

    // DIALOG HAPUS ULASAN
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
                    "Ulasan ini akan dihapus dari daftar reputasi pengguna."
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
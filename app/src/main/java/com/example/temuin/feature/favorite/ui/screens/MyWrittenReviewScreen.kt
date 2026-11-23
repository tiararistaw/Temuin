package com.example.temuin.feature.favorite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.temuin.feature.review.ui.components.*
import com.example.temuin.feature.review.ui.viewmodel.ReviewViewModel

@Composable
fun MyWrittenReviewsScreen(
    viewModel: ReviewViewModel,
    currentUserId: String,
    currentUserName: String,
    onBack: () -> Unit,
    onEditReview: (Review) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var reviewToDelete by remember {
        mutableStateOf<Review?>(null)
    }

    LaunchedEffect(currentUserId) {
        viewModel.loadWrittenReviews(currentUserId)
    }

    val writtenReviews = uiState.writtenReviews

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TemuinBackground)
    ) {
        ReviewTopBar(
            title = "Riwayat Ulasan",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 18.dp,
                vertical = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InitialAvatar(
                            name = currentUserName,
                            size = 46
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = currentUserName,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleSmall
                            )

                            Text(
                                text = "Total ulasan yang diberikan",
                                color = TemuinMuted,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Text(
                            text = "${writtenReviews.size}",
                            color = TemuinTeal,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Daftar Ulasan",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (writtenReviews.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Belum Ada Ulasan",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Ulasan yang Anda berikan kepada pengguna lain akan ditampilkan di sini.",
                                color = TemuinMuted,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            } else {
                items(
                    items = writtenReviews,
                    key = { it.id }
                ) { review ->
                    ReviewCard(
                        review = review,
                        currentUserId = currentUserId,
                        showActions = true,
                        showReviewedUser = true,
                        onEdit = {
                            onEditReview(review)
                        },
                        onDelete = {
                            reviewToDelete = review
                        }
                    )
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    reviewToDelete?.let { review ->
        AlertDialog(
            onDismissRequest = {
                reviewToDelete = null
            },
            title = {
                Text("Hapus Ulasan?")
            },
            text = {
                Text("Apakah Anda yakin ingin menghapus ulasan untuk ${review.reviewedUserName}?")
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
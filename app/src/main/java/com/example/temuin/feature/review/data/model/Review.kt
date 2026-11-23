package com.example.temuin.feature.review.data.model

data class Review(
    val id: String,
    val reviewerId: String,
    val reviewerName: String,
    val reviewedUserId: String,
    val reviewedUserName: String,
    val targetRole: ReviewRole,
    val reportId: String,
    val itemName: String,
    val rating: Int,
    val comment: String,
    val tags: List<String>,
    val createdAt: String,
    val isAnonymous: Boolean = false,
    val helpfulCount: Int = 0,
)

enum class ReviewRole(val label: String) {
    REPORTER("Pelapor / Penemu"),
    CLAIMANT("Pengaju Klaim / Pemilik Barang"),
}

data class ReviewContext(
    val role: ReviewRole,
    val reportId: String,
    val itemName: String,
)
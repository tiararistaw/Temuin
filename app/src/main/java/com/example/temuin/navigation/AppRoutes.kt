package com.example.temuin.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable data object Home : Screen
    @Serializable data object Search : Screen
    @Serializable data class History(val tab: String = "Semua") : Screen
    @Serializable data object Profile : Screen
    @Serializable data object ProfileEdit : Screen
    @Serializable data object Favorites : Screen
    @Serializable data class FavoriteDetail(val reportId: String) : Screen
    @Serializable data object ReportAdd : Screen
    @Serializable data object ReportLost : Screen
    @Serializable data class ReportEdit(val reportId: String) : Screen
    @Serializable data class ReportSuccess(val reportId: String) : Screen
    @Serializable data class ReportDetail(val reportId: String) : Screen
    @Serializable data class ReportProgress(val reportId: String) : Screen
    @Serializable data class ClaimForm(val itemName: String) : Screen
    @Serializable data class ClaimDetail(val claimId: String) : Screen
    @Serializable data object IncomingClaims : Screen
    @Serializable data class UserReviews(val targetUserId: String) : Screen
    @Serializable data object MyReceivedReviews : Screen
    @Serializable data object MyWrittenReviews : Screen
    @Serializable data class ReviewForm(
        val reviewId: String? = null,
        val role: String = "",
        val reportId: String = "",
        val itemName: String = "",
        val recipientId: String = "",
        val recipientName: String = ""
    ) : Screen
    @Serializable data object Login : Screen
    @Serializable data object Register : Screen
}

package com.example.temuin.feature.favorite.data.model

enum class ReportType(val label: String) {
    FOUND("Ditemukan"),
    LOST("Hilang")
}

data class FavoriteReport(
    val reportId: String,
    val itemName: String,
    val category: String,
    val type: ReportType,
    val location: String,
    val description: String,
    val timeLabel: String,
    val reporterName: String,
    val contact: String = "",
    val photoUrl: String? = null
)

package com.example.temuin.feature.favorite.data.repository

import com.example.temuin.feature.favorite.data.model.FavoriteReport
import com.example.temuin.feature.favorite.data.model.ReportType

class DummyFavoriteRepository : FavoriteRepository {

    private val favorites = mutableListOf(
        FavoriteReport(
            "RPT-01", "Kunci Motor Honda Vario", "Kunci & Aksesoris",
            ReportType.FOUND, "Gazebo Gedung F FILKOM",
            "Kunci motor Honda Vario dengan gantungan boneka Dino.",
            "2 jam lalu", "Rian Pratama", "0812-0000-1111"
        ),
        FavoriteReport(
            "RPT-02", "Tumbler Corkcicle Tosca", "Perlengkapan Pribadi",
            ReportType.LOST, "R. B1.2 Gedung B",
            "Tumbler tosca, ada sticker hologram astronot kecil di bagian bawah.",
            "Kemarin", "Nadhira Putri", "0812-0000-4444"
        ),
        FavoriteReport(
            "RPT-03", "Charger Laptop Asus Type-C", "Elektronik",
            ReportType.FOUND, "Lab Komputer 3",
            "Charger Asus Type-C warna hitam, kabel dililit selotip.",
            "3 jam lalu", "Bagas Daniswara", "0812-0000-5555"
        ),
        FavoriteReport(
            "RPT-04", "Kalkulator Casio fx-991", "Elektronik",
            ReportType.FOUND, "Musholla FILKOM",
            "Kalkulator Casio fx-991 dengan nama tertulis di belakang.",
            "5 jam lalu", "Tiara Aristawidya", "0812-0000-2222"
        )
    )

    override suspend fun getFavorites(userId: String): List<FavoriteReport> = favorites.toList()

    override suspend fun addFavorite(userId: String, reportId: String) { /* diisi di Fase B */ }

    override suspend fun removeFavorite(userId: String, reportId: String) {
        favorites.removeAll { it.reportId == reportId }
    }
}

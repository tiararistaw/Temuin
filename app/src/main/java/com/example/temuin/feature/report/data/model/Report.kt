package com.example.temuin.feature.report.data.model

/** Jenis laporan: barang hilang atau barang ditemukan. */
enum class ReportType(val label: String) {
    LOST("Barang Hilang"),
    FOUND("Barang Ditemukan")
}

/** Status siklus hidup sebuah laporan. */
enum class ReportStatus(val label: String) {
    ACTIVE("Aktif"),
    CLAIM_PENDING("Ada Pengajuan Klaim"),
    HANDOVER_PENDING("Menunggu Serah Terima"),
    COMPLETED("Selesai")
}

/** Daftar kategori barang (sesuai chip pada form desain). */
val ReportCategories = listOf(
    "Elektronik",
    "Dompet & Dokumen",
    "Kunci & Aksesoris",
    "Pakaian & Sandang",
    "Tas & Perlengkapan",
    "Buku & Alat Tulis",
    "Lainnya"
)

/** Saran titik kampus untuk kolom lokasi. */
val CampusSpots = listOf(
    "Perpustakaan Pusat Gd. A",
    "Kantin Asrama",
    "Masjid Raden Patah",
    "Gazebo Gedung F FILKOM",
    "Lab Komputer 3",
    "Pos Satpam FILKOM"
)

/**
 * Model utama laporan. Satu tabel `reports` di Supabase kelak memetakan
 * field ini 1:1 (photoUrls disimpan sebagai array teks / tabel relasi).
 */
data class Report(
    val id: String,
    val type: ReportType,
    val itemName: String,
    val category: String,
    val location: String,
    val occurredAtMillis: Long,
    val description: String,
    val photoUrls: List<String> = emptyList(),
    val isAnonymous: Boolean = false,
    val contact: String = "",
    val ownerId: String,
    val ownerName: String,
    val status: ReportStatus = ReportStatus.ACTIVE,
    val createdAtMillis: Long = System.currentTimeMillis()
)

/** Data isian form (dipakai untuk tambah maupun edit). */
data class ReportDraft(
    val type: ReportType,
    val itemName: String,
    val category: String,
    val location: String,
    val occurredAtMillis: Long,
    val description: String,
    val photoUrls: List<String>,
    val isAnonymous: Boolean,
    val contact: String
)
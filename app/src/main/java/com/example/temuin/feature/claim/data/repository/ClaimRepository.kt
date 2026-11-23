package com.example.temuin.feature.claim.data.repository

import com.example.temuin.feature.claim.data.model.Claim
import com.example.temuin.feature.claim.data.model.FoundItem

class ClaimRepository {

    // Dummy data barang temuan
    private val foundItems = mutableListOf(
        FoundItem(
            id = "FND-8821",
            name = "Kunci Motor Honda Vario & Gantungan Dino",
            category = "Kendaraan",
            location = "Gazebo Gedung F FILKOM",
            dateFound = "9 November 2026",
            description = "Kunci motor dengan gantungan boneka dino hijau.",
            finderName = "Rian Pratama", ownerId = "user-03"
        ),
        FoundItem(
            id = "FND-8822",
            name = "MacBook Air M2 Space Grey",
            category = "Elektronik",
            location = "Gedung F Lantai 3",
            dateFound = "8 November 2026",
            description = "Laptop berwarna Space Grey.",
            finderName = "Trisha Malina", ownerId = "user-01"
        ),
        FoundItem(
            id = "FND-8823",
            name = "Tumbler Corkcicle Turquoise",
            category = "Aksesoris",
            location = "Kantin Baru FILKOM",
            dateFound = "7 November 2026",
            description = "Tumbler berwarna turquoise.",
            finderName = "Dina Putri", ownerId = "user-04"
        ),
        FoundItem(
            id = "FND-8824",
            name = "Dompet Kulit Warna Hitam",
            category = "Aksesoris",
            location = "Perpustakaan FILKOM",
            dateFound = "6 November 2026",
            description = "Dompet kulit berwarna hitam.",
            finderName = "Andi Saputra", ownerId = "user-05"
        ),
        FoundItem(
            id = "FND-8825",
            name = "Dompet Kulit Hitam",
            category = "Dompet & Dokumen",
            location = "Kantin FEB UB",
            dateFound = "14 November 2026",
            description = "Dompet kulit hitam berisi kartu mahasiswa dan uang tunai.",
            finderName = "Trisha Malina",
            ownerId = "user-01"
        )
    )

    // Penyimpanan sementara pengajuan klaim
    private val claims = mutableListOf(
        Claim(
            id = "CLM-2044",
            userId = "user-02", // Ailsa Nasywa
            itemId = "FND-8825",
            itemName = "Dompet Kulit Hitam",
            claimDescription = "Di dalam dompet ada kartu perpustakaan dan foto polaroid kecil. Jahitan sudut kanan sedikit terlepas.",
            claimDate = "14 November 2026",
            status = "Menunggu",
            reportOwnerId = "user-01", // Trisha Malina
            reportOwnerName = "Trisha Malina",
            claimantName = "Ailsa Nasywa"
        )
    )

    // Contoh klaim yang diajukan pengguna aktif pada laporan milik pengguna lain.
    private val ownDemoClaim = Claim(
        id = "CLM-2038",
        userId = "user-01",
        itemId = "FND-8821",
        itemName = "Kunci Motor Honda Vario & Gantungan Dino",
        claimDescription = "Kunci motor dengan gantungan dino hijau. Ada goresan kecil di sisi kepala kunci.",
        claimDate = "13 November 2026",
        status = "Diproses",
        reportOwnerId = "user-03",
        reportOwnerName = "Rian Pratama",
        claimantName = "Trisha Malina"
    )

    init { claims.add(ownDemoClaim) }

    // Mengambil seluruh barang temuan
    fun getFoundItems(): List<FoundItem> {
        return foundItems.toList()
    }

    // Mengambil detail barang berdasarkan ID
    fun getFoundItemById(itemId: String): FoundItem? {
        return foundItems.find { it.id == itemId }
    }

    // Mengambil riwayat klaim pengguna
    fun getClaimsByUser(userId: String): List<Claim> {
        return claims.filter { it.userId == userId }
    }

    // Mengirim pengajuan klaim
    fun submitClaim(claim: Claim) {
        claims.add(claim)
    }

    // Mengambil detail klaim berdasarkan ID
    fun getClaimById(claimId: String): Claim? {
        return claims.find { it.id == claimId }
    }

    /** Klaim masuk yang harus ditanggapi pemilik laporan temuan. */
    fun getIncomingClaims(reportOwnerId: String): List<Claim> =
        claims.filter { it.reportOwnerId == reportOwnerId }

    fun updateClaimStatus(claimId: String, status: String, rejectionNote: String? = null): Claim? {
        val index = claims.indexOfFirst { it.id == claimId }
        if (index < 0) return null
        val updated = claims[index].copy(status = status, rejectionNote = rejectionNote)
        claims[index] = updated
        return updated
    }

    /** Penyelesaian hanya dicatat setelah konfirmasi serah terima. */
    fun confirmHandover(claimId: String): Claim? {
        val index = claims.indexOfFirst { it.id == claimId }
        if (index < 0) return null
        val updated = claims[index].copy(
            status = "Selesai", handoverConfirmed = true,
            completedAtMillis = System.currentTimeMillis()
        )
        claims[index] = updated
        return updated
    }
}

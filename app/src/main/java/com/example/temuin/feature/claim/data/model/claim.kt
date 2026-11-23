package com.example.temuin.feature.claim.data.model

data class Claim(
    val id: String,
    val userId: String,
    val itemId: String,
    val itemName: String,
    val claimDescription: String,
    val whatsapp: String = "",
    val pickupLocation: String = "",
    val evidenceUri: String? = null,
    val claimDate: String,
    val status: String = "Menunggu",
    /** Pemilik laporan temuan yang dapat merespons klaim; bukan akun admin. */
    val reportOwnerId: String = "",
    val reportOwnerName: String = "",
    val claimantName: String = "",
    val rejectionNote: String? = null,
    val handoverConfirmed: Boolean = false,
    val completedAtMillis: Long? = null
)

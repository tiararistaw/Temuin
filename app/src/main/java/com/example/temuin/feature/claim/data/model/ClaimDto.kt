
package com.example.temuin.feature.claim.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Model untuk membaca data dari tabel claims Supabase.
 */
@Serializable
data class ClaimRow(
    val id: String,

    @SerialName("report_id")
    val reportId: String,

    @SerialName("claimant_id")
    val claimantId: String,

    @SerialName("item_name")
    val itemName: String,

    @SerialName("claim_description")
    val claimDescription: String,

    val whatsapp: String? = null,

    @SerialName("pickup_location")
    val pickupLocation: String? = null,

    @SerialName("evidence_url")
    val evidenceUrl: String? = null,

    @SerialName("report_owner_id")
    val reportOwnerId: String? = null,

    @SerialName("report_owner_name")
    val reportOwnerName: String? = null,

    @SerialName("claimant_name")
    val claimantName: String? = null,

    val status: String = "Menunggu",

    @SerialName("rejection_note")
    val rejectionNote: String? = null,

    @SerialName("handover_confirmed")
    val handoverConfirmed: Boolean = false,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("completed_at")
    val completedAt: String? = null
)

/**
 * Model untuk mengirim pengajuan klaim ke Supabase.
 * Kolom lain diisi otomatis oleh database.
 */
@Serializable
data class ClaimInsert(
    @SerialName("report_id")
    val reportId: String,

    @SerialName("claim_description")
    val claimDescription: String,

    val whatsapp: String? = null,

    @SerialName("pickup_location")
    val pickupLocation: String? = null,

    @SerialName("evidence_url")
    val evidenceUrl: String? = null,

    @SerialName("claimant_name")
    val claimantName: String? = null
)

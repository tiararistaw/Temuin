
package com.example.temuin.feature.claim.data.repository

import com.example.temuin.SupabaseClient
import com.example.temuin.feature.claim.data.model.ClaimInsert
import com.example.temuin.feature.claim.data.model.ClaimRow
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class SupabaseClaimRepository {

    private val supabase = SupabaseClient.client

    private val table
        get() = supabase.from("claims")

    /**
     * Mengirim pengajuan klaim baru.
     */
    suspend fun submitClaim(
        reportId: String,
        claimDescription: String,
        whatsapp: String,
        pickupLocation: String,
        evidenceUrl: String? = null,
        claimantName: String? = null
    ): ClaimRow {

        val currentUser = supabase.auth.currentUserOrNull()
            ?: error("Silakan login terlebih dahulu.")

        val payload = ClaimInsert(
            reportId = reportId,
            claimDescription = claimDescription,
            whatsapp = whatsapp,
            pickupLocation = pickupLocation,
            evidenceUrl = evidenceUrl,
            claimantName = claimantName
        )

        return table.insert(payload) {
            select()
        }.decodeSingle<ClaimRow>()
    }

    /**
     * Mengambil riwayat klaim milik pengguna yang sedang login.
     */
    suspend fun getMyClaims(): List<ClaimRow> {

        val currentUser = supabase.auth.currentUserOrNull()
            ?: error("Sesi login tidak ditemukan.")

        return table.select {
            filter {
                eq("claimant_id", currentUser.id)
            }
        }.decodeList<ClaimRow>()
    }

    /**
     * Mengambil klaim yang masuk pada laporan milik pengguna.
     */
    suspend fun getIncomingClaims(): List<ClaimRow> {

        val currentUser = supabase.auth.currentUserOrNull()
            ?: error("Sesi login tidak ditemukan.")

        return table.select {
            filter {
                eq("report_owner_id", currentUser.id)
            }
        }.decodeList<ClaimRow>()
    }

    /**
     * Mengambil detail klaim berdasarkan ID.
     */
    suspend fun getClaimById(claimId: String): ClaimRow? {

        return table.select {
            filter {
                eq("id", claimId)
            }
        }.decodeSingleOrNull<ClaimRow>()
    }

    /**
     * Memperbarui status klaim.
     */
    suspend fun updateClaimStatus(
        claimId: String,
        status: String,
        rejectionNote: String? = null
    ): ClaimRow {

        return table.update({
            set("status", status)
            set("rejection_note", rejectionNote)
        }) {
            select()
            filter {
                eq("id", claimId)
            }
        }.decodeSingle<ClaimRow>()
    }
}

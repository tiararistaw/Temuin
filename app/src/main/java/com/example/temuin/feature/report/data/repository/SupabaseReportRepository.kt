package com.example.temuin.feature.report.data.repository

import com.example.temuin.SupabaseClient
import com.example.temuin.feature.report.data.model.Report
import com.example.temuin.feature.report.data.model.ReportDraft
import com.example.temuin.feature.report.data.model.ReportInsert
import com.example.temuin.feature.report.data.model.ReportRow
import com.example.temuin.feature.report.data.model.ReportStatus
import com.example.temuin.feature.report.data.model.ReportUpdate
import com.example.temuin.feature.report.data.model.toIsoString
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.result.PostgrestResult
import io.github.jan.supabase.storage.storage
import java.util.UUID

/**
 * Implementasi ReportRepository memakai Supabase (tabel `reports` + bucket Storage `report-photos`).
 */
class SupabaseReportRepository : ReportRepository {

    private val supabase = SupabaseClient.client
    private val table get() = supabase.from("reports")
    private val bucket get() = supabase.storage.from(PHOTO_BUCKET)

    // ---------------- READ ----------------

    override suspend fun getReports(): List<Report> =
        table.select {
            order("created_at", Order.DESCENDING)
        }.decodeList<ReportRow>().map { it.toReport() }

    override suspend fun getReportById(reportId: String): Report? =
        table.select {
            filter { eq("id", reportId) }
        }.decodeSingleOrNull<ReportRow>()?.toReport()

    // ---------------- INSERT ----------------

    override suspend fun createReport(
        draft: ReportDraft,
        ownerId: String,
        ownerName: String,
        photoBytes: List<ByteArray>
    ): Report {
        val uploaded = uploadPhotos(ownerId, photoBytes)
        val payload = ReportInsert(
            type = draft.type.name,
            itemName = draft.itemName,
            category = draft.category,
            location = draft.location,
            occurredAt = draft.occurredAtMillis.toIsoString(),
            description = draft.description,
            photoUrls = keepRemoteUrls(draft.photoUrls) + uploaded,
            isAnonymous = draft.isAnonymous,
            contact = draft.contact,
            ownerId = ownerId,
            ownerName = ownerName
        )
        return table.insert(payload) { select() }
            .decodeSingle<ReportRow>()
            .toReport()
    }

    // ---------------- UPDATE ----------------

    override suspend fun updateReport(
        reportId: String,
        draft: ReportDraft,
        photoBytes: List<ByteArray>
    ): Report {
        val ownerId = supabase.auth.currentUserOrNull()?.id
            ?: error("Sesi login tidak ditemukan.")
        val uploaded = uploadPhotos(ownerId, photoBytes)
        val payload = ReportUpdate(
            type = draft.type.name,
            itemName = draft.itemName,
            category = draft.category,
            location = draft.location,
            occurredAt = draft.occurredAtMillis.toIsoString(),
            description = draft.description,
            // foto lama yang tidak dihapus (http) + foto baru yang baru diunggah
            photoUrls = keepRemoteUrls(draft.photoUrls) + uploaded,
            isAnonymous = draft.isAnonymous,
            contact = draft.contact
        )
        return updateRow(reportId) { table.update(payload) { select(); filter { eq("id", reportId) } } }
    }

    override suspend fun updateStatus(reportId: String, status: ReportStatus): Report =
        updateRow(reportId) {
            table.update({ set("status", status.name) }) {
                select()
                filter { eq("id", reportId) }
            }
        }

    override suspend fun markCompleted(reportId: String): Report =
        updateStatus(reportId, ReportStatus.COMPLETED)

    // ---------------- DELETE ----------------

    override suspend fun deleteReport(reportId: String): Boolean =
        table.delete {
            select()
            filter { eq("id", reportId) }
        }.decodeList<ReportRow>().isNotEmpty()

    // ---------------- helper ----------------

    /** Foto lama (sudah URL http) dipertahankan; URI lokal content:// diabaikan karena diunggah terpisah. */
    private fun keepRemoteUrls(urls: List<String>) = urls.filter { it.startsWith("http") }

    /** Unggah foto terkompres ke Storage, kembalikan daftar URL publiknya. */
    private suspend fun uploadPhotos(ownerId: String, photos: List<ByteArray>): List<String> =
        photos.map { bytes ->
            val path = "$ownerId/${UUID.randomUUID()}.jpg"
            bucket.upload(path, bytes) { upsert = false }
            bucket.publicUrl(path)
        }

    /** Jika RLS menolak (bukan pemilik), Supabase mengembalikan 0 baris -> beri pesan jelas. */
    private suspend fun updateRow(
        reportId: String,
        block: suspend () -> PostgrestResult
    ): Report {
        val rows = block().decodeList<ReportRow>()
        return rows.firstOrNull()?.toReport()
            ?: error("Laporan $reportId tidak ditemukan atau bukan milik kamu.")
    }

    private companion object {
        const val PHOTO_BUCKET = "report-photos"
    }
}
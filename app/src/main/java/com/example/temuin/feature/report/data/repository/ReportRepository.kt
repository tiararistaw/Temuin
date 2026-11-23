package com.example.temuin.feature.report.data.repository

import com.example.temuin.feature.report.data.model.Report
import com.example.temuin.feature.report.data.model.ReportDraft

/**
 * Kontrak data fitur Lapor Barang.
 * Implementasi Supabase nantinya cukup mengganti DummyReportRepository.
 */
interface ReportRepository {
    /** READ: seluruh laporan (hilang + ditemukan), terbaru di atas. */
    suspend fun getReports(): List<Report>

    /** READ: detail satu laporan. */
    suspend fun getReportById(reportId: String): Report?

    /**
     * INSERT: membuat laporan baru.
     * [photoBytes] = foto terkompres untuk diunggah ke Supabase Storage.
     */
    suspend fun createReport(
        draft: ReportDraft,
        ownerId: String,
        ownerName: String,
        photoBytes: List<ByteArray> = emptyList()
    ): Report

    /** UPDATE: memperbarui laporan milik pengguna yang sedang login. */
    suspend fun updateReport(
        reportId: String,
        draft: ReportDraft,
        photoBytes: List<ByteArray> = emptyList()
    ): Report

    /** UPDATE: mengubah status proses laporan. */
    suspend fun updateStatus(reportId: String, status: com.example.temuin.feature.report.data.model.ReportStatus): Report

    /** UPDATE: menandai laporan selesai. */
    suspend fun markCompleted(reportId: String): Report

    /** DELETE: menghapus laporan milik sendiri yang belum diklaim. */
    suspend fun deleteReport(reportId: String): Boolean
}
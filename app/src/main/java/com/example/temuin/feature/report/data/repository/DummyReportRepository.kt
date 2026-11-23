package com.example.temuin.feature.report.data.repository

import com.example.temuin.feature.report.data.model.Report
import com.example.temuin.feature.report.data.model.ReportDraft
import com.example.temuin.feature.report.data.model.ReportStatus
import com.example.temuin.feature.report.data.model.ReportType
import kotlinx.coroutines.delay

class DummyReportRepository : ReportRepository {

    private val now = System.currentTimeMillis()
    private val hour = 60 * 60 * 1000L

    private var counter = 2041

    private val reports = mutableListOf(
        Report(
            id = "FND-8821", type = ReportType.FOUND,
            itemName = "Kunci Motor Honda Vario & Gantungan Dino",
            category = "Kunci & Aksesoris", location = "Gazebo Gedung F FILKOM",
            occurredAtMillis = now - 2 * hour,
            description = "Kunci motor dengan gantungan boneka dino hijau.",
            contact = "0812-0000-1111",
            ownerId = "user-03", ownerName = "Rian Pratama",
            createdAtMillis = now - 2 * hour
        ),
        Report(
            id = "FND-8822", type = ReportType.FOUND,
            itemName = "MacBook Air M2 Space Grey",
            category = "Elektronik", location = "Gedung F Lantai 3",
            occurredAtMillis = now - 26 * hour,
            description = "Laptop Space Grey, ada stiker kecil di bagian cover.",
            contact = "0812-0000-2222",
            ownerId = "user-01", ownerName = "Trisha Malina",
            createdAtMillis = now - 26 * hour
        ),
        Report(
            id = "FND-8824", type = ReportType.FOUND,
            itemName = "Dompet Kulit Warna Hitam",
            category = "Dompet & Dokumen", location = "Perpustakaan FILKOM",
            occurredAtMillis = now - 50 * hour,
            description = "Dompet kulit hitam berisi beberapa kartu.",
            isAnonymous = true, contact = "0812-0000-3333",
            ownerId = "user-04", ownerName = "Andi Saputra",
            createdAtMillis = now - 50 * hour
        ),
        Report(
            id = "LST-3001", type = ReportType.LOST,
            itemName = "Tumbler Corkcicle Tosca",
            category = "Tas & Perlengkapan", location = "R. B1.2 Gedung B",
            occurredAtMillis = now - 30 * hour,
            description = "Tumbler tosca, ada sticker hologram astronot kecil di bagian bawah.",
            contact = "0812-0000-4444",
            ownerId = "user-05", ownerName = "Nadhira Putri",
            createdAtMillis = now - 30 * hour
        ),
        Report(
            id = "FND-8825", type = ReportType.FOUND,
            itemName = "Dompet Kulit Hitam",
            category = "Dompet & Dokumen", location = "Kantin FEB UB",
            occurredAtMillis = now - 24 * hour,
            description = "Dompet kulit hitam berisi kartu mahasiswa dan uang tunai. Pemilik dapat menyebutkan ciri khusus untuk verifikasi.",
            contact = "0812-0000-5555",
            ownerId = "user-01", ownerName = "Trisha Malina",
            status = ReportStatus.CLAIM_PENDING,
            createdAtMillis = now - 23 * hour
        ),
        Report(
            id = "LST-3002", type = ReportType.LOST,
            itemName = "Kartu Tanda Mahasiswa a.n. Dennisa Syafa",
            category = "Dompet & Dokumen", location = "Kantin Asrama",
            occurredAtMillis = now - 72 * hour,
            description = "KTM terselip di casing HP, hilang sekitar waktu makan siang.",
            contact = "0812-9844-0000",
            ownerId = "user-02", ownerName = "Dennisa Syafa",
            createdAtMillis = now - 72 * hour
        )
    )

    override suspend fun getReports(): List<Report> =
        reports.sortedByDescending { it.createdAtMillis }

    override suspend fun getReportById(reportId: String): Report? =
        reports.find { it.id == reportId }

    override suspend fun createReport(
        draft: ReportDraft,
        ownerId: String,
        ownerName: String,
        photoBytes: List<ByteArray>
    ): Report {
        delay(600) // simulasi jaringan
        val prefix = if (draft.type == ReportType.FOUND) "FND" else "LST"
        val report = Report(
            id = "$prefix-${counter++}",
            type = draft.type,
            itemName = draft.itemName,
            category = draft.category,
            location = draft.location,
            occurredAtMillis = draft.occurredAtMillis,
            description = draft.description,
            // Fase Supabase: unggah photoBytes ke Storage lalu simpan URL publiknya.
            photoUrls = draft.photoUrls,
            isAnonymous = draft.isAnonymous,
            contact = draft.contact,
            ownerId = ownerId,
            ownerName = ownerName
        )
        reports.add(0, report)
        return report
    }

    override suspend fun updateReport(
        reportId: String,
        draft: ReportDraft,
        photoBytes: List<ByteArray>
    ): Report {
        delay(400)
        val index = reports.indexOfFirst { it.id == reportId }
        require(index >= 0) { "Laporan tidak ditemukan" }
        val updated = reports[index].copy(
            type = draft.type,
            itemName = draft.itemName,
            category = draft.category,
            location = draft.location,
            occurredAtMillis = draft.occurredAtMillis,
            description = draft.description,
            photoUrls = draft.photoUrls,
            isAnonymous = draft.isAnonymous,
            contact = draft.contact
        )
        reports[index] = updated
        return updated
    }

    override suspend fun updateStatus(reportId: String, status: ReportStatus): Report {
        val index = reports.indexOfFirst { it.id == reportId }
        require(index >= 0) { "Laporan tidak ditemukan" }
        val updated = reports[index].copy(status = status)
        reports[index] = updated
        return updated
    }

    override suspend fun markCompleted(reportId: String): Report =
        updateStatus(reportId, ReportStatus.COMPLETED)

    override suspend fun deleteReport(reportId: String): Boolean {
        delay(300)
        val index = reports.indexOfFirst { it.id == reportId }
        return if (index >= 0) {
            reports.removeAt(index)
            true
        } else false
    }
}
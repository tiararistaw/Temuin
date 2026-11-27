
package com.example.temuin.feature.claim.data.repository

import com.example.temuin.SupabaseClient
import com.example.temuin.feature.claim.data.model.FoundItem
import com.example.temuin.feature.report.data.model.ReportRow
import io.github.jan.supabase.postgrest.from

class SupabaseFoundItemRepository {

    private val supabase = SupabaseClient.client
    private val table get() = supabase.from("reports")

    // Mengambil daftar laporan barang temuan dari Supabase
    suspend fun getFoundItems(): List<FoundItem> {

        return table.select {
            filter {
                eq("type", "FOUND")
            }
        }.decodeList<ReportRow>().map { report ->

            FoundItem(
                id = report.id,
                name = report.itemName,
                category = report.category,
                location = report.location,
                dateFound = report.occurredAt.substringBefore("T"),
                description = report.description,
                finderName = report.ownerName,
                status = if (report.status == "ACTIVE") "Tersedia" else report.status,
                ownerId = report.ownerId
            )
        }
    }

    // Mengambil detail barang temuan berdasarkan ID
    suspend fun getFoundItemById(itemId: String): FoundItem? {

        return table.select {
            filter {
                eq("id", itemId)
                eq("type", "FOUND")
            }
        }.decodeSingleOrNull<ReportRow>()?.let { report ->

            FoundItem(
                id = report.id,
                name = report.itemName,
                category = report.category,
                location = report.location,
                dateFound = report.occurredAt.substringBefore("T"),
                description = report.description,
                finderName = report.ownerName,
                status = if (report.status == "ACTIVE") "Tersedia" else report.status,
                ownerId = report.ownerId
            )
        }
    }
}

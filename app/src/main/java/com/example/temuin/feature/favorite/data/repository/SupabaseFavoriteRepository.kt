package com.example.temuin.feature.favorite.data.repository

import com.example.temuin.SupabaseClient
import com.example.temuin.feature.favorite.data.model.FavoriteReport
import com.example.temuin.feature.favorite.data.model.ReportType
import com.example.temuin.feature.report.data.model.ReportRow
import com.example.temuin.feature.report.util.relativeTime
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class FavoriteIdRow(
    @SerialName("report_id") val reportId: String
)

@Serializable
internal data class FavoriteInsert(
    @SerialName("user_id") val userId: String,
    @SerialName("report_id") val reportId: String
)

/**
 * Favorit disimpan di tabel `favorites` (user_id, report_id).
 * Detail laporan diambil dari tabel `reports`, jadi tidak ada data ganda.
 * userId dari parameter diabaikan; yang dipakai adalah sesi login Supabase,
 * supaya aman walau UI masih mengirim "user-01" saat belum login.
 */
class SupabaseFavoriteRepository : FavoriteRepository {

    private val supabase = SupabaseClient.client

    private fun currentUid(): String? = supabase.auth.currentUserOrNull()?.id

    override suspend fun getFavoriteIds(userId: String): Set<String> {
        val uid = currentUid() ?: return emptySet()
        return supabase.from("favorites")
            .select { filter { eq("user_id", uid) } }
            .decodeList<FavoriteIdRow>()
            .map { it.reportId }
            .toSet()
    }

    override suspend fun getFavorites(userId: String): List<FavoriteReport> {
        val uid = currentUid() ?: return emptyList()
        val ids = getFavoriteIds(uid).toList()
        if (ids.isEmpty()) return emptyList()

        return supabase.from("reports")
            .select { filter { isIn("id", ids) } }
            .decodeList<ReportRow>()
            .map { it.toReport() }
            .sortedByDescending { it.createdAtMillis }
            .map { r ->
                val hideName = r.isAnonymous && r.ownerId != uid
                FavoriteReport(
                    reportId = r.id,
                    itemName = r.itemName,
                    category = r.category,
                    type = if (r.type.name == "LOST") ReportType.LOST else ReportType.FOUND,
                    location = r.location,
                    description = r.description,
                    timeLabel = relativeTime(r.createdAtMillis),
                    reporterName = if (hideName) "Pengguna Anonim" else r.ownerName,
                    contact = r.contact,
                    photoUrl = r.photoUrls.firstOrNull()
                )
            }
    }

    override suspend fun addFavorite(userId: String, reportId: String) {
        val uid = currentUid() ?: error("Silakan login terlebih dahulu.")
        if (reportId in getFavoriteIds(uid)) return   // sudah favorit, tidak perlu insert lagi
        supabase.from("favorites").insert(FavoriteInsert(uid, reportId))
    }

    override suspend fun removeFavorite(userId: String, reportId: String) {
        val uid = currentUid() ?: return
        supabase.from("favorites").delete {
            filter {
                eq("user_id", uid)
                eq("report_id", reportId)
            }
        }
    }
}
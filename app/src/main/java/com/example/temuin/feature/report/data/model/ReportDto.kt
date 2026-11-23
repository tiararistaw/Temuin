package com.example.temuin.feature.report.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.OffsetDateTime

/** Baris tabel `reports` di Supabase (hasil SELECT). */
@Serializable
data class ReportRow(
    val id: String,
    val type: String,
    @SerialName("item_name") val itemName: String,
    val category: String,
    val location: String,
    @SerialName("occurred_at") val occurredAt: String,
    val description: String,
    @SerialName("photo_urls") val photoUrls: List<String> = emptyList(),
    @SerialName("is_anonymous") val isAnonymous: Boolean = false,
    val contact: String = "",
    @SerialName("owner_id") val ownerId: String,
    @SerialName("owner_name") val ownerName: String,
    val status: String = "ACTIVE",
    @SerialName("created_at") val createdAt: String
) {
    fun toReport() = Report(
        id = id,
        type = runCatching { ReportType.valueOf(type) }.getOrDefault(ReportType.FOUND),
        itemName = itemName,
        category = category,
        location = location,
        occurredAtMillis = OffsetDateTime.parse(occurredAt).toInstant().toEpochMilli(),
        description = description,
        photoUrls = photoUrls,
        isAnonymous = isAnonymous,
        contact = contact,
        ownerId = ownerId,
        ownerName = ownerName,
        status = runCatching { ReportStatus.valueOf(status) }.getOrDefault(ReportStatus.ACTIVE),
        createdAtMillis = OffsetDateTime.parse(createdAt).toInstant().toEpochMilli()
    )
}

/** Payload INSERT. id, status, dan created_at diisi otomatis oleh database. */
@Serializable
data class ReportInsert(
    val type: String,
    @SerialName("item_name") val itemName: String,
    val category: String,
    val location: String,
    @SerialName("occurred_at") val occurredAt: String,
    val description: String,
    @SerialName("photo_urls") val photoUrls: List<String>,
    @SerialName("is_anonymous") val isAnonymous: Boolean,
    val contact: String,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("owner_name") val ownerName: String
)

/** Payload UPDATE isi laporan (owner_id dan status tidak ikut diubah). */
@Serializable
data class ReportUpdate(
    val type: String,
    @SerialName("item_name") val itemName: String,
    val category: String,
    val location: String,
    @SerialName("occurred_at") val occurredAt: String,
    val description: String,
    @SerialName("photo_urls") val photoUrls: List<String>,
    @SerialName("is_anonymous") val isAnonymous: Boolean,
    val contact: String
)

internal fun Long.toIsoString(): String = Instant.ofEpochMilli(this).toString()
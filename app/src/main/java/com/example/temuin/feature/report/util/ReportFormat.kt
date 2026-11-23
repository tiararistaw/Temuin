package com.example.temuin.feature.report.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

fun millisToDateTime(millis: Long): LocalDateTime =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()

fun toMillis(date: LocalDate, time: LocalTime): Long =
    LocalDateTime.of(date, time).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun formatDate(date: LocalDate): String = date.format(dateFormat)

fun formatTime(time: LocalTime): String = time.format(timeFormat)

/** Contoh: 11/06/2026, 15:07 WIB */
fun formatDateTimeWib(millis: Long): String {
    val dt = millisToDateTime(millis)
    return "${dt.format(dateFormat)}, ${dt.format(timeFormat)} WIB"
}

/** Label relatif singkat: "Baru Saja", "2 jam lalu", "Kemarin", atau tanggal. */
fun relativeTime(millis: Long, now: Long = System.currentTimeMillis()): String {
    val diff = now - millis
    val minute = 60_000L
    val hour = 60 * minute
    val day = 24 * hour
    return when {
        diff < 2 * minute -> "Baru Saja"
        diff < hour -> "${diff / minute} menit lalu"
        diff < day -> "${diff / hour} jam lalu"
        diff < 2 * day -> "Kemarin"
        diff < 7 * day -> "${diff / day} hari lalu"
        else -> formatDate(millisToDateTime(millis).toLocalDate())
    }
}
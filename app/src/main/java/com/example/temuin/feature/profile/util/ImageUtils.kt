package com.example.temuin.feature.profile.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

fun readAndCompress(context: Context, uri: Uri, maxWidth: Int = 800): ByteArray? {
    val bitmap = context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it)
    } ?: return null

    val scaled = if (bitmap.width > maxWidth) {
        val ratio = maxWidth.toFloat() / bitmap.width
        Bitmap.createScaledBitmap(bitmap, maxWidth, (bitmap.height * ratio).toInt(), true)
    } else bitmap

    val out = ByteArrayOutputStream()
    scaled.compress(Bitmap.CompressFormat.JPEG, 80, out)
    return out.toByteArray()
}
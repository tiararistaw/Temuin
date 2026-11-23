package com.example.temuin.feature.auth.data.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val nim: String = "245150400111000",
    val studyProgram: String = "Teknik Informatika '23",
    val phone: String = "081234567890",
    val avatarUrl: String? = null,
    val createdAt: String = "2026-11-15"
)

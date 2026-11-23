package com.example.temuin.feature.profile.data.model

data class UserProfile(
    val id: String,
    val fullName: String,
    val nim: String,
    val studyProgram: String,
    val phone: String,
    val avatarUrl: String? = null,
    val itemsReported: Int = 0,
    val claimsDone: Int = 0,
    val claimsOngoing: Int = 2,
    val rating: Double = 0.0,
    val reviewCount: Int = 0
)
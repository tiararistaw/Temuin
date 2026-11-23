package com.example.temuin.feature.profile.data.repository

import com.example.temuin.feature.profile.data.model.UserProfile

class DummyProfileRepository : ProfileRepository {

    private var profile = UserProfile(
        id = "user-01",
        fullName = "Trisha Malina",
        nim = "245150400111011",
        studyProgram = "Sistem Informasi '23",
        phone = "0812-9844-0000",
        avatarUrl = null,
        itemsReported = 14,
        claimsDone = 8,
        rating = 4.9,
        reviewCount = 24
    )

    override suspend fun getProfile(userId: String): UserProfile =
        if (profile.id == userId) profile else profile.copy(id = userId)

    override suspend fun updateProfile(profile: UserProfile) {
        this.profile = profile
    }

    // Upload foto sungguhan baru bisa diuji di Fase B (Supabase Storage)
    override suspend fun uploadAvatar(userId: String, imageBytes: ByteArray): String =
        profile.avatarUrl ?: ""
}
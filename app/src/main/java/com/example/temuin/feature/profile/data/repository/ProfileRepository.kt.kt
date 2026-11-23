package com.example.temuin.feature.profile.data.repository

import com.example.temuin.feature.profile.data.model.UserProfile

interface ProfileRepository {
    suspend fun getProfile(userId: String): UserProfile
    suspend fun updateProfile(profile: UserProfile)
    suspend fun uploadAvatar(userId: String, imageBytes: ByteArray): String
}
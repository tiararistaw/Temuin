package com.example.temuin.feature.favorite.data.repository

import com.example.temuin.feature.favorite.data.model.FavoriteReport

interface FavoriteRepository {
    suspend fun getFavorites(userId: String): List<FavoriteReport>
    suspend fun addFavorite(userId: String, reportId: String)
    suspend fun removeFavorite(userId: String, reportId: String)
}
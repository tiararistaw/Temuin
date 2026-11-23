
package com.example.temuin.feature.review.data.repository

import com.example.temuin.feature.review.data.model.Review

interface ReviewRepository {

    // Ulasan yang diterima pengguna
    fun getReviewsForUser(userId: String): List<Review>

    // Ulasan yang diberikan pengguna kepada orang lain
    fun getReviewsWrittenBy(userId: String): List<Review>

    fun getReviewById(reviewId: String): Review?

    fun addReview(review: Review)

    fun updateReview(review: Review)

    fun deleteReview(reviewId: String)
}

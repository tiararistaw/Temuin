
package com.example.temuin.feature.favorite.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.temuin.feature.review.data.model.Review
import com.example.temuin.feature.review.data.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReviewUiState(
    val reviews: List<Review> = emptyList(),
    val writtenReviews: List<Review> = emptyList()
)

class ReviewViewModel(
    private val repository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())

    val uiState: StateFlow<ReviewUiState> =
        _uiState.asStateFlow()

    // Memuat ulasan yang diterima
    fun loadReviews(userId: String) {
        _uiState.value = _uiState.value.copy(
            reviews = repository.getReviewsForUser(userId)
        )
    }

    // Memuat ulasan yang diberikan
    fun loadWrittenReviews(userId: String) {
        _uiState.value = _uiState.value.copy(
            writtenReviews = repository.getReviewsWrittenBy(userId)
        )
    }

    fun getReviewById(id: String): Review? {
        return repository.getReviewById(id)
    }

    fun addReview(
        review: Review,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addReview(review)

            loadReviews(review.reviewedUserId)
            loadWrittenReviews(review.reviewerId)

            onComplete()
        }
    }

    fun updateReview(
        review: Review,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            repository.updateReview(review)

            loadReviews(review.reviewedUserId)
            loadWrittenReviews(review.reviewerId)

            onComplete()
        }
    }

    fun deleteReview(review: Review) {
        viewModelScope.launch {
            repository.deleteReview(review.id)

            loadReviews(review.reviewedUserId)
            loadWrittenReviews(review.reviewerId)
        }
    }
}

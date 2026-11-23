package com.example.temuin.feature.claim.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.temuin.feature.claim.data.repository.ClaimRepository

class ClaimViewModelFactory(
    private val repository: ClaimRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(ClaimViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClaimViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
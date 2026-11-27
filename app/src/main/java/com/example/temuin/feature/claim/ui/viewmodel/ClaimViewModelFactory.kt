
package com.example.temuin.feature.claim.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.temuin.feature.claim.data.repository.ClaimRepository
import com.example.temuin.feature.claim.data.repository.SupabaseClaimRepository
import com.example.temuin.feature.claim.data.repository.SupabaseFoundItemRepository

class ClaimViewModelFactory(
    private val repository: ClaimRepository,
    private val supabaseRepository: SupabaseClaimRepository,
    private val foundItemRepository: SupabaseFoundItemRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClaimViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClaimViewModel(
                repository = repository,
                supabaseRepository = supabaseRepository,
                foundItemRepository = foundItemRepository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

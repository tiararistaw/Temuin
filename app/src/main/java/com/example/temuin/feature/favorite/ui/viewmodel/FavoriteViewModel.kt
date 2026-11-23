package com.example.temuin.feature.favorite.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.temuin.feature.favorite.data.model.FavoriteReport
import com.example.temuin.feature.favorite.data.model.ReportType
import com.example.temuin.feature.favorite.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FavoriteFilter(val label: String) {
    ALL("Semua"), FOUND("Ditemukan"), LOST("Hilang")
}

data class FavoriteUiState(
    val isLoading: Boolean = false,
    val items: List<FavoriteReport> = emptyList(),
    val filter: FavoriteFilter = FavoriteFilter.ALL,
    val error: String? = null
) {
    val filtered: List<FavoriteReport>
        get() = when (filter) {
            FavoriteFilter.ALL -> items
            FavoriteFilter.FOUND -> items.filter { it.type == ReportType.FOUND }
            FavoriteFilter.LOST -> items.filter { it.type == ReportType.LOST }
        }

    fun count(f: FavoriteFilter): Int = when (f) {
        FavoriteFilter.ALL -> items.size
        FavoriteFilter.FOUND -> items.count { it.type == ReportType.FOUND }
        FavoriteFilter.LOST -> items.count { it.type == ReportType.LOST }
    }
}

class FavoriteViewModel(
    private val repository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    fun load(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.getFavorites(userId) }
                .onSuccess { list -> _uiState.update { it.copy(isLoading = false, items = list) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun setFilter(filter: FavoriteFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun remove(userId: String, reportId: String) {
        viewModelScope.launch {
            runCatching { repository.removeFavorite(userId, reportId) }
                .onSuccess {
                    _uiState.update { s -> s.copy(items = s.items.filterNot { it.reportId == reportId }) }
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun add(userId: String, reportId: String) {
        viewModelScope.launch {
            runCatching { repository.addFavorite(userId, reportId) }
                .onSuccess { load(userId) }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun getById(reportId: String): FavoriteReport? =
        _uiState.value.items.find { it.reportId == reportId }
}

class FavoriteViewModelFactory(
    private val repository: FavoriteRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
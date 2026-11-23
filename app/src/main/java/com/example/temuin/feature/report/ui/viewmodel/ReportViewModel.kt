package com.example.temuin.feature.report.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.temuin.feature.report.data.model.Report
import com.example.temuin.feature.report.data.model.ReportDraft
import com.example.temuin.feature.report.data.model.ReportType
import com.example.temuin.feature.report.data.model.ReportStatus
import com.example.temuin.feature.report.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ReportFilter(val label: String) {
    ALL("Semua"), FOUND("Ditemukan"), LOST("Hilang"), MINE("Laporan Saya")
}

data class ReportUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val currentUserId: String = "",
    val reports: List<Report> = emptyList(),
    val filter: ReportFilter = ReportFilter.ALL,
    val query: String = "",
    val error: String? = null
) {
    val filtered: List<Report>
        get() = reports
            .filter {
                when (filter) {
                    ReportFilter.ALL -> true
                    ReportFilter.FOUND -> it.type == ReportType.FOUND
                    ReportFilter.LOST -> it.type == ReportType.LOST
                    ReportFilter.MINE -> it.ownerId == currentUserId
                }
            }
            .filter {
                query.isBlank() ||
                        it.itemName.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true) ||
                        it.location.contains(query, ignoreCase = true)
            }

    fun count(f: ReportFilter): Int = when (f) {
        ReportFilter.ALL -> reports.size
        ReportFilter.FOUND -> reports.count { it.type == ReportType.FOUND }
        ReportFilter.LOST -> reports.count { it.type == ReportType.LOST }
        ReportFilter.MINE -> reports.count { it.ownerId == currentUserId }
    }
}

class ReportViewModel(
    private val repository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    fun load(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, currentUserId = userId) }
            runCatching { repository.getReports() }
                .onSuccess { list -> _uiState.update { it.copy(isLoading = false, reports = list) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun setFilter(filter: ReportFilter) = _uiState.update { it.copy(filter = filter) }

    fun setQuery(query: String) = _uiState.update { it.copy(query = query) }

    fun getById(reportId: String): Report? =
        _uiState.value.reports.find { it.id == reportId }

    /** INSERT. [onSuccess] menerima id laporan baru untuk navigasi ke halaman status. */
    fun publish(
        draft: ReportDraft,
        ownerId: String,
        ownerName: String,
        photoBytes: List<ByteArray>,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            runCatching { repository.createReport(draft, ownerId, ownerName, photoBytes) }
                .onSuccess { created ->
                    _uiState.update {
                        it.copy(isSubmitting = false, reports = listOf(created) + it.reports)
                    }
                    onSuccess(created.id)
                }
                .onFailure { e -> _uiState.update { it.copy(isSubmitting = false, error = e.message) } }
        }
    }

    /** UPDATE laporan milik sendiri. */
    fun update(
        reportId: String,
        draft: ReportDraft,
        photoBytes: List<ByteArray>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            runCatching { repository.updateReport(reportId, draft, photoBytes) }
                .onSuccess { updated ->
                    _uiState.update { s ->
                        s.copy(
                            isSubmitting = false,
                            reports = s.reports.map { if (it.id == reportId) updated else it }
                        )
                    }
                    onSuccess()
                }
                .onFailure { e -> _uiState.update { it.copy(isSubmitting = false, error = e.message) } }
        }
    }

    fun updateStatus(reportId: String, status: ReportStatus) {
        viewModelScope.launch {
            runCatching { repository.updateStatus(reportId, status) }
                .onSuccess { updated ->
                    _uiState.update { s ->
                        s.copy(reports = s.reports.map { if (it.id == reportId) updated else it })
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun markCompleted(reportId: String) = updateStatus(reportId, ReportStatus.COMPLETED)

    fun deleteReport(reportId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            runCatching { repository.deleteReport(reportId) }
                .onSuccess {
                    _uiState.update { s ->
                        s.copy(
                            isSubmitting = false,
                            reports = s.reports.filter { it.id != reportId }
                        )
                    }
                    onSuccess()
                }
                .onFailure { e -> _uiState.update { it.copy(isSubmitting = false, error = e.message) } }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}

class ReportViewModelFactory(
    private val repository: ReportRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            return ReportViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
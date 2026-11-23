package com.example.temuin.feature.profile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.temuin.feature.profile.data.model.UserProfile
import com.example.temuin.feature.profile.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val error: String? = null,
    val saved: Boolean = false
)

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun load(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.getProfile(userId) }
                .onSuccess { p -> _uiState.update { it.copy(isLoading = false, profile = p) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun save(name: String, nim: String, study: String, phone: String) {
        val current = _uiState.value.profile ?: return
        val updated = current.copy(fullName = name.trim(), nim = nim.trim(), studyProgram = study.trim(), phone = phone.trim())
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.updateProfile(updated) }
                .onSuccess { _uiState.update { it.copy(isLoading = false, profile = updated, saved = true) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun uploadAvatar(userId: String, bytes: ByteArray) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.uploadAvatar(userId, bytes) }
                .onSuccess { load(userId) }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun consumeSaved() {
        _uiState.update { it.copy(saved = false) }
    }
}

class ProfileViewModelFactory(
    private val repository: ProfileRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
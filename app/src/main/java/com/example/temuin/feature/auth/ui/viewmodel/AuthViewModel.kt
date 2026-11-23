package com.example.temuin.feature.auth.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.temuin.feature.auth.data.model.User
import com.example.temuin.feature.auth.data.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(val repository: AuthRepository) : ViewModel() {
    val currentUser: StateFlow<User?> = repository.currentUser

    fun login(email: String, password: String): Result<User> {
        return repository.login(email, password)
    }

    fun register(
        name: String,
        email: String,
        password: String,
        nim: String = "",
        phone: String = ""
    ): Result<User> {
        return repository.register(name, email, password, nim, phone)
    }

    fun logout() {
        repository.logout()
    }
}

package com.example.temuin.feature.auth.data.repository

import com.example.temuin.feature.auth.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository {
    private val users = mutableListOf(
        User(
            id = "user-01",
            name = "Trisha Malina",
            email = "trisha.malina@student.ub.ac.id",
            password = "trisha123",
            nim = "245150400111011",
            studyProgram = "Teknik Informatika '22",
            phone = "08111222333"
        ),
        User(
            id = "user-02",
            name = "Ailsa Nasywa",
            email = "ailsa.nasywa@student.ub.ac.id",
            password = "ailsa123",
            nim = "245150400111022",
            studyProgram = "Sistem Informasi '23",
            phone = "08222333444"
        ),
        User(
            id = "user-03",
            name = "Dennisa Syafa",
            email = "dennisa.syafa@student.ub.ac.id",
            password = "dennisa123",
            nim = "245150400111033",
            studyProgram = "Ilmu Komputer '23",
            phone = "08333444555"
        ),
        User(
            id = "user-04",
            name = "Tiara Aristawidya",
            email = "tiara.arista@student.ub.ac.id",
            password = "tiara123",
            nim = "245150400111044",
            studyProgram = "Teknologi Informasi '22",
            phone = "08444555666"
        ),
        User(
            id = "user-05",
            name = "Sandra Triana",
            email = "sandra.triana@student.ub.ac.id",
            password = "sandra123",
            nim = "245150400111055",
            studyProgram = "Pendidikan Teknologi Informasi '24",
            phone = "08555666777"
        )
    )

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun login(email: String, password: String): Result<User> {
        val trimmedEmail = email.trim()
        val user = users.firstOrNull { it.email.equals(trimmedEmail, ignoreCase = true) && it.password == password }
        return if (user != null) {
            _currentUser.value = user
            Result.success(user)
        } else {
            Result.failure(Exception("Email atau password salah."))
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        nim: String = "",
        phone: String = ""
    ): Result<User> {
        val trimmedEmail = email.trim()
        if (users.any { it.email.equals(trimmedEmail, ignoreCase = true) }) {
            return Result.failure(Exception("Email sudah terdaftar."))
        }
        val newUser = User(
            id = "user-${System.currentTimeMillis()}",
            name = name.trim(),
            email = trimmedEmail,
            password = password,
            nim = nim.trim().ifBlank { "245150400111000" },
            phone = phone.trim().ifBlank { "081234567890" }
        )
        users.add(newUser)
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    fun logout() {
        _currentUser.value = null
    }

    fun getUserById(userId: String): User? {
        return users.firstOrNull { it.id == userId }
    }
}

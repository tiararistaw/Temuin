
package com.example.temuin.feature.auth.data.repository

import com.example.temuin.SupabaseClient
import com.example.temuin.feature.auth.data.model.User
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository {

    private val supabase = SupabaseClient.client

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    suspend fun register(
        name: String,
        email: String,
        password: String,
        nim: String,
        phone: String
    ): Result<Unit> {
        return runCatching {
            val trimmedEmail = email.trim()

            require(trimmedEmail.endsWith("@student.ub.ac.id", ignoreCase = true)) {
                "Gunakan email mahasiswa UB."
            }

            require(name.isNotBlank()) {
                "Nama lengkap wajib diisi."
            }
            require(nim.isNotBlank()) {
                "NIM wajib diisi."
            }
            require(phone.isNotBlank()) {
                "Nomor HP wajib diisi."
            }

            supabase.auth.signUpWith(
                Email,
                redirectUrl = "temuin://auth"
            ) {
                this.email = trimmedEmail
                this.password = password

                data = buildJsonObject {
                    put("full_name", name.trim())
                    put("nim", nim.trim())
                    put("phone", phone.trim())
                }
            }

            Unit
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<User> {
        return runCatching {
            val trimmedEmail = email.trim()

            supabase.auth.signInWith(Email) {
                this.email = trimmedEmail
                this.password = password
            }

            val authUser = supabase.auth.currentUserOrNull()
                ?: error("Sesi login tidak ditemukan.")

            val profile = supabase
                .from("profiles")
                .select {
                    filter {
                        eq("id", authUser.id)
                    }
                }
                .decodeSingle<User>()

            val user = profile.copy(
                email = authUser.email ?: trimmedEmail
            )

            _currentUser.value = user
            user
        }
    }

    suspend fun logout() {
        supabase.auth.signOut()
        _currentUser.value = null
    }

    fun getUserById(userId: String): User? {
        return _currentUser.value?.takeIf { it.id == userId }
    }
}

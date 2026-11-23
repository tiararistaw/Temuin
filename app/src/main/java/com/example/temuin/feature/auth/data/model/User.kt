
package com.example.temuin.feature.auth.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    @SerialName("full_name")
    val name: String,
    val email: String = "",
    val nim: String,
    val phone: String,
    @SerialName("created_at")
    val createdAt: String? = null
)

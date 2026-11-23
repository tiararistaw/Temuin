
package com.example.temuin.feature.claim.data.model

data class FoundItem(
    val id: String,
    val name: String,
    val category: String,
    val location: String,
    val dateFound: String,
    val description: String = "",
    val finderName: String = "",
    val status: String = "Tersedia",
    val ownerId: String = "user-01"
)

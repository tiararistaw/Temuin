package com.example.temuin.feature.claim.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.temuin.feature.claim.data.model.Claim
import com.example.temuin.feature.claim.data.repository.ClaimRepository

class ClaimViewModel(
    private val repository: ClaimRepository
) : ViewModel() {

    var foundItems = mutableStateOf(repository.getFoundItems())
        private set

    var claims = mutableStateOf<List<Claim>>(emptyList())
        private set

    var selectedClaim = mutableStateOf<Claim?>(null)
        private set

    var incomingClaims = mutableStateOf<List<Claim>>(emptyList())
        private set

    fun loadClaims(userId: String) {
        claims.value = repository.getClaimsByUser(userId)
    }

    fun getFoundItemById(itemId: String) =
        repository.getFoundItemById(itemId)

    fun submitClaim(claim: Claim) {
        repository.submitClaim(claim)
        loadClaims(claim.userId)
    }

    fun getClaimById(claimId: String) {
        selectedClaim.value = repository.getClaimById(claimId)
    }

    private var loadedOwnerId: String? = null

    fun loadIncomingClaims(reportOwnerId: String) {
        loadedOwnerId = reportOwnerId
        incomingClaims.value = repository.getIncomingClaims(reportOwnerId)
    }

    fun respondToClaim(claimId: String, approved: Boolean, rejectionNote: String? = null) {
        repository.updateClaimStatus(
            claimId, if (approved) "Diproses" else "Ditolak",
            if (approved) null else rejectionNote
        )
        selectedClaim.value = repository.getClaimById(claimId)
        loadedOwnerId?.let { incomingClaims.value = repository.getIncomingClaims(it) }
    }

    fun confirmHandover(claimId: String) {
        repository.confirmHandover(claimId)
        selectedClaim.value = repository.getClaimById(claimId)
        loadedOwnerId?.let { incomingClaims.value = repository.getIncomingClaims(it) }
    }
}
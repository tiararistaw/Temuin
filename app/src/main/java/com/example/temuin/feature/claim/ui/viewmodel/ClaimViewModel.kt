
package com.example.temuin.feature.claim.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.temuin.feature.claim.data.model.Claim
import com.example.temuin.feature.claim.data.model.FoundItem
import com.example.temuin.feature.claim.data.repository.ClaimRepository
import com.example.temuin.feature.claim.data.repository.SupabaseClaimRepository
import com.example.temuin.feature.claim.data.repository.SupabaseFoundItemRepository
import kotlinx.coroutines.launch

class ClaimViewModel(
    private val repository: ClaimRepository,
    private val supabaseRepository: SupabaseClaimRepository,
    private val foundItemRepository: SupabaseFoundItemRepository
) : ViewModel() {

    var foundItems = mutableStateOf<List<FoundItem>>(emptyList())
        private set

    var claims = mutableStateOf<List<Claim>>(emptyList())
        private set

    var selectedClaim = mutableStateOf<Claim?>(null)
        private set

    var incomingClaims = mutableStateOf<List<Claim>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun loadFoundItems() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                foundItems.value = foundItemRepository.getFoundItems()
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    fun getFoundItemById(itemId: String): FoundItem? {
        return foundItems.value.find { it.id == itemId }
    }

    fun loadClaims(userId: String = "") {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                claims.value = supabaseRepository.getMyClaims().map { it.toClaim() }
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    fun submitClaim(claim: Claim) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                supabaseRepository.submitClaim(
                    reportId = claim.itemId,
                    claimDescription = claim.claimDescription,
                    whatsapp = claim.whatsapp,
                    pickupLocation = claim.pickupLocation,
                    evidenceUrl = claim.evidenceUri,
                    claimantName = claim.claimantName
                )
                loadClaims()
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    fun getClaimById(claimId: String) {
        viewModelScope.launch {
            try {
                selectedClaim.value =
                    supabaseRepository.getClaimById(claimId)?.toClaim()
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }

    fun loadIncomingClaims(reportOwnerId: String = "") {
        viewModelScope.launch {
            isLoading.value = true

            try {
                incomingClaims.value =
                    supabaseRepository.getIncomingClaims().map { it.toClaim() }
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    fun respondToClaim(
        claimId: String,
        approved: Boolean,
        rejectionNote: String? = null
    ) {
        viewModelScope.launch {
            try {
                selectedClaim.value = supabaseRepository.updateClaimStatus(
                    claimId = claimId,
                    status = if (approved) "Diproses" else "Ditolak",
                    rejectionNote = if (approved) null else rejectionNote
                ).toClaim()

                loadIncomingClaims()
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }

    // Sementara masih menggunakan repository dummy
    fun confirmHandover(claimId: String) {
        repository.confirmHandover(claimId)
        selectedClaim.value = repository.getClaimById(claimId)
    }
}

private fun com.example.temuin.feature.claim.data.model.ClaimRow.toClaim(): Claim {
    return Claim(
        id = id,
        userId = claimantId,
        itemId = reportId,
        itemName = itemName,
        claimDescription = claimDescription,
        whatsapp = whatsapp.orEmpty(),
        pickupLocation = pickupLocation.orEmpty(),
        evidenceUri = evidenceUrl,
        claimDate = createdAt,
        status = status,
        reportOwnerId = reportOwnerId.orEmpty(),
        reportOwnerName = reportOwnerName.orEmpty(),
        claimantName = claimantName.orEmpty(),
        rejectionNote = rejectionNote,
        handoverConfirmed = handoverConfirmed
    )
}

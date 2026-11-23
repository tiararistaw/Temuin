package com.example.temuin.feature.claim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.temuin.feature.claim.ui.viewmodel.ClaimViewModel
import com.example.temuin.feature.review.ui.components.*

@Composable
fun IncomingClaimsScreen(viewModel: ClaimViewModel, ownerId: String, onBack: () -> Unit) {
    val claims by viewModel.incomingClaims
    var rejectTarget by remember { mutableStateOf<String?>(null) }
    var rejection by remember { mutableStateOf("") }
    LaunchedEffect(ownerId) { viewModel.loadIncomingClaims(ownerId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TemuinBackground)
    ) {
        TemuinTopBar(title = "Pengajuan Masuk", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Tanggapi klaim yang diajukan pengguna lain pada laporan temuanmu.", color = TemuinMuted)

            if (claims.isEmpty()) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("Belum ada pengajuan klaim untuk laporanmu.", color = TemuinMuted)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(claims, key = { it.id }) { claim ->
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
                            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(claim.itemName, fontWeight = FontWeight.SemiBold, color = TemuinDark)
                                Text("${claim.claimDate} • ${claim.status}", color = TemuinMuted, style = MaterialTheme.typography.bodySmall)
                                Text(claim.claimDescription, color = TemuinDark)
                                if (claim.rejectionNote != null) Text("Alasan penolakan: ${claim.rejectionNote}", color = Color(0xFFB54747))
                                if (claim.status == "Menunggu") Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { viewModel.respondToClaim(claim.id, true) }, colors = ButtonDefaults.buttonColors(containerColor = TemuinTeal)) { Text("Setujui") }
                                    OutlinedButton(onClick = { rejectTarget = claim.id }) { Text("Tolak") }
                                }
                                if (claim.status == "Diproses") Button(onClick = { viewModel.confirmHandover(claim.id) }, colors = ButtonDefaults.buttonColors(containerColor = TemuinTeal)) { Text("Konfirmasi Serah Terima") }
                                if (claim.handoverConfirmed) Text("Serah terima selesai", color = TemuinTeal, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
    if (rejectTarget != null) AlertDialog(
        onDismissRequest = { rejectTarget = null },
        title = { Text("Tolak Pengajuan") },
        text = { OutlinedTextField(rejection, { rejection = it }, label = { Text("Alasan penolakan") }) },
        confirmButton = { TextButton(onClick = { viewModel.respondToClaim(rejectTarget!!, false, rejection); rejectTarget = null; rejection = "" }) { Text("Kirim") } },
        dismissButton = { TextButton(onClick = { rejectTarget = null }) { Text("Batal") } }
    )
}

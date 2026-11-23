package com.example.temuin.feature.claim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temuin.feature.review.ui.components.*

@Composable
fun ClaimFormScreen(
    itemName: String = "Kunci Motor Honda Vario & Gantungan Dino",
    itemLocation: String = "Gazebo Gedung F FILKOM",
    onSubmit: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var characteristics by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var agreement by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TemuinBackground)
    ) {
        TemuinTopBar(title = "Formulir Klaim Kepemilikan", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Item summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Barang yang diklaim:", fontSize = 12.sp, color = TemuinMuted)
                    Text(itemName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TemuinDark)
                    Text("Lokasi: $itemLocation", fontSize = 13.sp, color = TemuinMuted)
                }
            }

            // Input form card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Bukti & Keterangan Kepemilikan", fontWeight = FontWeight.SemiBold, color = TemuinDark)

                    OutlinedTextField(
                        value = characteristics,
                        onValueChange = { characteristics = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("Sebutkan ciri khusus (misal: ada gantungan kunci warna biru, goresan di layar...)") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TemuinTeal,
                            unfocusedBorderColor = TemuinBorder
                        )
                    )

                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nomor WhatsApp yang bisa dihubungi") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TemuinTeal,
                            unfocusedBorderColor = TemuinBorder
                        )
                    )

                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(checked = agreement, onCheckedChange = { agreement = it })
                        Text("Saya menyatakan data yang diberikan adalah benar dan dapat dipertanggungjawabkan.", fontSize = 12.sp, color = TemuinMuted)
                    }

                    Button(
                        onClick = { onSubmit(characteristics) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = agreement && characteristics.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = TemuinTeal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Ajukan Klaim Sekarang", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

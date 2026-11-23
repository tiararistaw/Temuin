
package com.example.temuin.feature.claim.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ClaimTeal = Color(0xFF006F68)
private val ClaimLavender = Color(0xFFE9EAFE)
private val ClaimPaleBlue = Color(0xFFF0F2FF)
private val ClaimMuted = Color(0xFF66716F)
private val ClaimBorder = Color(0xFFEDECF4)

@Composable
fun FoundItemCard(
    itemName: String,
    category: String,
    location: String,
    date: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Placeholder foto barang
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ClaimPaleBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Foto\nBarang",
                    color = ClaimMuted,
                    fontSize = 12.sp
                )
            }

            // Informasi barang
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = ClaimLavender
                ) {
                    Text(
                        text = category,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        ),
                        color = ClaimTeal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = itemName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Lokasi: $location",
                    fontSize = 12.sp,
                    color = ClaimMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = date,
                    fontSize = 11.sp,
                    color = ClaimMuted
                )

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color(0xFFE0F2F1)
                ) {
                    Text(
                        text = "Tersedia untuk diklaim",
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                        color = ClaimTeal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

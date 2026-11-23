package com.example.temuin.feature.favorite.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.temuin.feature.favorite.data.model.FavoriteReport
import com.example.temuin.feature.favorite.data.model.ReportType
import com.example.temuin.feature.review.ui.components.TemuinMuted
import com.example.temuin.feature.review.ui.components.TemuinPaleBlue
import com.example.temuin.feature.review.ui.components.TemuinTeal

val TemuinLostRed = Color(0xFFB84B4B)

@Composable
fun FavoriteCard(
    item: FavoriteReport,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    onAction: () -> Unit = {}
) {
    val badgeColor = if (item.type == ReportType.FOUND) TemuinTeal else TemuinLostRed

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(TemuinPaleBlue)
            ) {
                if (item.photoUrl != null) {
                    AsyncImage(
                        model = item.photoUrl,
                        contentDescription = item.itemName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Image, null,
                        tint = TemuinMuted,
                        modifier = Modifier.align(Alignment.Center).size(36.dp)
                    )
                }

                Surface(
                    color = badgeColor,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                ) {
                    Text(
                        item.type.label,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.align(Alignment.TopEnd).size(40.dp)
                ) {
                    Icon(Icons.Default.Favorite, "Hapus dari favorit", tint = TemuinLostRed)
                }
            }

            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    item.itemName,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, null, tint = TemuinTeal, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(
                        item.location,
                        color = TemuinMuted,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(item.timeLabel, color = TemuinMuted, style = MaterialTheme.typography.labelSmall)

                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = onAction,
                    modifier = Modifier.fillMaxWidth().height(34.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = TemuinTeal)
                ) {
                    Text(
                        if (item.type == ReportType.FOUND) "Klaim" else "Bantu Cari",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}
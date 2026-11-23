package com.example.temuin.feature.favorite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.temuin.feature.favorite.ui.components.FavoriteCard
import com.example.temuin.feature.favorite.ui.viewmodel.FavoriteFilter
import com.example.temuin.feature.favorite.ui.viewmodel.FavoriteViewModel
import com.example.temuin.feature.review.ui.components.*

@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel,
    userId: String,
    onBack: () -> Unit,
    onItemClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) { viewModel.load(userId) }

    Column(Modifier.fillMaxSize().background(TemuinBackground)) {
        ReviewTopBar("Favorit", onBack)

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Judul
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Laporan Favorit Saya",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(color = TemuinLavender, shape = RoundedCornerShape(50)) {
                            Text(
                                "${state.items.size} Barang Disimpan",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Text(
                        "Pantau perkembangan status barang yang Anda tandai di sekitar kampus.",
                        color = TemuinMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Filter (LazyRow)
            item(span = { GridItemSpan(maxLineSpan) }) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(FavoriteFilter.entries.toList()) { f ->
                        TemuinFilterChip(
                            label = "${f.label} (${state.count(f)})",
                            selected = state.filter == f,
                            onClick = { viewModel.setFilter(f) }
                        )
                    }
                }
            }

            when {
                state.isLoading -> item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(Modifier.fillMaxWidth().padding(40.dp), Alignment.Center) {
                        CircularProgressIndicator(color = TemuinTeal)
                    }
                }

                state.error != null -> item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Gagal memuat: ${state.error}", color = TemuinMuted)
                        TextButton(onClick = { viewModel.load(userId) }) { Text("Coba lagi") }
                    }
                }

                state.filtered.isEmpty() -> item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        "Belum ada laporan favorit.",
                        color = TemuinMuted,
                        modifier = Modifier.fillMaxWidth().padding(32.dp)
                    )
                }

                else -> items(state.filtered, key = { it.reportId }) { item ->
                    FavoriteCard(
                        item = item,
                        onClick = { onItemClick(item.reportId) },
                        onRemove = { viewModel.remove(userId, item.reportId) }
                    )
                }
            }

            // Kartu tips
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Tips Kelola Wishlist Kampus", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Ketuk ikon hati pada kartu laporan untuk menghapus dari favorit. " +
                                    "Barang yang sudah dikembalikan akan hilang dari daftar secara otomatis.",
                            color = TemuinMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
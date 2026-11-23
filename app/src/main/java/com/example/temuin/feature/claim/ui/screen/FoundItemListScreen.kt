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
import androidx.compose.ui.unit.sp
import com.example.temuin.feature.claim.ui.components.FoundItemCard
import com.example.temuin.feature.claim.ui.viewmodel.ClaimViewModel
import com.example.temuin.feature.review.ui.components.*

@Composable
fun FoundItemListScreen(
    viewModel: ClaimViewModel,
    onItemClick: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val foundItems = viewModel.foundItems.value

    val filteredItems = foundItems.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true) ||
                it.location.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TemuinBackground)
    ) {
        TemuinTopBar(title = "Barang Temuan", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Temukan barang yang mungkin menjadi milikmu.",
                fontSize = 13.sp,
                color = TemuinMuted
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari nama atau lokasi barang...", fontSize = 13.sp) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TemuinTeal,
                    unfocusedBorderColor = TemuinBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Text(
                text = "${filteredItems.size} Barang Ditemukan",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TemuinDark
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(
                    items = filteredItems,
                    key = { it.id }
                ) { item ->
                    FoundItemCard(
                        itemName = item.name,
                        category = item.category,
                        location = item.location,
                        date = item.dateFound,
                        onClick = { onItemClick(item.id) }
                    )
                }

                if (filteredItems.isEmpty()) {
                    item {
                        Text(
                            text = "Barang tidak ditemukan.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            color = TemuinMuted
                        )
                    }
                }
            }
        }
    }
}

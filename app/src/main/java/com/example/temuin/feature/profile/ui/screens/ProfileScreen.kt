package com.example.temuin.feature.profile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.temuin.feature.favorite.ui.viewmodel.FavoriteViewModel
import com.example.temuin.feature.profile.data.model.UserProfile
import com.example.temuin.feature.profile.ui.viewmodel.ProfileViewModel
import com.example.temuin.feature.review.ui.components.*

private enum class ProfileTab(val label: String, val icon: ImageVector) {
    CLAIMS("Riwayat Klaim", Icons.Outlined.Description),
    FAVORITES("Favorit", Icons.Outlined.BookmarkBorder),
    REVIEWS("Ulasan", Icons.Outlined.RateReview)
}

private data class ClaimPreview(
    val id: String,
    val item: String,
    val location: String,
    val note: String,
    val status: String,
    val statusBg: Color,
    val statusFg: Color,
    val footer: String
)

private val dummyClaims = listOf(
    ClaimPreview(
        "#CLM-8921", "MacBook Air M2 Space Grey", "Gedung F Lantai 3 - Ruang F3.1",
        "Verifikasi kepemilikan oleh Satpam GKB", "Diproses",
        Color(0xFFDDE8FF), Color(0xFF1A56C4), "Update: Hari ini, 10:15 WIB"
    ),
    ClaimPreview(
        "#CLM-8910", "Tumbler Corkcicle Turquoise", "Kantin Baru FILKOM",
        "Menunggu validasi penemu barang", "Menunggu",
        Color(0xFFFFE9C7), Color(0xFF9A5B00), "Diajukan: Kemarin, 16:40 WIB"
    ),
    ClaimPreview(
        "#CLM-8702", "Dompet Kartu Kulit Cokelat", "Pos Satpam Gedung H FILKOM",
        "Sudah serah terima dengan Satpam", "Selesai Diterima",
        Color(0xFFA7F1E4), Color(0xFF00544F), "Selesai"
    )
)

@Composable
fun ProfileAvatar(profile: UserProfile, size: Int) {
    if (profile.avatarUrl.isNullOrBlank()) {
        InitialAvatar(profile.fullName, size)
    } else {
        AsyncImage(
            model = profile.avatarUrl,
            contentDescription = "Foto profil",
            modifier = Modifier.size(size.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    favoriteViewModel: FavoriteViewModel,
    userId: String,
    onEdit: () -> Unit,
    onOpenClaims: () -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenReviews: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val favState by favoriteViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(ProfileTab.CLAIMS) }

    LaunchedEffect(userId) {
        viewModel.load(userId)
        favoriteViewModel.load(userId)
    }

    Column(Modifier.fillMaxSize().background(TemuinBackground)) {
        TemuinHeader()

        val profile = state.profile
        when {
            profile == null && state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = TemuinTeal)
            }

            profile == null -> Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Gagal memuat profil: ${state.error ?: "-"}", color = TemuinMuted)
                TextButton(onClick = { viewModel.load(userId) }) { Text("Coba lagi") }
            }

            else -> Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // KARTU PROFIL
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ProfileAvatar(profile, 76)
                            Column(
                                Modifier.weight(1f).padding(start = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    profile.fullName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(color = Color(0xFFD9F5EE), shape = RoundedCornerShape(50)) {
                                    Row(
                                        Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Filled.CheckCircle, null, tint = TemuinTeal, modifier = Modifier.size(13.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "Terverifikasi", color = TemuinTeal,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                Text("NIM ${profile.nim}", color = TemuinMuted, style = MaterialTheme.typography.bodySmall)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.School, null, tint = TemuinTeal, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        profile.studyProgram, color = TemuinTeal,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            IconButton(
                                onClick = onEdit,
                                modifier = Modifier.size(40.dp).background(TemuinPaleBlue, CircleShape)
                            ) {
                                Icon(Icons.Outlined.Edit, "Edit profil", modifier = Modifier.size(18.dp))
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatBox(profile.itemsReported.toString(), "Laporan Saya", TemuinTeal, false, Modifier.weight(1f))
                            StatBox(profile.claimsOngoing.toString(), "Klaim Berjalan", Color(0xFF9A5B00), false, Modifier.weight(1f))
                            StatBox("${profile.rating}", "${profile.reviewCount} Ulasan", Color(0xFF17202A), true, Modifier.weight(1f))
                        }
                    }
                }

                // TAB
                Surface(color = TemuinPaleBlue, shape = RoundedCornerShape(16.dp)) {
                    Row(Modifier.fillMaxWidth().padding(4.dp)) {
                        ProfileTab.entries.forEach { tab ->
                            val selected = selectedTab == tab
                            Row(
                                Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) TemuinTeal else Color.Transparent)
                                    .clickable {
                                        if (tab == ProfileTab.REVIEWS) onOpenReviews()
                                        else selectedTab = tab
                                    }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    tab.icon, null,
                                    tint = if (selected) Color.White else TemuinMuted,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    tab.label,
                                    color = if (selected) Color.White else TemuinMuted,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // ISI TAB
                when (selectedTab) {
                    ProfileTab.CLAIMS -> dummyClaims.forEach { ClaimCard(it, onOpenClaims) }

                    ProfileTab.FAVORITES -> {
                        if (favState.items.isEmpty()) {
                            Text(
                                "Belum ada laporan favorit.", color = TemuinMuted,
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            favState.items.take(3).forEach { fav ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Row(
                                        Modifier.fillMaxWidth().padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(Modifier.weight(1f)) {
                                            Text(fav.itemName, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text(fav.location, color = TemuinMuted, style = MaterialTheme.typography.bodySmall)
                                        }
                                        Text(fav.type.label, color = TemuinTeal, style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }
                        }
                        Button(
                            onClick = onOpenFavorites,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = TemuinTeal)
                        ) { Text("Lihat Semua Favorit") }
                    }

                    ProfileTab.REVIEWS -> {}
                }

                // PUSAT LAYANAN
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(44.dp).background(Color(0xFFDDEFEA), CircleShape),
                                contentAlignment = Alignment.Center
                            ) { Icon(Icons.Outlined.Shield, null, tint = TemuinTeal) }
                            Column(Modifier.padding(start = 12.dp)) {
                                Text("Pusat Layanan Satpam & Kampus", fontWeight = FontWeight.SemiBold)
                                Text(
                                    "Butuh bantuan pengamanan fisik barang berharga?",
                                    color = TemuinMuted, style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ServiceButton("Posko Gedung F", Icons.Outlined.Call, Modifier.weight(1f))
                            ServiceButton("Chat Admin FILKOM", Icons.Outlined.SupportAgent, Modifier.weight(1f))
                        }
                    }
                }

                // PENGATURAN AKUN
                Text(
                    "PENGATURAN AKUN",
                    color = TemuinMuted,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        SettingRow("Data Diri & Verifikasi KTM", Icons.Outlined.Person, onEdit)
                        HorizontalDivider(color = Color(0xFFEFEFEF), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingRow("Preferensi Notifikasi Laporan", Icons.Outlined.Notifications) {}
                        HorizontalDivider(color = Color(0xFFEFEFEF), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingRow("Keamanan & Sandi Akun UB", Icons.Outlined.Lock) {}
                        HorizontalDivider(color = Color(0xFFEFEFEF), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingRow("Panduan & FAQ Temuin", Icons.Outlined.Info) {}
                    }
                }

                // KELUAR
                Surface(
                    onClick = onLogout,
                    color = Color(0xFFFBE4E4),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.Logout, null, tint = Color(0xFFC62828))
                        Spacer(Modifier.width(8.dp))
                        Text("Keluar dari Akun", color = Color(0xFFC62828), fontWeight = FontWeight.SemiBold)
                    }
                }

                Column(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Temuin v2.4.0 • Kolaborasi DTI & BEM FILKOM UB", color = TemuinMuted, style = MaterialTheme.typography.labelSmall)
                    Text("Kebaikan kecil, temukan kembali senyummu.", color = TemuinMuted, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun TemuinHeader() {
    Row(
        Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Temuin", color = TemuinTeal, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(8.dp))
        Surface(color = TemuinLavender, shape = RoundedCornerShape(50)) {
            Text(
                "FILKOM UB", color = Color(0xFF344054),
                style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
        Spacer(Modifier.weight(1f))
        Icon(Icons.Outlined.Notifications, "Notifikasi", tint = Color(0xFF17202A))
        Spacer(Modifier.width(14.dp))
        Box(Modifier.size(36.dp).background(TemuinTeal, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Person, null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun StatBox(value: String, label: String, valueColor: Color, withStar: Boolean, modifier: Modifier) {
    Column(
        modifier.background(TemuinPaleBlue, RoundedCornerShape(14.dp)).padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (withStar) Icon(Icons.Filled.Star, null, tint = TemuinOrange, modifier = Modifier.size(15.dp))
            Text(value, color = valueColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        }
        Text(label, color = TemuinMuted, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun ClaimCard(claim: ClaimPreview, onDetail: () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "ID Klaim: ${claim.id}", color = TemuinMuted,
                    style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Surface(color = claim.statusBg, shape = RoundedCornerShape(50)) {
                    Text(
                        claim.status, color = claim.statusFg,
                        style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Row {
                Box(Modifier.size(64.dp).background(TemuinPaleBlue, RoundedCornerShape(12.dp)))
                Column(Modifier.padding(start = 12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(claim.item, fontWeight = FontWeight.SemiBold)
                    Text(claim.location, color = TemuinMuted, style = MaterialTheme.typography.bodySmall)
                    Text(claim.note, color = TemuinTeal, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
            }
            Surface(color = TemuinPaleBlue, shape = RoundedCornerShape(10.dp), onClick = onDetail) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(claim.footer, color = Color(0xFF344054), style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                    Text("Detail Status →", color = TemuinTeal, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ServiceButton(label: String, icon: ImageVector, modifier: Modifier) {
    Surface(color = TemuinPaleBlue, shape = RoundedCornerShape(12.dp), modifier = modifier) {
        Row(
            Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = TemuinTeal, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}

@Composable
private fun SettingRow(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color(0xFF536078), modifier = Modifier.size(22.dp))
        Text(label, Modifier.weight(1f).padding(start = 14.dp), fontWeight = FontWeight.Medium)
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TemuinMuted)
    }
}
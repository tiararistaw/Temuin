package com.example.temuin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.jan.supabase.auth.handleDeeplinks
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.background

// AUTH
import com.example.temuin.feature.auth.data.repository.AuthRepository
import com.example.temuin.feature.auth.ui.screens.LoginScreen
import com.example.temuin.feature.auth.ui.screens.RegisterScreen
import com.example.temuin.feature.auth.ui.screens.SplashScreen
import com.example.temuin.feature.auth.ui.viewmodel.AuthViewModel

// HISTORY & SEARCH
import com.example.temuin.feature.history.ui.screens.HistoryScreen
import com.example.temuin.feature.history.ui.screens.SearchScreen

// CLAIM
import com.example.temuin.feature.claim.data.model.Claim
import com.example.temuin.feature.claim.data.repository.ClaimRepository
import com.example.temuin.feature.claim.ui.screens.ClaimDetailScreen
import com.example.temuin.feature.claim.ui.screens.ClaimFormScreen
import com.example.temuin.feature.claim.ui.screens.FoundItemDetailScreen
import com.example.temuin.feature.claim.ui.screens.FoundItemListScreen
import com.example.temuin.feature.claim.ui.screens.IncomingClaimsScreen
import com.example.temuin.feature.claim.ui.viewmodel.ClaimViewModel
import com.example.temuin.feature.claim.ui.viewmodel.ClaimViewModelFactory

// FAVORITE
import com.example.temuin.feature.favorite.data.repository.DummyFavoriteRepository
import com.example.temuin.feature.favorite.ui.viewmodel.FavoriteViewModel
import com.example.temuin.feature.favorite.ui.viewmodel.FavoriteViewModelFactory

// HOME
import com.example.temuin.feature.home.ui.screens.HomeScreen

// PROFILE
import com.example.temuin.feature.profile.data.repository.DummyProfileRepository
import com.example.temuin.feature.profile.ui.viewmodel.ProfileViewModel
import com.example.temuin.feature.profile.ui.viewmodel.ProfileViewModelFactory

// REPORT
import com.example.temuin.feature.report.data.model.Report
import com.example.temuin.feature.report.data.repository.SupabaseReportRepository
import com.example.temuin.feature.report.ui.screens.ReportProgressScreen
import com.example.temuin.feature.report.ui.viewmodel.ReportViewModel
import com.example.temuin.feature.report.ui.viewmodel.ReportViewModelFactory

// REVIEW
import com.example.temuin.feature.review.data.model.ReviewContext
import com.example.temuin.feature.review.data.model.ReviewRole
import com.example.temuin.feature.review.data.repository.DummyReviewRepository
import com.example.temuin.feature.review.ui.components.TemuinDark
import com.example.temuin.feature.review.ui.components.TemuinMuted
import com.example.temuin.feature.review.ui.components.TemuinPaleBlue
import com.example.temuin.feature.review.ui.components.TemuinTeal
import com.example.temuin.feature.review.ui.screens.MyReceivedReviewsScreen
import com.example.temuin.feature.review.ui.screens.MyWrittenReviewsScreen
import com.example.temuin.feature.review.ui.screens.ReviewFormScreen
import com.example.temuin.feature.review.ui.screens.UserReviewsScreen
import com.example.temuin.feature.review.ui.viewmodel.ReviewViewModel
import com.example.temuin.feature.review.ui.viewmodel.ReviewViewModelFactory

// NAVIGATION
import com.example.temuin.navigation.Screen
import com.example.temuin.navigation.TemuinBottomBar
import com.example.temuin.navigation.profileFavoriteGraph
import com.example.temuin.navigation.reportGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //SupabaseClient.client.handleDeeplinks(intent)

        setContent {
            ReviewNavigation()
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewNavigation() {
    val navController = rememberNavController()

    // AUTH VIEWMODEL
    val authRepository = remember { AuthRepository() }
    val authViewModel: AuthViewModel = viewModel(
        factory = remember(authRepository) {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return AuthViewModel(authRepository) as T
                }
            }
        }
    )

    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserId = currentUser?.id ?: "user-01"
    val defaultUserName = currentUser?.name ?: "Trisha Malina"
    val currentNim = currentUser?.nim ?: "245150400111011"
    val currentStudy = "Sistem Informasi"

    // REPOSITORY & VIEWMODEL REVIEW
    val repository = remember { DummyReviewRepository() }
    val reviewViewModel: ReviewViewModel = viewModel(factory = remember(repository) { ReviewViewModelFactory(repository) })

    // VIEWMODEL PROFILE
    val profileViewModel: ProfileViewModel = viewModel(factory = remember { ProfileViewModelFactory(DummyProfileRepository()) })

    // VIEWMODEL FAVORITE
    val favoriteViewModel: FavoriteViewModel = viewModel(factory = remember { FavoriteViewModelFactory(DummyFavoriteRepository()) })

    // VIEWMODEL REPORT
    val reportViewModel: ReportViewModel = viewModel(factory = remember { ReportViewModelFactory(SupabaseReportRepository()) })

    val profileState by profileViewModel.uiState.collectAsState()
    val activeUserName = currentUser?.name ?: profileState.profile?.fullName ?: defaultUserName
    val activeUserPhone = currentUser?.phone?.takeIf { it.isNotBlank() } ?: profileState.profile?.phone ?: ""
    LaunchedEffect(currentUserId) {
        profileViewModel.load(currentUserId)
        // Laporan dibaca dari Supabase: tunggu sampai user benar-benar login.
        if (currentUser != null) reportViewModel.load(currentUserId)
        reviewViewModel.loadWrittenReviews(currentUserId)
    }

    val targetUserId = if (currentUserId == "user-01") "user-02" else "user-01"
    val targetUserName = if (currentUserId == "user-01") "Ailsa Nasywa" else "Trisha Malina"

    val demoContext = ReviewContext(
        role = ReviewRole.REPORTER,
        reportId = "FND-882",
        itemName = "Dompet Kulit Hitam"
    )

    // REPOSITORY & VIEWMODEL CLAIM
    val claimRepository = remember { ClaimRepository() }
    val claimViewModel: ClaimViewModel = viewModel(factory = remember(claimRepository) { ClaimViewModelFactory(claimRepository) })
    LaunchedEffect(currentUserId) {
        claimViewModel.loadClaims(currentUserId)
    }

    var showReportChoice by remember { mutableStateOf(false) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val mainRoutes = setOf(
        Screen.Home::class.qualifiedName,
        Screen.Search::class.qualifiedName,
        Screen.History::class.qualifiedName,
        Screen.Profile::class.qualifiedName
    )
    val showBottomBar = currentRoute in mainRoutes || (mainRoutes.any { it != null && currentRoute?.startsWith(it) == true }) && currentUser != null

    if (showReportChoice) {
        ModalBottomSheet(
            onDismissRequest = { showReportChoice = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Pilih Jenis Laporan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TemuinDark
                )
                Text(
                    text = "Pilih jenis laporan yang ingin kamu buat untuk warga kampus.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TemuinMuted
                )

                // Option 1: Found Item
                Card(
                    onClick = {
                        showReportChoice = false
                        navController.navigate(Screen.ReportAdd)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TemuinPaleBlue)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = TemuinTeal,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color.White)
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Lapor Barang Ditemukan",
                                fontWeight = FontWeight.Bold,
                                color = TemuinDark,
                                fontSize = 15.sp
                            )
                            Text(
                                "Saya menemukan barang milik orang lain di area kampus.",
                                fontSize = 12.sp,
                                color = TemuinMuted
                            )
                        }
                    }
                }

                // Option 2: Lost Item
                Card(
                    onClick = {
                        showReportChoice = false
                        navController.navigate(Screen.ReportLost)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE8E7))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFC62828),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = Color.White)
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Lapor Barang Hilang",
                                fontWeight = FontWeight.Bold,
                                color = TemuinDark,
                                fontSize = 15.sp
                            )
                            Text(
                                "Barang milik saya hilang atau tertinggal di area kampus.",
                                fontSize = 12.sp,
                                color = TemuinMuted
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) TemuinBottomBar(navController) { showReportChoice = true }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Login,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Login> {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home) {
                            popUpTo<Screen.Login> { inclusive = true }
                        }
                    },
                    onNavigateRegister = {
                        navController.navigate(Screen.Register)
                    }
                )
            }

            composable<Screen.Register> {
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home) {
                            popUpTo<Screen.Login> { inclusive = true }
                        }
                    },
                    onNavigateLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable<Screen.Search> {
                SearchScreen(
                    viewModel = reportViewModel,
                    userId = currentUserId,
                    onReportClick = { reportId ->
                        navController.navigate(Screen.ReportDetail(reportId))
                    }
                )
            }

            composable<Screen.History> { historyEntry ->
                val route: Screen.History = historyEntry.toRoute()
                HistoryScreen(
                    reportViewModel = reportViewModel,
                    claimViewModel = claimViewModel,
                    userId = currentUserId,
                    initialTab = route.tab,
                    onReportClick = { reportId ->
                        navController.navigate(Screen.ReportProgress(reportId))
                    },
                    onClaimClick = { claimId ->
                        navController.navigate(Screen.ClaimDetail(claimId))
                    }
                )
            }

            composable<Screen.ReportProgress> { entry ->
                val route: Screen.ReportProgress = entry.toRoute()
                ReportProgressScreen(
                    viewModel = reportViewModel,
                    claimViewModel = claimViewModel,
                    reviewViewModel = reviewViewModel,
                    reportId = route.reportId,
                    ownerId = currentUserId,
                    onBack = { navController.popBackStack() },
                    onRate = { rep: Report ->
                        val recipientId = if (rep.ownerId == currentUserId) "user-target" else rep.ownerId
                        val recipientName = if (rep.ownerId == currentUserId) "Pengguna Lain" else rep.ownerName
                        val role = if (rep.ownerId == currentUserId) ReviewRole.REPORTER else ReviewRole.CLAIMANT
                        navController.navigate(
                            Screen.ReviewForm(
                                role = role.name,
                                reportId = rep.id,
                                itemName = rep.itemName,
                                recipientId = recipientId,
                                recipientName = recipientName
                            )
                        )
                    }
                )
            }

            profileFavoriteGraph(
                navController,
                profileViewModel,
                favoriteViewModel,
                currentUserId,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )

            reportGraph(
                navController = navController,
                reportViewModel = reportViewModel,
                userId = currentUserId,
                userName = activeUserName,
                defaultContact = activeUserPhone,
                onIncomingClaims = { report -> navController.navigate(Screen.ReportProgress(report.id)) },
                onRate = { report ->
                    val recipientId = if (report.ownerId == currentUserId) targetUserId else report.ownerId
                    val recipientName = if (report.ownerId == currentUserId) targetUserName else report.ownerName
                    val role = if (report.ownerId == currentUserId) ReviewRole.REPORTER else ReviewRole.CLAIMANT
                    navController.navigate(
                        Screen.ReviewForm(
                            role = role.name,
                            reportId = report.id,
                            itemName = report.itemName,
                            recipientId = recipientId,
                            recipientName = recipientName
                        )
                    )
                }
            )

            composable<Screen.IncomingClaims> {
                IncomingClaimsScreen(
                    viewModel = claimViewModel,
                    ownerId = currentUserId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<Screen.ClaimDetail> { backStackEntry ->
                val route: Screen.ClaimDetail = backStackEntry.toRoute()
                val claim = claimViewModel.claims.value.firstOrNull { it.id == route.claimId }
                ClaimDetailScreen(
                    claim = claim,
                    onConfirmReceipt = {
                        claimViewModel.confirmHandover(route.claimId)
                        claimViewModel.loadClaims(currentUserId)
                        claim?.let { reportViewModel.markCompleted(it.itemId) }
                    },
                    onRate = {
                        val recipientId = if (claim?.reportOwnerId == currentUserId) claim?.userId ?: targetUserId else claim?.reportOwnerId ?: targetUserId
                        val recipientName = if (claim?.reportOwnerId == currentUserId) "Pengaju Klaim" else claim?.reportOwnerName ?: targetUserName
                        navController.navigate(
                            Screen.ReviewForm(
                                role = ReviewRole.CLAIMANT.name,
                                reportId = claim?.itemId ?: "",
                                itemName = claim?.itemName ?: "",
                                recipientId = recipientId,
                                recipientName = recipientName
                            )
                        )
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable<Screen.ClaimForm> { backStackEntry ->
                val route: Screen.ClaimForm = backStackEntry.toRoute()
                val itemName = route.itemName.ifBlank { "Barang Temuan" }
                ClaimFormScreen(
                    itemName = itemName,
                    onSubmit = { description ->
                        val found = claimViewModel.foundItems.value.firstOrNull { it.name == itemName }
                        claimViewModel.submitClaim(
                            Claim(
                                id = "CLM-${System.currentTimeMillis()}",
                                userId = currentUserId,
                                itemId = found?.id ?: itemName,
                                itemName = itemName,
                                claimDescription = description,
                                claimDate = java.time.LocalDate.now().toString(),
                                reportOwnerId = found?.ownerId ?: targetUserId,
                                reportOwnerName = found?.finderName ?: targetUserName,
                                claimantName = activeUserName
                            )
                        )
                        navController.navigate(Screen.History()) { popUpTo(Screen.Home) }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable<Screen.Home> {
                HomeScreen(
                    viewModel = reportViewModel,
                    userId = currentUserId,
                    userName = activeUserName,
                    avatarUrl = profileState.profile?.avatarUrl,
                    onReportClick = { reportId ->
                        navController.navigate(Screen.ReportDetail(reportId))
                    },
                    onEditClick = { reportId ->
                        navController.navigate(Screen.ReportEdit(reportId))
                    },
                    onCreateClick = {
                        showReportChoice = true
                    },
                    onProgressClick = { reportId -> navController.navigate(Screen.ReportProgress(reportId)) },
                    onProfileClick = { navController.navigate(Screen.Profile) }
                )
            }

            composable<Screen.UserReviews> {
                UserReviewsScreen(
                    viewModel = reviewViewModel,
                    currentUserId = currentUserId,
                    targetUserId = targetUserId,
                    targetUserName = targetUserName,
                    targetNim = "245150400111022",
                    targetStudy = "Sistem Informasi '23",
                    reviewContext = demoContext,
                    canWriteReview = true,
                    onBack = { navController.popBackStack() },
                    onWriteReview = { context ->
                        navController.navigate(
                            Screen.ReviewForm(
                                role = context.role.name,
                                reportId = context.reportId,
                                itemName = context.itemName
                            )
                        )
                    },
                    onEditReview = { review ->
                        navController.navigate(Screen.ReviewForm(reviewId = review.id))
                    }
                )
            }

            composable<Screen.MyReceivedReviews> {
                MyReceivedReviewsScreen(
                    viewModel = reviewViewModel,
                    currentUserId = currentUserId,
                    currentUserName = activeUserName,
                    currentNim = currentNim,
                    currentStudy = currentStudy,
                    onBack = { navController.popBackStack() },
                    onEditReview = { review ->
                        navController.navigate(Screen.ReviewForm(reviewId = review.id))
                    },
                    onViewWrittenReviews = {
                        navController.navigate(Screen.MyWrittenReviews)
                    }
                )
            }

            composable<Screen.MyWrittenReviews> {
                MyWrittenReviewsScreen(
                    viewModel = reviewViewModel,
                    currentUserId = currentUserId,
                    currentUserName = activeUserName,
                    onBack = { navController.popBackStack() },
                    onEditReview = { review ->
                        navController.navigate(Screen.ReviewForm(reviewId = review.id))
                    }
                )
            }

            composable<Screen.ReviewForm> { entry ->
                val route: Screen.ReviewForm = entry.toRoute()
                val reviewId = route.reviewId
                val existingReview = reviewId?.let { reviewViewModel.getReviewById(it) }
                val role = runCatching { ReviewRole.valueOf(route.role) }.getOrNull() ?: existingReview?.targetRole ?: demoContext.role

                val context = existingReview?.let {
                    ReviewContext(it.targetRole, it.reportId, it.itemName)
                } ?: ReviewContext(
                    role,
                    route.reportId.ifBlank { demoContext.reportId },
                    route.itemName.ifBlank { demoContext.itemName }
                )

                ReviewFormScreen(
                    viewModel = reviewViewModel,
                    currentUserId = currentUserId,
                    currentUserName = activeUserName,
                    targetUserId = existingReview?.reviewedUserId ?: route.recipientId.ifBlank { targetUserId },
                    targetUserName = existingReview?.reviewedUserName ?: route.recipientName.ifBlank { targetUserName },
                    reviewContext = context,
                    existingReview = existingReview,
                    onBack = { navController.popBackStack() },
                    onSubmitted = { navController.popBackStack() }
                )
            }
        }
    }
}
}


package com.example.temuin.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.temuin.feature.favorite.ui.screens.FavoriteDetailScreen
import com.example.temuin.feature.favorite.ui.screens.FavoriteScreen
import com.example.temuin.feature.favorite.ui.viewmodel.FavoriteViewModel
import com.example.temuin.feature.profile.ui.screens.EditProfileScreen
import com.example.temuin.feature.profile.ui.screens.ProfileScreen
import com.example.temuin.feature.profile.ui.viewmodel.ProfileViewModel

fun NavGraphBuilder.profileFavoriteGraph(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    favoriteViewModel: FavoriteViewModel,
    userId: String,
    onLogout: () -> Unit
) {
    composable<Screen.Profile> {
        ProfileScreen(
            viewModel = profileViewModel,
            favoriteViewModel = favoriteViewModel,
            userId = userId,
            onEdit = { navController.navigate(Screen.ProfileEdit) },
            onOpenClaims = { navController.navigate(Screen.History()) },
            onOpenFavorites = { navController.navigate(Screen.Favorites) },
            onOpenReviews = { navController.navigate(Screen.MyReceivedReviews) },
            onLogout = onLogout
        )
    }

    composable<Screen.ProfileEdit> {
        EditProfileScreen(
            viewModel = profileViewModel,
            userId = userId,
            onBack = { navController.popBackStack() },
            onDone = { navController.popBackStack() }
        )
    }

    composable<Screen.Favorites> {
        FavoriteScreen(
            viewModel = favoriteViewModel,
            userId = userId,
            onBack = { navController.popBackStack() },
            onItemClick = { reportId -> navController.navigate(Screen.FavoriteDetail(reportId)) }
        )
    }

    composable<Screen.FavoriteDetail> { entry ->
        val route: Screen.FavoriteDetail = entry.toRoute()
        FavoriteDetailScreen(
            viewModel = favoriteViewModel,
            userId = userId,
            reportId = route.reportId,
            onBack = { navController.popBackStack() }
        )
    }
}

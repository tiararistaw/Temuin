package com.example.temuin.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.temuin.feature.report.data.model.Report
import com.example.temuin.feature.report.data.model.ReportType
import com.example.temuin.feature.report.ui.screens.ReportDetailScreen
import com.example.temuin.feature.report.ui.screens.ReportFormScreen
import com.example.temuin.feature.report.ui.screens.ReportSuccessScreen
import com.example.temuin.feature.report.ui.viewmodel.ReportViewModel

fun NavGraphBuilder.reportGraph(
    navController: NavController,
    reportViewModel: ReportViewModel,
    userId: String,
    userName: String,
    defaultContact: String,
    onRate: (Report) -> Unit = {},
    onIncomingClaims: (Report) -> Unit = {}
) {
    composable<Screen.ReportAdd> {
        ReportFormScreen(
            viewModel = reportViewModel,
            userId = userId,
            userName = userName,
            defaultContact = defaultContact,
            reportId = null,
            onBack = {
                navController.navigate(Screen.Home) {
                    popUpTo(Screen.Home) { inclusive = false }
                    launchSingleTop = true
                }
            },
            onPublished = { newId ->
                navController.navigate(Screen.ReportSuccess(newId)) {
                    popUpTo<Screen.ReportAdd> { inclusive = true }
                }
            }
        )
    }

    composable<Screen.ReportLost> {
        ReportFormScreen(
            viewModel = reportViewModel,
            userId = userId,
            userName = userName,
            defaultContact = defaultContact,
            reportId = null,
            initialType = ReportType.LOST,
            onBack = { navController.popBackStack() },
            onPublished = { newId ->
                navController.navigate(Screen.ReportSuccess(newId)) {
                    popUpTo(Screen.Home) { inclusive = false }
                }
            }
        )
    }

    composable<Screen.ReportEdit> { entry ->
        val route: Screen.ReportEdit = entry.toRoute()
        ReportFormScreen(
            viewModel = reportViewModel,
            userId = userId,
            userName = userName,
            defaultContact = defaultContact,
            reportId = route.reportId,
            onBack = { navController.popBackStack() },
            onPublished = { },
            onUpdated = { navController.popBackStack() }
        )
    }

    composable<Screen.ReportSuccess> { entry ->
        val route: Screen.ReportSuccess = entry.toRoute()
        ReportSuccessScreen(
            viewModel = reportViewModel,
            userId = userId,
            reportId = route.reportId,
            onViewDetail = {
                navController.navigate(Screen.ReportDetail(route.reportId)) {
                    popUpTo(Screen.Home) { inclusive = false }
                }
            },
            onHome = {
                navController.navigate(Screen.Home) {
                    popUpTo(Screen.Home) { inclusive = false }
                    launchSingleTop = true
                }
            },
            onHistory = {
                navController.navigate(Screen.History("Laporan")) {
                    popUpTo(Screen.Home) { inclusive = false }
                }
            }
        )
    }

    composable<Screen.ReportDetail> { entry ->
        val route: Screen.ReportDetail = entry.toRoute()
        ReportDetailScreen(
            viewModel = reportViewModel,
            userId = userId,
            reportId = route.reportId,
            onBack = { navController.popBackStack() },
            onEdit = { reportId -> navController.navigate(Screen.ReportEdit(reportId)) },
            onClaim = { report ->
                navController.navigate(Screen.ClaimForm(report.itemName))
            },
            onRate = onRate,
            onIncomingClaims = onIncomingClaims
        )
    }
}

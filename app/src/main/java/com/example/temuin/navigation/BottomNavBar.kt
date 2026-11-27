package com.example.temuin.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.temuin.feature.review.ui.components.TemuinMuted
import com.example.temuin.feature.review.ui.components.TemuinTeal

@Composable
fun TemuinBottomBar(navController: NavController, onAdd: () -> Unit) {
    val entry by navController.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    val mainRoutes = setOf(
        Screen.Home::class.qualifiedName,
        Screen.Search::class.qualifiedName,
        Screen.History::class.qualifiedName,
        Screen.Profile::class.qualifiedName
    )
    if (route !in mainRoutes && mainRoutes.none { it != null && route?.startsWith(it) == true }) return

    Surface(color = Color.White, shadowElevation = 8.dp) {
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().height(76.dp).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavSlot(Screen.Home, "Beranda", Icons.Outlined.Home, route, navController)
            NavSlot(Screen.Search, "Cari", Icons.Outlined.Search, route, navController)
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                FloatingActionButton(
                    onClick = onAdd,
                    containerColor = TemuinTeal,
                    contentColor = Color.White,
                    modifier = Modifier.size(58.dp)
                ) { Icon(Icons.Outlined.Add, contentDescription = "Lapor", modifier = Modifier.size(30.dp)) }
            }
            NavSlot(Screen.History(), "Riwayat", Icons.Outlined.AccessTime, route, navController)
            NavSlot(Screen.Profile, "Profil", Icons.Outlined.Person, route, navController)
        }
    }
}

@Composable
private fun RowScope.NavSlot(
    targetScreen: Screen,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    currentRoute: String?,
    navController: NavController
) {
    val targetRoute = targetScreen::class.qualifiedName
    val selected =
        currentRoute == targetRoute || (targetRoute != null && currentRoute?.startsWith(targetRoute) == true)
    Column(
        Modifier.weight(1f).fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = {
                navController.navigate(targetScreen) {
                    popUpTo<Screen.Home> {
                        inclusive = false
                        saveState = false
                    }
                    launchSingleTop = true
                    restoreState = false
                }
            }
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (selected) TemuinTeal else TemuinMuted
            )
        }
    }
}

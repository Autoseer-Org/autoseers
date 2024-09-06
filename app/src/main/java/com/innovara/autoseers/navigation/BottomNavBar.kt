package com.innovara.autoseers.navigation

import androidx.compose.material3.Icon
import androidx.compose.material.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.innovara.autoseers.navigation.routes.GlobalRoute

data class BottomNavItem(
    val route: GlobalRoute,
    val icon: ImageVector,
    val iconSelected: ImageVector,
    val label: String
)

@Composable
fun BottomNavBar(navController: NavController, items: List<BottomNavItem>) {
    NavigationBar(
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        items.forEach { item ->
            val isItemSelected = navBackStackEntry?.getRouteLastSegmentName() == item.route.toString()

            NavigationBarItem(
                icon = {
                       if (isItemSelected) {
                           Icon(item.iconSelected, contentDescription = item.label)
                       } else {
                           Icon(item.icon, contentDescription = item.label)
                       }
                },
                label = { Text(item.label) },
                selected = isItemSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
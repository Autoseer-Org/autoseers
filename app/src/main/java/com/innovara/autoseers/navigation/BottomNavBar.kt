package com.innovara.autoseers.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.innovara.autoseers.navigation.routes.AutoSeersExperience
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
                           Icon(item.iconSelected, contentDescription = item.label, tint = MaterialTheme.colorScheme.onPrimary)
                       } else {
                           Icon(item.icon, contentDescription = item.label)
                       }
                },
                label = { Text(item.label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface) },
                selected = isItemSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(AutoSeersExperience){
                            inclusive = false
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
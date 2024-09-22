package com.innovara.autoseers.navigation

import androidx.navigation.NavBackStackEntry
import com.innovara.autoseers.navigation.routes.homeroute.HomeRoute
import com.innovara.autoseers.navigation.routes.recommendations.RecommendationsRoute
import com.innovara.autoseers.navigation.routes.settings.SettingsRoute

object NavBar {
    val allowedScreensList = listOf(
        HomeRoute.toString(),
        RecommendationsRoute.toString(),
        SettingsRoute.toString()
    )
}

/*
 * Currently route names from the [backStackEntry] are fetched with the full path of the project
 * This function gets the last segment of the path project which is the route name we need
 */
internal fun NavBackStackEntry.getRouteLastSegmentName(): String {
    return destination.route?.split(".")?.last() ?: ""
}

/*
 * Checks to see if the current route name belongs to the allowed list of screens
 * that can see the bottom nav bar
 */
internal fun NavBackStackEntry.isAllowedToSeeBottomNavBar() =
    getRouteLastSegmentName() in NavBar.allowedScreensList
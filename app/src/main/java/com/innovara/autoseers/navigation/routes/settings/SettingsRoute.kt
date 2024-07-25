package com.innovara.autoseers.navigation.routes.settings

import com.innovara.autoseers.navigation.routes.GlobalRoute
import kotlinx.serialization.Serializable

@Serializable
object SettingsRoute: GlobalRoute() {
    override fun toString(): String = "SettingsRoute"
}
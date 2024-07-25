package com.innovara.autoseers.navigation.routes.maps

import com.innovara.autoseers.navigation.routes.GlobalRoute
import kotlinx.serialization.Serializable

@Serializable
object MapsRoute: GlobalRoute() {
    override fun toString(): String = "MapsRoute"
}
package com.kyang.tftteambuilder.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.kyang.tftteambuilder.R

interface NavDestination {
    val route: String
    val labelRes: Int
    val icon: ImageVector
}

object Home : NavDestination {
    override val route = "home"
    override val labelRes = R.string.home_route
    override val icon = Icons.Filled.Home
}

object Detail : NavDestination {
    override val route = "detail"
    override val labelRes = R.string.detail_route
    override val icon = Icons.Default.Edit
}
package com.kyang.tftteambuilder.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kyang.tftteambuilder.ui.detail.DetailPane
import com.kyang.tftteambuilder.ui.home.HomePane
import com.kyang.tftteambuilder.ui.home.HomeViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
     NavHost(navController = navController, startDestination = Home.route, modifier = modifier) {
          composable(route = Home.route) {
               val viewModel = hiltViewModel<HomeViewModel>()
               viewModel.loadData()
               HomePane(viewModel = viewModel)
          }

          composable(route = Detail.route) {
               DetailPane()
          }
     }
}
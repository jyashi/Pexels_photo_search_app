package com.example.pexels_photo_search_app

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pexels_photo_search_app.data.network.ApiClient
import com.example.pexels_photo_search_app.model.MainViewModel
import com.example.pexels_photo_search_app.ui.screen.HomeScreen
import com.example.pexels_photo_search_app.ui.screen.ImageDetailScreen
import io.ktor.client.engine.cio.CIO

@Composable
fun NavGraph() {

    val apiClient = ApiClient(CIO.create())
    val viewModel = MainViewModel(apiClient)
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavRoutes.Home) {
        composable(NavRoutes.Home) { HomeScreen(viewModel, navController) }
        composable("${NavRoutes.ImageDetail}/{photoId}", arguments = listOf(navArgument("photoId") { type = NavType.IntType })) { backStackEntry ->
            ImageDetailScreen(viewModel = viewModel, photoId = backStackEntry.arguments?.getInt("photoId"))
        }
    }
}
object NavRoutes {
    const val Home = "homeScreen"
    const val ImageDetail = "imageDetail"
}
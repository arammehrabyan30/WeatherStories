package com.aram.mehrabyan.weatherstories.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aram.mehrabyan.weatherstories.presentation.stories.StoriesScreen
import com.aram.mehrabyan.weatherstories.presentation.weather.WeatherScreen

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String) {
    data object Weather : Screen("weather")
    data object Stories : Screen("stories/{city}") {
        fun createRoute(city: String) = "stories/$city"
    }
}

/**
 * Main navigation component
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Weather.route
    ) {
        composable(Screen.Weather.route) {
            WeatherScreen(
                onNavigateToStories = { city ->
                    navController.navigate(Screen.Stories.createRoute(city))
                }
            )
        }

        composable(
            route = Screen.Stories.route,
            arguments = listOf(
                navArgument("city") { type = NavType.StringType }
            )
        ) {
            StoriesScreen(
                onClose = {
                    navController.popBackStack()
                }
            )
        }
    }
}


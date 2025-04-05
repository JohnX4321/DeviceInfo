package com.thingsenz.devinfo.ui.nav

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thingsenz.devinfo.HomeScreen
import com.thingsenz.devinfo.ui.screen.SettingsScreen
import com.thingsenz.devinfo.ui.screen.AboutScreen

@Composable
fun NavigationStack() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route,
        modifier = Modifier.fillMaxSize(),
       /* enterTransition = {
            slideInHorizontally(
               // initialOffsetX = { fullWidth -> fullWidth * direction },
                animationSpec = tween(durationMillis = 500)
            ) + fadeIn(animationSpec = tween(durationMillis = 500))
        },
        exitTransition = {
            slideOutHorizontally(
               // targetOffsetX = { fullWidth -> -fullWidth * direction },
                animationSpec = tween(durationMillis = 500)
            ) + fadeOut(animationSpec = tween(durationMillis = 500))
        }*/) {

        composable(route = Screen.Home.route) {

            HomeScreen(navController)

        }
        composable(route = Screen.Settings.route) {
            SettingsScreen(navController)
        }

        composable(route = Screen.Libraries.route) {
            AboutScreen(navController)
        }

    }

}
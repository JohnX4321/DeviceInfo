package com.thingsenz.devinfo.ui.nav

sealed class Screen(val route: String) {

    data object Home: Screen("home_screen")
    data object About: Screen("about_screen")
    data object Libraries: Screen("libraries_screen")
    data object Settings: Screen("settings_screen")

}
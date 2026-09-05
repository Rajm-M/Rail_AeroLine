package com.example.rail_aeroline.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Admin : Screen("admin", "Admin Settings")
    object DeviceSetup : Screen("device_setup", "Device Setup")
    object Capture : Screen("capture", "Data Capture")
    object LogHistory : Screen("log_history", "Log History")
    object Report : Screen("report", "Reports")
}

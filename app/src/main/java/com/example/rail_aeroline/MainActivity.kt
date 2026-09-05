package com.example.rail_aeroline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rail_aeroline.model.UserRole
import com.example.rail_aeroline.ui.admin.AdminScreen
import com.example.rail_aeroline.ui.admin.DeviceSetupScreen
import com.example.rail_aeroline.ui.capture.CaptureScreen
import com.example.rail_aeroline.ui.history.LogHistoryScreen
import com.example.rail_aeroline.ui.home.HomeScreen
import com.example.rail_aeroline.ui.report.ReportScreen
import com.example.rail_aeroline.ui.navigation.Screen
import com.example.rail_aeroline.ui.theme.Rail_AeroLineTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import android.app.ActivityManager

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Rail_AeroLineTheme {
                MainContent { enable ->
                    if (enable) startLockTask() else stopLockTask()
                }
            }
        }
    }

    fun isKioskModeActive(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        return activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(onLockTask: (Boolean) -> Unit) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Mocking current user role - would normally come from a ViewModel
    var currentUserRole by remember { mutableStateOf(UserRole.ADMIN) }

    val navigationItems = listOf(
        Screen.Home,
        Screen.Capture,
        Screen.LogHistory,
        Screen.Report
    ).toMutableList()

    // Dynamically add Admin items based on role
    if (currentUserRole == UserRole.ADMIN || currentUserRole == UserRole.SUPER_USER) {
        navigationItems.add(Screen.Admin)
        navigationItems.add(Screen.DeviceSetup)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Rail AeroLine",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                HorizontalDivider()
                navigationItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.title) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route)
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Rail AeroLine") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) { HomeScreen() }
                composable(Screen.Admin.route) { AdminScreen(onLockTask = onLockTask) }
                composable(Screen.DeviceSetup.route) { DeviceSetupScreen() }
                composable(Screen.Capture.route) { CaptureScreen() }
                composable(Screen.LogHistory.route) { LogHistoryScreen() }
                composable(Screen.Report.route) { ReportScreen() }
            }
        }
    }
}

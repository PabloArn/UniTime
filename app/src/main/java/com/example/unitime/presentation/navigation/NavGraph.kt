package com.example.unitime.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.unitime.presentation.schedule.ScheduleScreen

object Routes {
    const val SCHEDULE = "schedule"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SCHEDULE
    ) {
        composable(Routes.SCHEDULE) {
            ScheduleScreen(navController = navController)
        }
    }
}
package com.example.unitime.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.unitime.presentation.schedule.HorarioScreen
import com.example.unitime.presentation.schedule.AgregarClaseScreen
import com.example.unitime.presentation.tasks.AgregarTareaScreen
import com.example.unitime.presentation.focus.EnfoqueScreen
import com.example.unitime.presentation.auth.LoginScreen // <-- NUEVO IMPORT
import androidx.navigation.navArgument
import androidx.navigation.NavType

// Centralizamos las rutas en español para evitar errores de tipeo
object Rutas {
    const val HORARIO = "horario"
    const val AGREGAR_CLASE = "agregar_clase"
    const val AGREGAR_TAREA = "agregar_tarea"
    const val ENFOQUE = "enfoque"
    const val LOGIN = "login"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN // <-- CAMBIO: Ahora la app arranca en el Login
    ) {
        // --- NUEVA PANTALLA DE LOGIN ---
        composable(Rutas.LOGIN) {
            LoginScreen(navController = navController)
        }

        // Pantalla principal donde se verán las tarjetas de las materias
        composable(Rutas.HORARIO) {
            HorarioScreen(navController = navController)
        }

        composable(
            route = Rutas.AGREGAR_CLASE + "?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val idStr = backStackEntry.arguments?.getString("id")
            val id = idStr?.toLongOrNull()

            // Le pasamos el ID a la pantalla. Si es nuevo, el ID será 'null'
            AgregarClaseScreen(navController = navController, claseId = id)
        }

        composable(
            route = Rutas.AGREGAR_TAREA + "?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val idStr = backStackEntry.arguments?.getString("id")
            val id = idStr?.toLongOrNull()

            // Le pasamos el ID a la pantalla
            AgregarTareaScreen(navController = navController, tareaId = id)
        }

        composable(Rutas.ENFOQUE) {
            EnfoqueScreen(navController = navController)
        }
    }
}
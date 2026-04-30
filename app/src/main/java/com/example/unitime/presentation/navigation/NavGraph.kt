package com.example.unitime.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.unitime.presentation.schedule.HorarioScreen
import com.example.unitime.presentation.schedule.AgregarClaseScreen

// Centralizamos las rutas en español para evitar errores de tipeo
object Rutas {
    const val HORARIO = "horario"
    const val AGREGAR_CLASE = "agregar_clase"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.HORARIO
    ) {
        // Pantalla principal donde se verán las tarjetas de las materias
        composable(Rutas.HORARIO) {
            HorarioScreen(navController = navController)
        }

        // Pantalla del formulario para registrar una nueva materia
        composable(Rutas.AGREGAR_CLASE) {
            AgregarClaseScreen(navController = navController)
        }
    }
}
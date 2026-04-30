package com.example.unitime.presentation.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.unitime.presentation.navigation.Rutas

@Composable
fun HorarioScreen(navController: NavController) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Navegamos al formulario usando la ruta en español
                    navController.navigate(Rutas.AGREGAR_CLASE)
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar clase")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "¡UniTime funcionando! 🎉\nAquí irán las tarjetas de las clases.")
        }
    }
}
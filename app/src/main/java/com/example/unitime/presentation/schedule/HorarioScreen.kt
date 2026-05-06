package com.example.unitime.presentation.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.unitime.data.local.entity.ClaseEntity
import com.example.unitime.presentation.navigation.Rutas

@Composable
fun HorarioScreen(
    navController: NavController,
    viewModel: HorarioViewModel = hiltViewModel()
) {
    val clases: List<ClaseEntity> by viewModel.clases.collectAsState(initial = emptyList())

    // Variable para saber qué clase quiere borrar el usuario.
    // Si es null, no mostramos el diálogo. Si tiene una clase, lanzamos la alerta.
    var claseParaBorrar by remember { mutableStateOf<ClaseEntity?>(null) }

    // DIÁLOGO DE CONFIRMACIÓN (Cumpliendo el CU-07)
    claseParaBorrar?.let { clase ->
        AlertDialog(
            onDismissRequest = { claseParaBorrar = null }, // Si toca fuera, cancelamos
            title = { Text("Eliminar clase") },
            text = { Text("¿Estás seguro de eliminar '${clase.nombre}' y sus tareas vinculadas?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarClase(clase)
                        claseParaBorrar = null // Cerramos el diálogo después de borrar
                    }
                ) {
                    Text("Confirmar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { claseParaBorrar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Rutas.AGREGAR_CLASE) }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar clase")
            }
        }
    ) { paddingValues ->

        if (clases.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Aún no tienes clases. ¡Agrega una!")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(clases) { clase ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        // Usamos un Row para alinear el texto a la izquierda y el botón de borrar a la derecha
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = clase.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Salón: ${clase.salon} | Edificio: ${clase.edificio}")
                                Text(text = "${clase.diasDeLaSemana} • ${clase.horaInicio} - ${clase.horaFin}")
                            }

                            // Botón de eliminar con su icono rojo para alertar al estudiante
                            IconButton(onClick = { claseParaBorrar = clase }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar ${clase.nombre}",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
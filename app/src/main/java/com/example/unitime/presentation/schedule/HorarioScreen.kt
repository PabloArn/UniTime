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
    var claseParaBorrar by remember { mutableStateOf<ClaseEntity?>(null) }

    //Estado para controlar si el menú de agregar está abierto o cerrado
    var mostrarMenuAgregar by remember { mutableStateOf(false) }

    claseParaBorrar?.let { clase ->
        AlertDialog(
            onDismissRequest = { claseParaBorrar = null },
            title = { Text("Eliminar clase") },
            text = { Text("¿Estás seguro de eliminar '${clase.nombre}' y sus tareas vinculadas?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarClase(clase)
                        claseParaBorrar = null
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
            // Envolvemos el botón en un Box para que el menú desplegable se ancle a él
            Box {
                FloatingActionButton(
                    onClick = { mostrarMenuAgregar = true },
                    containerColor = MaterialTheme.colorScheme.primary, // Color azul/principal del Figma
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Opciones de agregar")
                }

                // Menú desplegable con las dos opciones
                DropdownMenu(
                    expanded = mostrarMenuAgregar,
                    onDismissRequest = { mostrarMenuAgregar = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("¡Nueva Tarea!") },
                        onClick = {
                            mostrarMenuAgregar = false
                            navController.navigate(Rutas.AGREGAR_TAREA) // Navega a la pantalla que acabamos de crear
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("¡Nueva Materia!") },
                        onClick = {
                            mostrarMenuAgregar = false
                            navController.navigate(Rutas.AGREGAR_CLASE)
                        }
                    )
                }
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
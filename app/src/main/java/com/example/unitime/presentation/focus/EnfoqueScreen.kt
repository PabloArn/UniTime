package com.example.unitime.presentation.focus

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.RingtoneManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnfoqueScreen(
    navController: NavController,
    viewModel: EnfoqueViewModel = hiltViewModel()
) {
    val contexto = LocalContext.current
    var inputMinutos by remember { mutableStateOf("25") }

    // --- LÓGICA DEL SENSOR (Acelerómetro) ---
    DisposableEffect(Unit) {
        val sensorManager = contexto.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                val z = event.values[2] // El eje Z nos dice si está boca arriba o boca abajo

                // Si Z es mayor a -5.0, significa que ya no está plano boca abajo.
                // Si el estudiante está en modo enfoque y levanta el celular -> Penalización
                if (z > -5.0f && viewModel.estaEnfocado) {
                    viewModel.penalizarPorLevantar()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        // Registramos el sensor solo mientras esta pantalla exista
        sensorManager.registerListener(sensorListener, acelerometro, SensorManager.SENSOR_DELAY_NORMAL)

        onDispose {
            // Es vital apagar el sensor al salir para no drenar la batería
            sensorManager.unregisterListener(sensorListener)
        }
    }

    // --- LÓGICA DE LA ALARMA SONORA ---
    LaunchedEffect(viewModel.alarmaActivada) {
        if (viewModel.alarmaActivada) {
            val uriAlarma = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val ringtone = RingtoneManager.getRingtone(contexto, uriAlarma)
            ringtone.play()

            delay(3000L) // Suena por 3 segundos
            ringtone.stop()
        }
    }

    // --- INTERFAZ GRÁFICA ---
    // Si la alarma se activa, el fondo se vuelve rojo intenso
    val colorFondo = if (viewModel.alarmaActivada) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.background

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modo Enfoque") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.detenerEnfoque()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorFondo)
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!viewModel.estaEnfocado && !viewModel.alarmaActivada) {
                // ESTADO 1: Configurar tiempo
                Icon(
                    imageVector = Icons.Default.Add, // Puedes cambiarlo por un ícono de reloj
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = inputMinutos,
                    onValueChange = { inputMinutos = it },
                    label = { Text("Minutos de estudio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.iniciarEnfoque(inputMinutos.toIntOrNull() ?: 0) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Empezar a Estudiar")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Al empezar, pon tu celular BOCA ABAJO. Si lo levantas, sonará una alarma.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (viewModel.alarmaActivada) {
                // ESTADO 2: Trampa detectada (Alarma)
                Text(
                    text = "¡CONCÉNTRATE!",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Se pausó tu tiempo por mirar el teléfono.")
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.detenerEnfoque() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Rendirse y salir")
                }
            } else {
                // ESTADO 3: Enfocado correctamente (Cronómetro activo)
                val minutos = viewModel.tiempoRestanteEnSegundos / 60
                val segundos = viewModel.tiempoRestanteEnSegundos % 60
                val tiempoFormateado = String.format("%02d:%02d", minutos, segundos)

                Text(
                    text = tiempoFormateado,
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Celular boca abajo. ¡No lo toques!")
                Spacer(modifier = Modifier.height(48.dp))
                OutlinedButton(onClick = { viewModel.detenerEnfoque() }) {
                    Text("Cancelar temporizador")
                }
            }
        }
    }
}
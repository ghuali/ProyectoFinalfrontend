package Screen

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import model.NuevaTarea
import model.Proyecto
import model.Tarea
import network.asignarTarea
import network.obtenerTareas

class ProyectoScreen(val proyecto: Proyecto) : Screen {
    @Composable
    override fun Content() {
        var tareas by remember { mutableStateOf<List<Tarea>>(emptyList()) }
        var nombreTarea by remember { mutableStateOf("") }
        var descripcionTarea by remember { mutableStateOf("") }
        var estimacionTarea by remember { mutableStateOf("") }
        var fechaFinalizacion by remember { mutableStateOf("") }
        val navigator = LocalNavigator.current

        // Obtener tareas del proyecto al cargar la pantalla
        LaunchedEffect(proyecto.id) {
            obtenerTareas(proyecto.id) { listaTareas ->
                tareas = listaTareas
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(16.dp)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color(0xFF1976D2)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = proyecto.nombre,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text("Descripción: ${proyecto.descripcion}", fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))
                Text("Fecha de creación: ${proyecto.fecha_creacion}", fontSize = 16.sp)
                Text("Fecha de inicio: ${proyecto.fecha_inicio}", fontSize = 16.sp)
                Text("Cliente ID: ${proyecto.cliente}", fontSize = 16.sp)

                Spacer(modifier = Modifier.height(16.dp))
                Text("Tareas del Proyecto", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                // Mostrar tareas obtenidas desde la API
                if (tareas.isEmpty()) {
                    Text("No hay tareas asignadas", fontSize = 16.sp, fontStyle = FontStyle.Italic)
                } else {
                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        items(tareas) { tarea ->
                            TaskItem(tarea)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Nueva Tarea", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = nombreTarea,
                    onValueChange = { nombreTarea = it },
                    label = { Text("Nombre de la tarea") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = descripcionTarea,
                    onValueChange = { descripcionTarea = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = estimacionTarea,
                    onValueChange = { estimacionTarea = it },
                    label = { Text("Estimación ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = fechaFinalizacion,
                    onValueChange = { fechaFinalizacion = it },
                    label = { Text("Fecha de finalización (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val nuevaTarea = NuevaTarea(
                            nombre = nombreTarea,
                            descripcion = descripcionTarea,
                            estimacion = estimacionTarea.toIntOrNull() ?: 0,
                            fecha_creacion = "2025-02-11", // Puedes cambiar esto por la fecha actual
                            fecha_finalizacion = fechaFinalizacion.ifEmpty { null },
                            programador = 1, // Deberías permitir seleccionar un programador
                            proyecto = proyecto.id
                        )

                        asignarTarea(nuevaTarea) { tarea ->
                            tareas = tareas + tarea
                            // Limpiar los campos después de agregar la tarea
                            nombreTarea = ""
                            descripcionTarea = ""
                            estimacionTarea = ""
                            fechaFinalizacion = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(Color(0xFF1976D2))
                ) {
                    Text("Agregar Tarea", color = Color.White)
                }
            }

            Button(
                onClick = { navigator?.pop() },
                modifier = Modifier.padding(16.dp),
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text("Volver", fontSize = 20.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun TaskItem(tarea: Tarea) {
    val navigator = LocalNavigator.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(tarea.nombre, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navigator?.push(TareaScreen(tarea)) }) {
                Text("Entrar a la Tarea")
            }
        }
    }
}




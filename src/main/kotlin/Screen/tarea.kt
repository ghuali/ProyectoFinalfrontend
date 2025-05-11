package Screen

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import model.Tarea

class TareaScreen(val tarea: Tarea) : Screen {
    @Composable
    override fun Content() {
        var expandedProgramadores by remember { mutableStateOf(false) }
        var selectedProgramador by remember { mutableStateOf("Seleccionar programador") }
        val programadores = listOf("Programador 1", "Programador 2", "Programador 3") // Esto debería venir de una API
        val navigator = LocalNavigator.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            // Bloque superior con nombre de la empresa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color(0xFF1976D2)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nombre de Empresa",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = tarea.nombre,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Text(
                    text = "Descripción: ${tarea.descripcion}",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text("Estimación: ${tarea.estimacion} horas", fontSize = 18.sp)
                Text("Fecha de Creación: ${tarea.fecha_creacion}", fontSize = 18.sp)
                Text("Fecha de Finalización: ${tarea.fecha_finalizacion ?: "Pendiente"}", fontSize = 18.sp)
                Text("ID del Programador Asignado: ${tarea.programador}", fontSize = 18.sp)
                Text("ID del Proyecto: ${tarea.proyecto}", fontSize = 18.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown para asignar programador
                Text(
                    text = "Asignar programador a la tarea",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Box {
                    Button(onClick = { expandedProgramadores = true }) {
                        Text(selectedProgramador)
                    }
                    DropdownMenu(
                        expanded = expandedProgramadores,
                        onDismissRequest = { expandedProgramadores = false }
                    ) {
                        programadores.forEach { programador ->
                            DropdownMenuItem(onClick = {
                                selectedProgramador = programador
                                expandedProgramadores = false
                            }) {
                                Text(programador)
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { navigator?.pop() },
                modifier = Modifier
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text("Volver", fontSize = 20.sp, color = Color.White)
            }
        }
    }
}

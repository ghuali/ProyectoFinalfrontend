package Screen

import ViewModel.SessionManager
import ViewModel.SessionManager.currentUser
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import model.User


class EquipoScreen: Screen {

    @Composable
    override fun Content() {
        val usuario = SessionManager.currentUser

        EquiposPantalla(
            usuario = usuario,
            onLogout = {
                SessionManager.authToken = null
                SessionManager.currentUser = null
            }
        )
    }

    @Composable
    fun EquiposPantalla(
        usuario: User?,
        onLogout: () -> Unit
    ) {
        val navigator = LocalNavigator.current
        val tieneEquipo = remember { mutableStateOf(false) } // Simulación, luego lo reemplazas con datos reales

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFEB3B))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            navigator?.pop()
                        }
                    ) {
                        Image(
                            painter = painterResource("CanaryEsportsImg.png"),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(50.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "CANARY'S ESPORTS",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Row {
                        var expanded by remember { mutableStateOf(false) }

                        Box {
                            Text(
                                currentUser?.nombre ?: "Usuario",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { expanded = true }
                            )

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                DropdownMenuItem(onClick = {
                                    expanded = false
                                    onLogout()
                                    navigator?.pop()
                                }) {
                                    Text("Cerrar sesión")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                usuario?.let {
                    if (tieneEquipo.value) {
                        mostrarEquipoBloque()
                    } else {
                        crearEquipoBloque()
                    }
                }
            }

            // Botón volver
            Button(
                onClick = { navigator?.pop() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD32F2F))
            ) {
                Text("Volver", fontSize = 20.sp, color = Color.White)
            }
        }
    }


    @Composable
    fun crearEquipoBloque() {
        var mostrarDialogo by remember { mutableStateOf(false) }
        var codigoNoEncontrado by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Mensaje y botón de crear equipo
            Text(
                "Aún no formas parte de un equipo",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { mostrarDialogo = true },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50))
            ) {
                Text("Crear equipo", color = Color.White, fontSize = 16.sp)
            }

            // Más espacio antes del bloque de unirse
            Spacer(modifier = Modifier.height(64.dp))

            // Bloque central para unirse a un equipo por código
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                backgroundColor = Color(0xFF1E1E1E),
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UnirseEquipoConCodigoBloque(
                        onBuscarEquipo = { codigoIngresado ->
                            println("Buscar equipo con código: $codigoIngresado")
                            // Simular búsqueda
                            val encontrado = false
                            codigoNoEncontrado = if (encontrado) null else "No se encontró ningún equipo con ese código."
                        },
                        mensajeError = codigoNoEncontrado
                    )
                }
            }
        }

        // Diálogo para crear equipo
        if (mostrarDialogo) {
            CrearEquipoDialog(
                onDismiss = { mostrarDialogo = false },
                onCrearEquipo = { nombreEquipo ->
                    mostrarDialogo = false
                    println("Equipo creado: $nombreEquipo")
                    // Llamada al backend pendiente
                }
            )
        }
    }



    @Composable
    fun mostrarEquipoBloque() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                "Tu equipo:",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Aquí más adelante usarás datos reales
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.DarkGray,
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nombre: LosCracks", color = Color.White)
                    Text("Código: ABC123", color = Color.White)
                    Text("Miembros: 5", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    // ver detalles, editar, etc.
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2196F3))
            ) {
                Text("Ver detalles", color = Color.White)
            }
        }
    }

    @Composable
    fun CrearEquipoDialog(
        onDismiss: () -> Unit,
        onCrearEquipo: (String) -> Unit
    ) {
        var nombreEquipo by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Crear nuevo equipo")
            },
            text = {
                Column {
                    Text("Introduce el nombre de tu equipo:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nombreEquipo,
                        onValueChange = { nombreEquipo = it },
                        label = { Text("Nombre del equipo") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nombreEquipo.isNotBlank()) {
                            onCrearEquipo(nombreEquipo.trim())
                        }
                    }
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }

    @Composable
    fun EquipoDetalles(
        nombre: String,
        codigo: String,
        miembros: List<String>
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                "Detalles del equipo:",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                backgroundColor = Color.DarkGray,
                elevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nombre: $nombre", color = Color.White)
                    Text("Código: $codigo", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Miembros:", color = Color.White, fontWeight = FontWeight.Bold)
                    miembros.forEach {
                        Text("- $it", color = Color.White)
                    }
                }
            }
        }
    }

    @Composable
    fun CodigoEquipoBloque(codigo: String) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                "Código del equipo (solo visible para el fundador):",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(codigo, color = Color.White, fontSize = 18.sp)
            }
        }
    }

    @Composable
    fun UnirseEquipoConCodigoBloque(
        onBuscarEquipo: (String) -> Unit,
        mensajeError: String? = null
    ) {
        var codigo by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                "¿Tienes un código de equipo?",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = { Text("Introduce el código") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (codigo.isNotBlank()) {
                        onBuscarEquipo(codigo.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1976D2))
            ) {
                Text("Buscar equipo", color = Color.White)
            }

            mensajeError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = Color.Red)
            }
        }
    }

}




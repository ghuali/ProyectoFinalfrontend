package Screen

import ViewModel.SessionManager
import ViewModel.SessionManager.currentUser
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import model.Evento
import model.EventoCreateRequest
import model.Juego
import model.User
import network.apiCrearEvento
import network.apiCrearTorneo
import network.getEventos
import network.getTodosLosJuegos

class AdminScreen : Screen {
    @Composable
    override fun Content() {
        val usuario = SessionManager.currentUser
        AdminPantalla(
            usuario = usuario,
            onLogout = {
                SessionManager.authToken = null
                SessionManager.currentUser = null
                SessionManager.clearSession()
            }
        )
    }

    @Composable
    fun AdminPantalla(
        usuario: User?,
        onLogout: () -> Unit
    ) {
        val navigator = LocalNavigator.current

        var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
        var juegos by remember { mutableStateOf<List<Juego>>(emptyList()) }

        // Estados para los dropdowns de selección
        var expandedEvento by remember { mutableStateOf(false) }
        var expandedJuego by remember { mutableStateOf(false) }
        var selectedEventoIndex by remember { mutableStateOf(-1) }
        var selectedJuegoIndex by remember { mutableStateOf(-1) }

        var nombreEvento by remember { mutableStateOf("") }
        var tipoEvento by remember { mutableStateOf("Anual") }
        var añoEvento by remember { mutableStateOf("") }

        var nombreTorneo by remember { mutableStateOf("") }
        var fechaInicio by remember { mutableStateOf("") }
        var fechaFin by remember { mutableStateOf("") }
        var ubicacion by remember { mutableStateOf("") }

        // Solo guardar el id como String para enviar al backend
        val idEvento = if (selectedEventoIndex >= 0) eventos[selectedEventoIndex].id_evento.toString() else ""
        val idJuego = if (selectedJuegoIndex >= 0) juegos[selectedJuegoIndex].id_juego.toString() else ""

        var expandedTipoEvento by remember { mutableStateOf(false) }
        var expandedUserMenu by remember { mutableStateOf(false) }

        var showDialogEvento by remember { mutableStateOf(false) }
        var showDialogTorneo by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            getEventos { listaEventos ->
                eventos = listaEventos
            }
            getTodosLosJuegos { listaJuegos ->
                juegos = listaJuegos
            }
        }

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
                        if (usuario != null) {
                            Box {
                                Text(
                                    usuario.nombre,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { expandedUserMenu = true }
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.clickable { expandedUserMenu = true }
                                )
                                DropdownMenu(
                                    expanded = expandedUserMenu,
                                    onDismissRequest = { expandedUserMenu = false }
                                ) {
                                    DropdownMenuItem(onClick = {
                                        expandedUserMenu = false
                                        navigator?.push(EditScreen())
                                    }) {
                                        Text("Editar perfil")
                                    }
                                    DropdownMenuItem(onClick = {
                                        expandedUserMenu = false
                                        navigator?.push(EquipoScreen())
                                    }) {
                                        Text("Equipo")
                                    }
                                    if (usuario.rol == "administrador") {
                                        DropdownMenuItem(onClick = {
                                            expandedUserMenu = false
                                            // Ya estamos aquí
                                        }) {
                                            Text("Administrar")
                                        }
                                    }
                                    DropdownMenuItem(onClick = {
                                        expandedUserMenu = false
                                        onLogout()
                                        navigator?.pop()
                                    }) {
                                        Text("Cerrar sesión")
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val scrollState = rememberScrollState()


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .weight(1f)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "ADMINISTRACIÓN",
                        fontSize = 36.sp,
                        color = Color.Yellow,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        "Crear Evento y Torneo",
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Crear Evento - Caja gris oscuro
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFD3D3D3))
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Administrador",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            // Crear Evento
                            Text(
                                text = "Crear Evento",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Text("Nombre del Evento", fontWeight = FontWeight.Bold, color = Color.Black)
                            TextField(
                                value = nombreEvento,
                                onValueChange = { nombreEvento = it },
                                placeholder = { Text("Introducir Nombre del Evento") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = Color.White
                                )
                            )

                            Text("Tipo de Evento", fontWeight = FontWeight.Bold, color = Color.Black)
                            TextField(
                                value = tipoEvento,
                                onValueChange = { tipoEvento = it },
                                placeholder = { Text("anual o mensual") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = Color.White
                                )
                            )

                            Text("Año del Evento", fontWeight = FontWeight.Bold, color = Color.Black)
                            TextField(
                                value = añoEvento,
                                onValueChange = { añoEvento = it },
                                placeholder = { Text("Ej: 2025") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = Color.White
                                )
                            )

                            Button(
                                onClick = {
                                    if (nombreEvento.isNotBlank() && añoEvento.length == 4) {
                                        showDialogEvento = true
                                    } else println("Complete correctamente los campos de evento")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            ) {
                                Text("Crear Evento")
                            }

                            Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray)

                            // Crear Torneo
                            Text(
                                text = "Crear Torneo",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Text("Nombre del Torneo", fontWeight = FontWeight.Bold, color = Color.Black)
                            TextField(
                                value = nombreTorneo,
                                onValueChange = { nombreTorneo = it },
                                placeholder = { Text("Nombre del Torneo") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = Color.White
                                )
                            )

                            Text("Fecha Inicio (YYYY-MM-DD)", fontWeight = FontWeight.Bold, color = Color.Black)
                            TextField(
                                value = fechaInicio,
                                onValueChange = { fechaInicio = it },
                                placeholder = { Text("2025-06-01") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = Color.White
                                )
                            )

                            Text("Fecha Fin (YYYY-MM-DD)", fontWeight = FontWeight.Bold, color = Color.Black)
                            TextField(
                                value = fechaFin,
                                onValueChange = { fechaFin = it },
                                placeholder = { Text("2025-06-10") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = Color.White
                                )
                            )

                            Text("Ubicación", fontWeight = FontWeight.Bold, color = Color.Black)
                            TextField(
                                value = ubicacion,
                                onValueChange = { ubicacion = it },
                                placeholder = { Text("Lugar del torneo") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = Color.White
                                )
                            )

                            Text("Seleccionar Evento", fontWeight = FontWeight.Bold, color = Color.Black)
                            DropdownMenuBox(
                                items = eventos.map { it.nombre },
                                selectedIndex = selectedEventoIndex,
                                onItemSelected = { selectedEventoIndex = it }
                            )

                            Text("Seleccionar Juego", fontWeight = FontWeight.Bold, color = Color.Black)
                            DropdownMenuBox(
                                items = juegos.map { it.nombre },
                                selectedIndex = selectedJuegoIndex,
                                onItemSelected = { selectedJuegoIndex = it }
                            )

                            Button(
                                onClick = {
                                    if (nombreTorneo.isNotBlank() &&
                                        fechaInicio.isNotBlank() &&
                                        fechaFin.isNotBlank() &&
                                        selectedEventoIndex >= 0 &&
                                        selectedJuegoIndex >= 0
                                    ) {
                                        showDialogTorneo = true
                                    } else println("Complete correctamente los campos de torneo")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            ) {
                                Text("Crear Torneo")
                            }
                        }
                    }

                }
            }

            // Botón Volver flotante abajo a la derecha
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
        // Confirmación Crear Evento
        if (showDialogEvento) {
            AlertDialog(
                onDismissRequest = { showDialogEvento = false },
                title = { Text("Confirmar creación") },
                text = { Text("¿Deseas crear este evento?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialogEvento = false
                            val añoEventoInt = añoEvento.toIntOrNull()
                            if (añoEventoInt != null) {
                                apiCrearEvento(
                                    evento = EventoCreateRequest(
                                        nombre = nombreEvento,
                                        tipo = tipoEvento.lowercase(),
                                        año = añoEventoInt
                                    ),
                                    token = SessionManager.authToken,
                                    onSuccess = { mensaje -> println("Éxito Crear Evento: $mensaje") },
                                    onError = { error -> println("Error Crear Evento: $error") }
                                )
                            } else println("El año debe ser un número válido")
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialogEvento = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Confirmación Crear Torneo
        if (showDialogTorneo) {
            AlertDialog(
                onDismissRequest = { showDialogTorneo = false },
                title = { Text("Confirmar creación") },
                text = { Text("¿Deseas crear este torneo?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialogTorneo = false
                            apiCrearTorneo(
                                nombre = nombreTorneo,
                                fechaInicio = fechaInicio,
                                fechaFin = fechaFin,
                                ubicacion = ubicacion,
                                idEvento = eventos[selectedEventoIndex].id_evento,
                                idJuego = juegos[selectedJuegoIndex].id_juego,
                                token = SessionManager.authToken,
                                onSuccess = { mensaje -> println("Éxito Crear Torneo: $mensaje") },
                                onError = { error -> println("Error Crear Torneo: $error") }
                            )
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialogTorneo = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

    }
    @Composable
    fun DropdownMenuBox(
        items: List<String>,
        selectedIndex: Int,
        onItemSelected: (Int) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(Color.White, RoundedCornerShape(4.dp))
                .clickable { expanded = true }
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (selectedIndex >= 0) items[selectedIndex] else "Selecciona una opción",
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expandir"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEachIndexed { index, label ->
                    DropdownMenuItem(onClick = {
                        onItemSelected(index)
                        expanded = false
                    }) {
                        Text(label)
                    }
                }
            }
        }
    }

}

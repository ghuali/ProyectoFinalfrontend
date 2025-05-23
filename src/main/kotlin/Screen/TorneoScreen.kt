package Screen

import ViewModel.SessionManager
import ViewModel.SessionManager.currentUser
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import apiEntrarTorneoJugadorIndividual
import apiSalirTorneoJugadorIndividual
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import model.Clasificacion
import model.Evento
import model.Torneo
import model.User
import network.*

class TorneoScreen : Screen {
    @Composable
    override fun Content() {
        val usuario = SessionManager.currentUser

        torneoPantalla(
            usuario = usuario,
            onLoginSuccess = { user ->
                // Actualizamos SessionManager aquí
                SessionManager.authToken = user.token
                SessionManager.currentUser = user
            },
            onLogout = {
                SessionManager.authToken = null
                SessionManager.currentUser = null
            }
        )
    }

    @Composable
    fun torneoPantalla(
        usuario: User?,
        onLoginSuccess: (User) -> Unit,
        onLogout: () -> Unit
    ){
        val navigator = LocalNavigator.current

        var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
        var torneos by remember { mutableStateOf<List<Torneo>>(emptyList()) }
        var clasificacion by remember { mutableStateOf<List<Clasificacion>>(emptyList()) }

        var eventoSeleccionado by remember { mutableStateOf<Evento?>(null) }
        var torneoSeleccionado by remember { mutableStateOf<Torneo?>(null) }

        var showSignInDialog by remember { mutableStateOf(false) }
        var showSignUpDialog by remember { mutableStateOf(false) }

        // Cargar eventos al iniciar
        LaunchedEffect(Unit) {
            getEventos { eventos = it }
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
                                        navigator?.push(EditScreen(token =SessionManager.authToken ?: ""))
                                    }) {
                                        Text("Editar perfil")
                                    }

                                    DropdownMenuItem(onClick = {
                                        expanded = false
                                        onLogout()
                                    }) {
                                        Text("Cerrar sesión")
                                    }
                                }
                            }

                        } else {

                            Button(
                                onClick = { showSignInDialog = true },
                                colors = ButtonDefaults.buttonColors(Color.Black),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Log in", color = Color.White)
                            }

                            Button(
                                onClick = { showSignUpDialog = true },
                                colors = ButtonDefaults.buttonColors(Color.Black)
                            ) {
                                Text("Sign up", color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    torneoSeleccionado != null -> {
                        // Mostrar Clasificación
                        Text("Clasificación - ${torneoSeleccionado!!.nombre}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    if (usuario != null) {
                                        apiEntrarTorneoJugadorIndividual(
                                            torneoId = torneoSeleccionado!!.id_torneo,
                                            token = SessionManager.authToken ?: "",
                                            onSuccess = {
                                                getClasificacionPorTorneo(torneoSeleccionado!!.id_torneo) {
                                                    clasificacion = it
                                                }
                                            },
                                            onError = {   /* Mostrar error */ }
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green)
                            ) {
                                Text("Entrar", color = Color.White)
                            }

                            Button(
                                onClick = {
                                    if (usuario != null) {
                                        apiSalirTorneoJugadorIndividual(
                                            torneoId = torneoSeleccionado!!.id_torneo,
                                            token = SessionManager.authToken ?: "",
                                            onSuccess = { getClasificacionPorTorneo(torneoSeleccionado!!.id_torneo) {
                                                clasificacion = it
                                            } },
                                            onError = { error -> /* Mostrar error */ }
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                            ) {
                                Text("Salir", color = Color.White)
                            }
                        }

                        if (clasificacion.isEmpty()) {
                            Text("Cargando clasificación...", color = Color.LightGray)
                        } else {
                            LazyColumn {
                                items(clasificacion) { c ->
                                    Card(
                                        backgroundColor = Color.DarkGray,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("${c.posicion}", color = Color.Yellow, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.width(30.dp))
                                            Spacer(Modifier.width(12.dp))
                                            Text(c.usuario ?: c.equipo ?: "N/A", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 18.sp)
                                            Spacer(Modifier.weight(1f))
                                            Text("${c.puntos} pts", color = Color.LightGray, fontSize = 16.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    eventoSeleccionado != null -> {
                        // Mostrar lista de torneos del evento seleccionado
                        Text("Torneos de: ${eventoSeleccionado!!.nombre}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))

                        if (torneos.isEmpty()) {
                            Text("Cargando torneos...", color = Color.LightGray)
                        } else {
                            LazyColumn {
                                items(torneos) { torneo ->
                                    Card(
                                        backgroundColor = Color.DarkGray,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp)
                                            .clickable {
                                                torneoSeleccionado = torneo
                                                clasificacion = emptyList()
                                                getClasificacionPorTorneo(torneo.id_torneo) {
                                                    clasificacion = it
                                                }
                                            }
                                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(torneo.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Yellow)
                                            Text("Desde: ${torneo.fecha_inicio}  Hasta: ${torneo.fecha_fin}", fontSize = 14.sp, color = Color.White)
                                            Text("Ubicación: ${torneo.ubicacion}", fontSize = 14.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        // Mostrar lista de eventos
                        Text("Eventos", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Yellow)
                        Spacer(modifier = Modifier.height(12.dp))

                        if (eventos.isEmpty()) {
                            Text("Cargando eventos...", color = Color.LightGray)
                        } else {
                            LazyColumn {
                                items(eventos) { evento ->
                                    Card(
                                        backgroundColor = Color.DarkGray,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp)
                                            .clickable {
                                                eventoSeleccionado = evento
                                                torneos = emptyList()
                                                // Carga torneos filtrados por evento, para eso necesitamos el id_juego
                                                // Aquí asumimos que el evento tiene un id_juego, o podemos hacer una llamada separada
                                                // Para simplificar, usaremos el id_juego 1 (puedes adaptar esto)
                                                getTorneosPorJuego(evento.idEvento) { torneosResponse ->
                                                    // Filtrar torneos que pertenezcan al evento seleccionado
                                                    torneos = torneosResponse.filter { it.id_evento == evento.idEvento }
                                                }
                                            }
                                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(evento.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Yellow)
                                            Text("Tipo: ${evento.tipo}", fontSize = 14.sp, color = Color.White)
                                            Text("Año: ${evento.anio}", fontSize = 14.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Botón Volver general
            Button(
                onClick = { navigator?.pop() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFD32F2F))
            ) {
                Text("Salir", fontSize = 20.sp, color = Color.White)
            }
        }

        if (showSignInDialog) {
            SignInDialog(
                onDismiss = { showSignInDialog = false },
                onSuccess = { user ->
                    showSignInDialog = false
                    onLoginSuccess(user)
                }
            )
        }

        if (showSignUpDialog) {
            SignUpDialog(
                onDismiss = { showSignUpDialog = false },
                onSignUpSuccess = { user ->
                    showSignUpDialog = false
                    onLoginSuccess(user)
                }
            )
        }
    }

    @Composable
    fun SignUpDialog(
        onDismiss: () -> Unit,
        onSignUpSuccess: (User) -> Unit
    ) {
        var nombre by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFD3D3D3))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Registrarse",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = Color.Black
                    )

                    Text("Nombre", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    TextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        placeholder = { Text("Introducir Nombre") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                    )

                    Text("Email", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Introducir Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                    )

                    Text("Contraseña", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Introducir Contraseña") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            apiRegister(nombre, email, password,
                                onSuccessResponse = { user ->
                                    onSignUpSuccess(user)
                                    onDismiss()
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Registrarse", color = Color.Yellow, fontWeight = FontWeight.Bold)
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(Color.Gray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar", color = Color.White)
                    }
                }
            }
        }
    }

    @Composable
    fun SignInDialog(
        onDismiss: () -> Unit,
        onSuccess: (User) -> Unit
    ) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFD3D3D3))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = Color.Black
                    )

                    Text("Email", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Introducir Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                    )

                    Text("Contraseña", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Introducir Contraseña") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            apiLogIn(
                                email, password,
                                callback = { user ->
                                    onSuccess(user)
                                    onDismiss()
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Iniciar Sesión", color = Color.Yellow, fontWeight = FontWeight.Bold)
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(Color.Gray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar", color = Color.White)
                    }
                }
            }
        }
    }
}

package Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import model.Equipo
import model.EquipoResumen
import model.Juego
import model.User
import network.apiLogIn
import network.apiRegister
import network.getEquiposPorJuego
import network.getJuegosPorEquipo
import ViewModel.SessionManager
import ViewModel.SessionManager.currentUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.EquipoConCodigo
import network.apiObtenerEquipoPorFundador
import network.getJugadoresPorJuego
import network.salirJuegoEquipo
import network.salirJuegoIndividual
import network.unirseJuegoEquipo
import network.unirseJuegoIndividual
import utils.LoginDialog
import utils.RegisterDialog


class WelcomeScreen : Screen {

    @Composable
    override fun Content() {
        val usuario = SessionManager.currentUser

        pantallaEquipo(
            usuario = usuario,
            onLoginSuccess = { user ->
                println("Asignando token: ${user.token}")
                // Actualizamos SessionManager aquí
                SessionManager.authToken = user.token
                SessionManager.currentUser = user
                SessionManager.saveSession()
            },
            onLogout = {
                SessionManager.authToken = null
                SessionManager.currentUser = null
                SessionManager.clearSession()
            }
        )

    }

    @Composable
    fun pantallaEquipo(
        usuario: User?,
        onLoginSuccess: (User) -> Unit,
        onLogout: () -> Unit
    ) {
        val navigator = LocalNavigator.current

        var showSignInDialog by remember { mutableStateOf(false) }
        var showSignUpDialog by remember { mutableStateOf(false) }


        var juegos by remember { mutableStateOf<List<Juego>>(emptyList()) }
        var selectedGameIndex by remember { mutableStateOf(0) }

        var equipos by remember { mutableStateOf(emptyList<EquipoResumen>()) }

        var miEquipo by remember { mutableStateOf<EquipoConCodigo?>(null) }

        LaunchedEffect(usuario?.id) {
            usuario?.id?.let { id ->
                apiObtenerEquipoPorFundador(
                    idUsuario = id,
                    onSuccess = { equipo ->
                        miEquipo = equipo
                        println("Equipo del usuario: $equipo")
                    },
                    onError = { error ->
                        println("Error al obtener equipo: $error")
                    }
                )
            }
        }

        LaunchedEffect(Unit) {
            getJuegosPorEquipo { juegosApi ->
                juegos = juegosApi
                if (juegos.isNotEmpty()) {
                    selectedGameIndex = 0
                    getEquiposPorJuego(juegos[0].id_juego) { equiposApi ->
                        equipos = equiposApi
                    }
                }
            }
        }

        LaunchedEffect(selectedGameIndex, juegos) {
            val juegoSeleccionado = juegos.getOrNull(selectedGameIndex)
            if (juegoSeleccionado != null) {
                getEquiposPorJuego(juegoSeleccionado.id_juego) { equiposApi ->
                    println("Equipos cargados para juego ${juegoSeleccionado.nombre}: $equiposApi")
                    equipos = equiposApi
                }
            } else {
                equipos = emptyList()
            }
        }

        val selectedGame = juegos.getOrNull(selectedGameIndex)

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
                                        println("Token antes de abrir EditScreen: ${SessionManager.authToken}")
                                        navigator?.push(EditScreen())
                                    }) {
                                        Text("Editar perfil")
                                    }

                                    DropdownMenuItem(onClick = {
                                        expanded = false
                                        navigator?.push(EquipoScreen())
                                    }) {
                                        Text("Equipo")
                                    }

                                    if (usuario.rol == "administrador") {
                                        DropdownMenuItem(onClick = {
                                            expanded = false
                                            navigator?.push(AdminScreen())  // O la pantalla que administre
                                        }) {
                                            Text("Administrar")
                                        }
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

                // Selector de juegos
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFF00))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        if (selectedGameIndex > 0) selectedGameIndex--
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Anterior")
                    }

                    Box(
                        modifier = Modifier
                            .border(2.dp, Color.Black)
                            .background(Color(0xFFFFCC80))
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = selectedGame?.nombre ?: "No hay juegos",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    IconButton(onClick = {
                        if (selectedGameIndex < juegos.size - 1) selectedGameIndex++
                    }) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = "Siguiente")
                    }
                }

                // Título del juego
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Título del juego a la izquierda
                    Text(
                        text = selectedGame?.nombre ?: "",
                        fontSize = 36.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    // Botones a la derecha
                    Row {
                        if (miEquipo != null && selectedGame != null) {
                            Button(
                                onClick = {
                                    val token = SessionManager.authToken
                                    if (token != null) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                unirseJuegoEquipo(selectedGame.id_juego, miEquipo!!.id_equipo, token) {
                                                    println("Equipo se ha unido al juego")
                                                    getEquiposPorJuego(selectedGame.id_juego) { equiposApi ->
                                                        equipos = equiposApi
                                                        println("Equipos actualizados: $equipos")
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                println("Error al unirse equipo: ${e.message}")
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green),
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                            ) {
                                Text("Unirse")
                            }

                            Button(
                                onClick = {
                                    val token = SessionManager.authToken
                                    if (token != null) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                salirJuegoEquipo(selectedGame.id_juego, miEquipo!!.id_equipo, token) {
                                                    println("Equipo ha salido del juego")
                                                    getEquiposPorJuego(selectedGame.id_juego) { equiposApi ->
                                                        equipos = equiposApi
                                                        println("Equipos actualizados: $equipos")
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                println("Error al salir equipo: ${e.message}")
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                            ) {
                                Text("Salir")
                            }
                        } else {
                            Text("No eres fundador de ningún equipo")
                        }
                    }
                }

                // Tabla: cabecera
                TableHeader()

                // Tabla: filas
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    val totalFilas = 14
                    val equiposRellenados = equipos + List(totalFilas - equipos.size) {
                        EquipoResumen(
                            id_equipo = -1,
                            nombre = "-",
                            victorias = 0,
                            derrotas = 0
                        )
                    }

                    itemsIndexed(equiposRellenados) { index, equipo ->
                        TableRow(
                            nombre = equipo.nombre,
                            victorias = equipo.victorias.toString(),
                            derrotas = equipo.derrotas.toString(),
                            index = index
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Botón Volver
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

        // Diálogos
        if (showSignInDialog) {
            LoginDialog(
                onDismiss = { showSignInDialog = false },
                onLoginSuccess = { user ->
                    onLoginSuccess(user)
                    showSignInDialog = false
                }
            )
        }

        if (showSignUpDialog) {
            RegisterDialog(
                onDismiss = { showSignUpDialog = false },
                onSignUpSuccess = { user ->
                    onLoginSuccess(user)
                    showSignUpDialog = false
                }
            )

        }
    }
}

    @Composable
    private fun TableHeader() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF424242))
                .border(width = 1.dp, color = Color.Black)
                .padding(vertical = 12.dp)
        ) {
            listOf("Equipo", "Victorias", "Derrotas").forEach { title ->
                Text(
                    text = title,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }

    @Composable
    private fun TableRow(
        nombre: String,
        victorias: String,
        derrotas: String,
        index: Int
    ) {
        val backgroundColor = if (index % 2 == 0) Color(0xFF2E2E2E) else Color(0xFF424242)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(vertical = 12.dp)
        ) {
            listOf(nombre, victorias, derrotas).forEach { content ->
                Text(
                    text = content,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }


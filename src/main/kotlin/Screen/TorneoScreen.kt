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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import apiEntrarTorneoJugadorIndividual
import apiInscribirEquipoEnTorneo
import apiSalirTorneoEquipo
import apiSalirTorneoJugadorIndividual
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.delay
import model.Clasificacion
import model.EquipoConCodigo
import model.Evento
import model.Juego
import model.Torneo
import model.TorneoCompleto
import model.TorneoConTipo
import model.User
import network.*
import utils.LoginDialog
import utils.RegisterDialog

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
    fun torneoPantalla(
        usuario: User?,
        onLoginSuccess: (User) -> Unit,
        onLogout: () -> Unit
    ){
        val navigator = LocalNavigator.current

        var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
        var juegos by remember { mutableStateOf<List<Juego>>(emptyList()) }
        var torneosConTipo by remember { mutableStateOf<List<TorneoConTipo>>(emptyList()) }
        var torneos by remember { mutableStateOf<List<TorneoCompleto>>(emptyList()) }
        var clasificacion by remember { mutableStateOf<List<Clasificacion>>(emptyList()) }
        var equipoObtenido by remember { mutableStateOf<EquipoConCodigo?>(null) }
        var cargandoEquipo by remember { mutableStateOf(false) }

        println("Juegos recibidos:")
        juegos.forEach {
            println("Juego: ${it.nombre}, es_individual: ${it.es_individual}")
        }

        var eventoSeleccionado by remember { mutableStateOf<Evento?>(null) }
        var torneoSeleccionado by remember { mutableStateOf<TorneoConTipo?>(null) }

        var isLoading by remember { mutableStateOf(false) }
        var errorMsg by remember { mutableStateOf<String?>(null) }
        var successMsg by remember { mutableStateOf<String?>(null) }


        var showSignInDialog by remember { mutableStateOf(false) }
        var showSignUpDialog by remember { mutableStateOf(false) }

        fun inscribirIndividual(torneoId: Int) {
            isLoading = true
            errorMsg = null
            successMsg = null

            apiEntrarTorneoJugadorIndividual(
                torneoId = torneoId,
                token = SessionManager.authToken ?: "",
                onSuccess = {
                    isLoading = false
                    successMsg = "¡Inscripción exitosa!"
                    getClasificacionPorTorneo(torneoId) {
                        clasificacion = it
                    }
                },
                onError = { error ->
                    isLoading = false
                    errorMsg = error
                }
            )
        }

        fun inscribirEquipo(torneoId: Int, equipoId: Int) {
            isLoading = true
            errorMsg = null
            successMsg = null

            apiInscribirEquipoEnTorneo(
                torneoId = torneoId,
                equipoId = equipoId,
                token = SessionManager.authToken ?: "",
                onSuccess = {
                    isLoading = false
                    successMsg = "¡Equipo inscrito exitosamente!"
                    getClasificacionPorTorneo(torneoId) {
                        clasificacion = it
                    }
                },
                onError = { error ->
                    isLoading = false
                    errorMsg = error
                }
            )
        }

        fun salirIndividual(torneoId: Int) {
            isLoading = true
            errorMsg = null
            successMsg = null

            apiSalirTorneoJugadorIndividual(
                torneoId = torneoId,
                token = SessionManager.authToken ?: "",
                onSuccess = {
                    isLoading = false
                    successMsg = "Saliste del torneo correctamente"
                    clasificacion = emptyList()
                    torneoSeleccionado = null
                },
                onError = { error ->
                    isLoading = false
                    errorMsg = error
                }
            )
        }

        fun salirEquipo(torneoId: Int, equipoId: Int) {
            isLoading = true
            errorMsg = null
            successMsg = null

            apiSalirTorneoEquipo(
                torneoId = torneoId,
                equipoId = equipoId,
                token = SessionManager.authToken ?: "",
                onSuccess = {
                    isLoading = false
                    successMsg = "El equipo salió del torneo correctamente"
                    clasificacion = emptyList()
                    torneoSeleccionado = null
                },
                onError = { error ->
                    isLoading = false
                    errorMsg = error
                }
            )
        }

        // Cargar eventos al iniciar
        LaunchedEffect(Unit) {
            getEventos { eventos = it }
        }

        LaunchedEffect(torneoSeleccionado) {
            while (torneoSeleccionado != null) {
                getClasificacionPorTorneo(torneoSeleccionado!!.torneo.id_torneo) {
                    clasificacion = it
                }
                delay(5000) // Actualiza cada 5 segundos
            }
        }
        LaunchedEffect(usuario?.id, torneoSeleccionado?.tipo) {
            if (usuario != null && torneoSeleccionado?.tipo == "equipo") {
                cargandoEquipo = true
                apiObtenerEquipoPorFundador(
                    idUsuario = usuario.id,
                    onSuccess = { equipo ->
                        equipoObtenido = equipo
                        cargandoEquipo = false
                    },
                    onError = {
                        equipoObtenido = null
                        cargandoEquipo = false
                    }
                )
            }
        }



        LaunchedEffect(eventoSeleccionado) {
            eventoSeleccionado?.let { evento ->
                getTodosLosJuegos { juegosObtenidos ->
                    juegos = juegosObtenidos
                    getTorneosPorEvento(evento.id_evento) { torneosObtenidos ->
                        torneosConTipo = torneosObtenidos.map { torneo ->
                            val juego = juegos.find { it.id_juego == torneo.id_juego }
                            val tipo = if (juego?.es_individual == true) "individual" else "equipo"
                            println("Torneo: ${torneo.nombre}, Tipo: $tipo")
                            TorneoConTipo(torneo = torneo, tipo = tipo)
                        }
                    }
                }
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
                        if (usuario != null && !SessionManager.authToken.isNullOrEmpty()) {
                            var expanded by remember { mutableStateOf(false) }

                            Box {
                                Text(
                                    usuario.nombre,
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

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    torneoSeleccionado != null -> {
                        // Mostrar Clasificación
                        Text("Clasificación - ${torneoSeleccionado!!.torneo.nombre}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tipo de torneo: ${torneoSeleccionado?.tipo ?: "Ninguno"}",
                            color = Color.White
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            if (torneoSeleccionado!!.tipo == "individual") {
                                Button(
                                    onClick = {
                                        if (usuario != null) {
                                            inscribirIndividual(torneoSeleccionado!!.torneo.id_torneo)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green)
                                ) {
                                    Text("Entrar", color = Color.White)
                                }

                                Button(
                                    onClick = {
                                        if (usuario != null) {
                                            salirIndividual(torneoSeleccionado!!.torneo.id_torneo)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                                ) {
                                    Text("Salir", color = Color.White)
                                }

                            } else if (torneoSeleccionado!!.tipo == "equipo") {
                                when {
                                    cargandoEquipo -> {
                                        CircularProgressIndicator(color = Color.White)
                                    }
                                    equipoObtenido != null -> {
                                        Button(
                                            onClick = {
                                                inscribirEquipo(torneoSeleccionado!!.torneo.id_torneo, equipoObtenido!!.id_equipo)
                                            },
                                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue)
                                        ) {
                                            Text("Entrar como equipo", color = Color.White)
                                        }

                                        Button(
                                            onClick = {
                                                salirEquipo(torneoSeleccionado!!.torneo.id_torneo, equipoObtenido!!.id_equipo)
                                            },
                                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                                        ) {
                                            Text("Salir del torneo", color = Color.White)
                                        }
                                    }
                                    else -> {
                                        Text(
                                            text = "No eres fundador de ningún equipo",
                                            color = Color.White,
                                            fontStyle = FontStyle.Italic,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (clasificacion.isEmpty()) {
                            Text(
                                text = "No hay jugadores en la clasificación para este torneo.",
                                color = Color.LightGray,
                                fontStyle = FontStyle.Italic,
                                modifier = Modifier.padding(16.dp)
                            )
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

                        if (torneosConTipo.isEmpty()) {
                            Text("Cargando torneos...", color = Color.LightGray)
                        } else {
                            LazyColumn {
                                items(torneosConTipo) { torneoConTipo ->  // <-- aquí la lista con tipo
                                    Card(
                                        backgroundColor = Color.DarkGray,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp)
                                            .clickable {
                                                torneoSeleccionado = torneoConTipo  // <-- asignamos TorneoConTipo
                                                clasificacion = emptyList()
                                                getClasificacionPorTorneo(torneoConTipo.torneo.id_torneo) {
                                                    clasificacion = it
                                                }
                                            }
                                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(torneoConTipo.torneo.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Yellow)
                                            Text("Desde: ${torneoConTipo.torneo.fecha_inicio}  Hasta: ${torneoConTipo.torneo.fecha_fin}", fontSize = 14.sp, color = Color.White)
                                            Text("Ubicación: ${torneoConTipo.torneo.ubicacion}", fontSize = 14.sp, color = Color.White)
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
                                            }
                                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(evento.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Yellow)
                                            Text("Tipo: ${evento.tipo}", fontSize = 14.sp, color = Color.White)
                                            Text("Año: ${evento.año}", fontSize = 14.sp, color = Color.White)
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
            LoginDialog(
                onDismiss = { showSignInDialog = false },
                onLoginSuccess = { user ->
                    showSignInDialog = false
                    onLoginSuccess(user)
                }
            )
        }

        if (showSignUpDialog) {
            RegisterDialog(
                onDismiss = { showSignUpDialog = false },
                onSignUpSuccess = { user ->
                    showSignUpDialog = false
                    onLoginSuccess(user)
                }
            )
        }
    }
}



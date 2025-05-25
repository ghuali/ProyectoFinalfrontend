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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import model.EquipoConCodigo
import model.User
import model.Usuario
import network.apiCrearEquipo
import network.apiObtenerEquipoDelUsuario
import network.apiObtenerEquipoPorCodigo
import network.apiObtenerMiembrosEquipo
import network.apiSalirDelEquipo
import network.apiUnirseAEquipoPorCodigo


class EquipoScreen: Screen {

    @Composable
    override fun Content() {
        val usuario = SessionManager.currentUser

        EquiposPantalla(
            usuario = usuario,
            onLogout = {
                SessionManager.authToken = null
                SessionManager.currentUser = null
                SessionManager.clearSession()
            }
        )
    }

    @Composable
    fun EquiposPantalla(
        usuario: User?,
        onLogout: () -> Unit
    ) {
        val navigator = LocalNavigator.current
        val equipo = remember { mutableStateOf<EquipoConCodigo?>(null) }
        val mensajeError = remember { mutableStateOf<String?>(null) }
        val cargando = remember { mutableStateOf(false) }
        val mensajeAccion = remember { mutableStateOf<String?>(null) }
        val cargandoAccion = remember { mutableStateOf(false) }
        var mostrarDialogoSalir by remember { mutableStateOf(false) }

        LaunchedEffect(usuario) {
            if (usuario != null) {
                mensajeError.value = null
                cargando.value = true
                apiObtenerEquipoDelUsuario(
                    onSuccess = { equipoUsuario ->
                        equipo.value = equipoUsuario
                        cargando.value = false
                    },
                    onError = { errorMsg ->
                        mensajeError.value = errorMsg
                        cargando.value = false
                    }
                )
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
                                    println("Token antes de abrir EditScreen: ${SessionManager.authToken}")
                                    expanded = false
                                    navigator?.push(EditScreen())
                                }) {
                                    Text("Editar perfil")
                                }

                                if (usuario?.rol == "administrador") {
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
                    when {
                        cargando.value -> {
                            Text("Cargando datos...", color = Color.White, modifier = Modifier.padding(16.dp))
                        }
                        mensajeError.value != null -> {
                            Text("Error: ${mensajeError.value}", color = Color.Red, modifier = Modifier.padding(16.dp))
                            crearEquipoBloque(equipo)
                        }
                        equipo.value != null -> {
                            mostrarEquipoBloque(equipo.value!!)

                            mensajeAccion.value?.let { msg ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = msg,
                                    color = if (msg.contains("error", true)) Color.Red else Color.Green,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }
                        }
                        else -> {
                            crearEquipoBloque(equipo)
                        }
                    }
                }
            }

            // Botón "Salir del equipo" abajo a la izquierda
            if (equipo.value != null) {
                Button(
                    onClick = { mostrarDialogoSalir = true },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text("Salir del equipo", color = Color.White, fontSize = 18.sp)
                }
            }

            // Diálogo de confirmación para salir del equipo
            if (mostrarDialogoSalir) {
                Dialog(onDismissRequest = { mostrarDialogoSalir = false }) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFD3D3D3), shape = RoundedCornerShape(12.dp))
                            .padding(24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Salir del equipo",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Text(
                                "¿Seguro que quieres salir del equipo \"${equipo.value?.nombre}\"?",
                                fontSize = 18.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            Button(
                                onClick = {
                                    mostrarDialogoSalir = false
                                    mensajeAccion.value = null
                                    cargandoAccion.value = true
                                    apiSalirDelEquipo(
                                        onSuccess = { msg ->
                                            mensajeAccion.value = msg
                                            equipo.value = null
                                            cargandoAccion.value = false
                                        },
                                        onError = { error ->
                                            mensajeAccion.value = error
                                            cargandoAccion.value = false
                                        }
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Salir", color = Color.Yellow, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { mostrarDialogoSalir = false },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Cancelar", color = Color.White)
                            }
                        }
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
    fun crearEquipoBloque(
        equipo: MutableState<EquipoConCodigo?>,
    ) {
        var mostrarDialogo by remember { mutableStateOf(false) }
        var equipoEncontrado by remember { mutableStateOf<EquipoConCodigo?>(null) }
        var mostrarDialogoUnirse by remember { mutableStateOf(false) }
        var codigoNoEncontrado by remember { mutableStateOf<String?>(null) }
        var mensajeUnirse by remember { mutableStateOf<String?>(null) }


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
                            apiObtenerEquipoPorCodigo(
                                codigo = codigoIngresado,
                                onSuccess = { equipo ->
                                    equipoEncontrado = equipo
                                    mostrarDialogoUnirse = true
                                    codigoNoEncontrado = null
                                    mensajeUnirse = null
                                },
                                onError = { error ->
                                    codigoNoEncontrado = error
                                }
                            )
                        },
                        mensajeError = codigoNoEncontrado
                    )

                }
                if (mostrarDialogoUnirse && equipoEncontrado != null) {
                    DialogUnirseEquipo(
                        equipoEncontrado = equipoEncontrado!!,
                        onConfirmar = {
                            apiUnirseAEquipoPorCodigo(
                                codigo = equipoEncontrado!!.codigo,
                                onSuccess = { mensaje ->
                                    equipo.value = equipoEncontrado
                                    mensajeUnirse = mensaje
                                },
                                onError = { error ->
                                    mensajeUnirse = error
                                }
                            )
                            mostrarDialogoUnirse = false
                        },
                        onCancelar = { mostrarDialogoUnirse = false }
                    )
                }

                mensajeUnirse?.let { msg ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = msg,
                        color = if (msg.contains("error", true) || msg.contains(
                                "no válido",
                                true
                            )
                        ) Color.Red else Color.Green
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
                    apiCrearEquipo(
                        nombreEquipo,
                        onSuccess = { equipoCreado ->
                            equipo.value = equipoCreado
                        },
                        onError = { error ->
                            println("Error creando equipo: $error")
                        }
                    )
                }
            )
        }
    }


    @Composable
    fun mostrarEquipoBloque(equipo: EquipoConCodigo) {
        val miembros = remember { mutableStateListOf<Usuario>() }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(equipo.id_equipo) {
            apiObtenerMiembrosEquipo(
                equipo.id_equipo,
                onSuccess = {
                    miembros.clear()
                    miembros.addAll(it)
                },
                onError = {
                    errorMessage = it
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                "Tu equipo: ${equipo.nombre}",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.DarkGray,
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nombre: ${equipo.nombre}", color = Color.White)
                    Text("Código: ${equipo.codigo}", color = Color.White)
                    Text("Victorias: ${equipo.victorias}", color = Color.White)
                    Text("Derrotas: ${equipo.derrotas}", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (miembros.isNotEmpty()) {
                MostrarMiembrosEquipo(miembros)
            } else {
                Text("No hay miembros en el equipo aún.", color = Color.Gray)
            }

            errorMessage?.let {
                Text("Error: $it", color = Color.Red)
            }
        }
    }


    @Composable
    fun MostrarMiembrosEquipo(miembros: List<Usuario>) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = "Miembros del equipo (${miembros.size}):",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            miembros.forEach { miembro ->
                Text(
                    text = miembro.nombre,
                    fontSize = 16.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }

    @Composable
    fun CrearEquipoDialog(
        onDismiss: () -> Unit,
        onCrearEquipo: (String) -> Unit
    ) {
        var nombreEquipo by remember { mutableStateOf("") }

        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFD3D3D3), shape = RoundedCornerShape(12.dp))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Crear nuevo equipo",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text("Nombre del equipo", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    TextField(
                        value = nombreEquipo,
                        onValueChange = { nombreEquipo = it },
                        placeholder = { Text("Introduce el nombre") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (nombreEquipo.isNotBlank()) {
                                onCrearEquipo(nombreEquipo.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Crear", color = Color.Yellow, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancelar", color = Color.White)
                    }
                }
            }
        }
    }

    @Composable
    fun DialogUnirseEquipo(
        equipoEncontrado: EquipoConCodigo,
        onConfirmar: () -> Unit,
        onCancelar: () -> Unit
    ) {
        Dialog(onDismissRequest = onCancelar) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFD3D3D3), shape = RoundedCornerShape(12.dp))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Unirse al equipo",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        "¿Quieres unirte al equipo \"${equipoEncontrado.nombre}\"?",
                        fontSize = 18.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Button(
                        onClick = onConfirmar,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Unirse", color = Color.Yellow, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onCancelar,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancelar", color = Color.White)
                    }
                }
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
                color = Color(0xFFFFEB3B), // Amarillo para destacar sobre negro
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = { Text("Introduce el código", color = Color(0xFFFFEB3B)) }, // Label amarillo
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    focusedBorderColor = Color(0xFFFFEB3B),
                    unfocusedBorderColor = Color(0xFFBBBBBB),
                    cursorColor = Color(0xFFFFEB3B),
                    focusedLabelColor = Color(0xFFFFEB3B),
                    unfocusedLabelColor = Color(0xFFBBBBBB),
                    placeholderColor = Color(0xFF888888)
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if (codigo.isNotBlank()) {
                        onBuscarEquipo(codigo.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFEB3B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Buscar equipo",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            mensajeError?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    it,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}



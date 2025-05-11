//package Screen
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import cafe.adriel.voyager.core.screen.Screen
//import cafe.adriel.voyager.navigator.LocalNavigator
//import network.apiLogIn
//
//
//class LoginScreen : Screen {
//    @Composable
//    override fun Content() {
//        val navigator = LocalNavigator.current
//        var newUsername by remember { mutableStateOf("") }
//        var newPassword by remember { mutableStateOf("") }
//        var errorMessage by remember { mutableStateOf<String?>(null) }
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(color = Color(0xFFFFFFFF))
//        ) {
//
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(80.dp)
//                    .background(Color(0xFF1976D2)),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = "Nombre de Empresa",
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.White
//                )
//            }
//
//            Column(
//                modifier = Modifier
//                    .padding(16.dp)
//                    .padding(vertical = 40.dp),
//                verticalArrangement = Arrangement.Top,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text("Introducir usuario", fontSize = 20.sp)
//                OutlinedTextField(
//                    value = newUsername,
//                    onValueChange = { newUsername = it },
//                    label = { Text("Nombre del Usuario") },
//                    modifier = Modifier.width(500.dp)
//                )
//                Text("Introducir contraseña", fontSize = 20.sp)
//                OutlinedTextField(
//                    value = newPassword,
//                    onValueChange = { newPassword = it },
//                    label = { Text("Contraseña") },
//                    modifier = Modifier.width(500.dp)
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//
//                    Button(
//                        onClick = {
//                            if (newUsername.isNotEmpty() && newPassword.isNotEmpty()) {
//                                errorMessage = null
//                                apiLogIn(newUsername, newPassword) { user ->
//
//
//                                    navigator?.push(WelcomeScreen(userDemo))
//                                }
//                            } else {
//                                errorMessage = "Por favor, introduce un usuario y una contraseña."
//                            }
//                        },
//                        modifier = Modifier
//                            .align(Alignment.CenterHorizontally),
//                        colors = ButtonDefaults.buttonColors(Color(0xFF1976D2))
//                    ) {
//                        Text("Iniciar sesion", color = Color.White)
//                    }
//                }
//
//                if (errorMessage != null) {
//                    Text(
//                        text = errorMessage!!,
//                        color = Color.Red,
//                        modifier = Modifier.padding(top = 8.dp)
//                    )
//                }
//            }
//        }
//    }

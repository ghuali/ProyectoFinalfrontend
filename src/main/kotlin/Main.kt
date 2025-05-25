import androidx.compose.ui.window.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import cafe.adriel.voyager.navigator.Navigator
import Screen.StartScreen
import ViewModel.SessionManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.painterResource

fun main() {
    // Cargar sesión antes de mostrar la ventana
    SessionManager.loadSession()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Canary's Esports",
            icon = painterResource("CanaryEsportsImg.png"),
            state = WindowState(
                placement = WindowPlacement.Maximized,
                size = DpSize.Unspecified
            ),
            undecorated = false,
            resizable = true,
        ) {
            App()
        }
    }
}


@Composable
fun App() {
    // Carga el token guardado al iniciar la app
    LaunchedEffect(Unit) {
        SessionManager.loadSession()
    }

    Navigator(screen = StartScreen())
}


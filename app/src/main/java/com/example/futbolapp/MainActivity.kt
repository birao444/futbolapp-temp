package com.example.futbolapp

// --- IMPORTS ESENCIALES ---
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column // <--- IMPORT PARA Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Para Home, Settings, Event, Group, Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier // <--- IMPORT PARA Modifier (aunque no lo uses directamente aquí, es bueno tenerlo si AppContent crece)
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle // <--- IMPORT PARA collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.futbolapp.ui.LoginScreen // Asegúrate que la ruta sea correcta
import com.example.futbolapp.ui.theme.FutbolAppTheme
import com.example.futbolapp.viewmodels.AuthViewModel // Asegúrate que la ruta sea correcta
import kotlinx.coroutines.launch

// --- DEFINICIONES DE DATOS Y LISTAS (COMO LAS TENÍAS) ---
data class NavItem(
    val id: String,
    val titleResId: Int,
    val icon: ImageVector
)

val navigationItemsList = listOf(
    NavItem("principal", R.string.nav_principal, Icons.Filled.Home),
    NavItem("proximo", R.string.nav_proximo_partido, Icons.Filled.Event),
    NavItem("mi_equipo", R.string.nav_mi_equipo, Icons.Filled.Group),
    NavItem("ajustes", R.string.nav_ajustes, Icons.Filled.Settings)
)
val settingsNavItem = NavItem("ajustes", R.string.nav_ajustes, Icons.Filled.Settings)


// --- CLASE MainActivity ---
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { // Esta suele ser la línea ~40-50
            // CORRECCIÓN PARA EL ERROR "Unresolved reference: compose" EN LÍNEA 45:
            // Simplemente llama a tu Composable principal dentro del tema.
            // No necesitas la palabra "compose" aquí.
            FutbolAppTheme {
                AppContent()
            }
        }
    }
}

// --- COMPOSABLE AppContent ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(authViewModel: AuthViewModel = viewModel()) {

    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle() // Necesita el import
    val isLoggedIn = currentUser != null

    Log.d("AppContent", "Recomponiendo. Usuario: ${currentUser?.uid ?: "null"}, isLoggedIn: $isLoggedIn")

    if (!isLoggedIn) {
        Log.d("AppContent", "Usuario NO logueado. Mostrando LoginScreen.")
        LoginScreen( // Asegúrate que LoginScreen esté definido e importado correctamente
            onLoginSuccess = {
                Log.d("AppContent", "LoginScreen reportó onLoginSuccess.")
            },
            authViewModel = authViewModel
        )
    } else {
        Log.d("AppContent", "Usuario SÍ logueado (${currentUser?.uid}). Mostrando contenido principal.")

        val userRole = remember(currentUser) { // Simulación de rol
            if (currentUser?.email?.contains("entrenador@") == true) "entrenador"
            else if (currentUser?.email?.contains("jugador@") == true) "jugador"
            else "desconocido"
        }
        Log.d("AppContent", "Rol (simulado): $userRole")

        val visibleNavigationItems = remember(userRole) {
            navigationItemsList.filter { item ->
                when (item.id) {
                    "mi_equipo" -> userRole == "entrenador" || userRole == "jugador"
                    "proximo" -> true
                    else -> true
                }
            }.filterNot { it.id == "ajustes" }
        }

        var selectedItem by remember(currentUser, userRole) {
            mutableStateOf(visibleNavigationItems.find { it.id == "principal" } ?: visibleNavigationItems.firstOrNull())
        }
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

                    visibleNavigationItems.forEach { item ->
                        NavigationDrawerItem(
                            icon = { Icon(item.icon, contentDescription = stringResource(item.titleResId)) },
                            label = { Text(stringResource(item.titleResId)) },
                            selected = item.id == selectedItem?.id,
                            onClick = {
                                selectedItem = item
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        NavigationDrawerItem(
                            icon = { Icon(settingsNavItem.icon, contentDescription = stringResource(settingsNavItem.titleResId)) },
                            label = { Text(stringResource(settingsNavItem.titleResId), fontWeight = FontWeight.Bold) },
                            selected = settingsNavItem.id == selectedItem?.id,
                            onClick = {
                                selectedItem = settingsNavItem
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(selectedItem?.let { stringResource(it.titleResId) } ?: stringResource(R.string.app_name))
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            }) {
                                Icon(Icons.Filled.Menu, stringResource(R.string.abrir_menu_navegacion))
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Column( // Necesita el import de Column
                    modifier = Modifier.fillMaxSize().padding(innerPadding)
                ) {
                    // Navegación de contenido (Asegúrate que estas pantallas existan o sean placeholders)
                    when (selectedItem?.id) {
                        "principal" -> PrincipalScreen()
                        "proximo" -> ProximoPartidoScreen()
                        "mi_equipo" -> if (userRole == "entrenador" || userRole == "jugador") MiEquipoScreen() else AccesoDenegadoScreen(item = selectedItem)
                        "ajustes" -> AjustesScreen(authViewModel = authViewModel)
                        else -> {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    selectedItem?.let { "Contenido para ${stringResource(it.titleResId)}" }
                                        ?: "Bienvenido. Selecciona una opción.",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// --- PANTALLAS PLACEHOLDER (DEBEN ESTAR DEFINIDAS) ---
@Composable
fun PrincipalScreen() { ScreenPlaceholder(name = "Principal") }
@Composable
fun ProximoPartidoScreen() { ScreenPlaceholder(name = "Próximo Partido") }
@Composable
fun MiEquipoScreen() { ScreenPlaceholder(name = "Mi Equipo") }

@Composable
fun AjustesScreen(authViewModel: AuthViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pantalla de Ajustes", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { authViewModel.signOut() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cerrar Sesión", color = MaterialTheme.colorScheme.onError)
        }
    }
}

@Composable
fun AccesoDenegadoScreen(item: NavItem?) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Acceso Denegado", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.error)
        item?.let {
            Text("No tienes permiso para ver '${stringResource(it.titleResId)}'.", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ScreenPlaceholder(name: String) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Pantalla: $name", style = MaterialTheme.typography.headlineMedium)
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun AppContentPreview_LoggedOut() {
    FutbolAppTheme {
        LoginScreen(onLoginSuccess = {}) // Asegúrate que LoginScreen esté definida e importada
    }
}

package com.example.futbolapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlignHorizontalLeft
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.futbolapp.ui.theme.FutbolAppTheme
import com.example.futbolapp.ui.LoginScreen
import com.example.futbolapp.viewmodels.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

// Define tu NavItem data class
data class NavItem(
    val id: String,
    val titleResId: Int,
    val icon: ImageVector,
    val isSettings: Boolean = false // Nuevo campo para identificar el item de Ajustes
)

// Lista de items de navegación
val navigationItemsList = listOf(
    NavItem(id = "principal", titleResId = R.string.nav_principal, icon = Icons.Filled.Home),
    NavItem(id = "proximo", titleResId = R.string.nav_proximo, icon = Icons.Filled.CalendarToday),
    NavItem(id = "mi_equipo", titleResId = R.string.nav_mi_equipo, icon = Icons.Filled.Group),
    NavItem(id = "partidos", titleResId = R.string.nav_partidos, icon = Icons.Filled.Dashboard),
    NavItem(id = "jugadores", titleResId = R.string.nav_jugadores, icon = Icons.Filled.People),
    NavItem(id = "alineaciones", titleResId = R.string.nav_alineaciones, icon = Icons.Filled.AlignHorizontalLeft),
    NavItem(id = "estadisticas", titleResId = R.string.nav_estadisticas, icon = Icons.Filled.BarChart),
    NavItem(id = "record", titleResId = R.string.nav_record, icon = Icons.Filled.Star),
    NavItem(id = "elementos", titleResId = R.string.nav_elementos, icon = Icons.Filled.Settings), // Este es el "Elementos Adicionales"
    NavItem(id = "campos", titleResId = R.string.nav_campos, icon = Icons.Filled.Place),
    // El item de Ajustes se manejará de forma especial en el drawer, pero podría estar aquí si quisieras un comportamiento estándar
    // NavItem(id = "ajustes", titleResId = R.string.nav_ajustes, icon = Icons.Filled.Settings, isSettings = true)
)

// Item específico para Ajustes (lo definimos separado para darle un tratamiento especial)
val settingsNavItem = NavItem(id = "ajustes", titleResId = R.string.nav_ajustes, icon = Icons.Filled.Settings, isSettings = true)


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FutbolAppTheme {
                AppContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(authViewModel: AuthViewModel = viewModel()) {
    val currentUser = authViewModel.currentUser
    var isLoggedIn by remember { mutableStateOf(currentUser != null) }

    if (!isLoggedIn) {
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
        return
    }

    var selectedItem by remember { mutableStateOf<NavItem?>(navigationItemsList.firstOrNull { it.id == "principal" }) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                // Puedes ajustar el ancho del drawer aquí si es necesario, pero por defecto se adapta bien.
                // modifier = Modifier.width(300.dp) // Ejemplo
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

                // Items de navegación regulares
                navigationItemsList.forEach { item ->
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

                Spacer(Modifier.weight(1f)) // Empuja el botón de Ajustes hacia abajo

                // Botón de Ajustes destacado y de ancho completo
                // Usamos un Box para poder aplicar un fondo si queremos destacarlo más
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp) // Padding alrededor del botón de ajustes
                ) {
                    NavigationDrawerItem(
                        icon = { Icon(settingsNavItem.icon, contentDescription = stringResource(settingsNavItem.titleResId)) },
                        label = {
                            Text(
                                stringResource(settingsNavItem.titleResId),
                                fontWeight = FontWeight.Bold // Opcional: texto en negrita para destacar
                            )
                        },
                        selected = settingsNavItem.id == selectedItem?.id, // Para que se marque como seleccionado
                        onClick = {
                            selectedItem = settingsNavItem
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.fillMaxWidth(), // El item interno ocupa el ancho del Box padre
                        // Colores personalizados para destacar el botón de Ajustes
                        colors = NavigationDrawerItemDefaults.colors(
                            // Puedes cambiar estos colores para que destaque
                            // selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            // unselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant, // Ejemplo de color de fondo
                            // selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            // unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            // selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            // unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                Spacer(Modifier.height(12.dp)) // Espacio debajo del botón de Ajustes
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedItem?.id) {
                    "principal" -> PrincipalScreen()
                    "proximo" -> ProximoPartidoScreen()
                    "mi_equipo" -> MiEquipoScreen()
                    "partidos" -> PartidosScreen()
                    "jugadores" -> JugadoresScreen()
                    "alineaciones" -> AlineacionesScreen()
                    "estadisticas" -> EstadisticasScreen()
                    "record" -> RecordScreen()
                    "elementos" -> ElementosScreen()
                    "campos" -> CamposScreen()
                    "ajustes" -> AjustesScreen() // NUEVO CASO PARA AJUSTES
                    else -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val textToShow = selectedItem?.let { "Contenido para ${stringResource(it.titleResId)}" }
                                ?: "Bienvenido a FutbolApp"
                            Text(textToShow, style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                }
            }
        }
    }
}

// --- Pantalla Principal ---
@Composable
fun PrincipalScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(stringResource(R.string.titulo_principal_screen), style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))
        Text(stringResource(R.string.descripcion_principal_screen), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 24.dp))
        // ... (contenido de PrincipalScreen)
    }
}

// --- NUEVA Pantalla de Ajustes ---
@Composable
fun AjustesScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top, // o Arrangement.Center si prefieres
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.titulo_ajustes_screen),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(R.string.descripcion_ajustes_screen),
            style = MaterialTheme.typography.bodyLarge
        )
        // Aquí añadirás las opciones de configuración de tu app
        // Ejemplos:
        // Switch(checked = ..., onCheckedChange = ...)
        // Button(onClick = ...) { Text("Vaciar caché") }
    }
}


// --- Composables Placeholder para las otras pantallas (DEBES CREARLOS O ADAPTARLOS) ---
@Composable
fun ProximoPartidoScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.contenido_proximo), style = MaterialTheme.typography.headlineSmall)
        Text("Esta pantalla mostrará detalles del próximo partido.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top=8.dp))
    }
}

@Composable
fun MiEquipoScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.contenido_mi_equipo), style = MaterialTheme.typography.headlineSmall)
        Text("Aquí podrás ver y gestionar la información de tu equipo.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top=8.dp))
    }
}
@Composable
fun PartidosScreen() {
     Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.contenido_partidos), style = MaterialTheme.typography.headlineSmall)
         Text("Listado de partidos jugados y futuros.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top=8.dp))
    }
}
@Composable
fun JugadoresScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.contenido_jugadores), style = MaterialTheme.typography.headlineSmall)
        Text("Información y estadísticas de los jugadores.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top=8.dp))
    }
}
@Composable
fun AlineacionesScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.contenido_alineaciones), style = MaterialTheme.typography.headlineSmall)
        Text("Visualiza y crea alineaciones.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top=8.dp))
    }
}
@Composable
fun EstadisticasScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.contenido_estadisticas), style = MaterialTheme.typography.headlineSmall)
        Text("Estadísticas generales del torneo o equipo.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top=8.dp))
    }
}
@Composable
fun RecordScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.contenido_record), style = MaterialTheme.typography.headlineSmall)
        Text("Historial y récords destacados.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top=8.dp))
    }
}
@Composable
fun ElementosScreen() { // Este es el que tenías como "Elementos Adicionales"
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.nav_elementos), style = MaterialTheme.typography.headlineSmall) // Usando el string de nav_elementos
        Text("Configuración de elementos adicionales.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top=8.dp))
    }
}
@Composable
fun CamposScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.contenido_campos), style = MaterialTheme.typography.headlineSmall)
        Text("Información sobre los campos de juego.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top=8.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FutbolAppTheme {
        AppContent()
    }
}

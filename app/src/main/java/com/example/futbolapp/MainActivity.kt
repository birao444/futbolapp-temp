package com.example.futbolapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlignHorizontalLeft
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home // Icono para la nueva pantalla principal
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.futbolapp.ui.theme.FutbolAppTheme
import kotlinx.coroutines.launch

// Define tu NavItem data class
data class NavItem(
    val id: String,
    val titleResId: Int, // Usaremos ID de recurso de String para el título
    val icon: ImageVector
)

// Lista de items de navegación
val navigationItemsList = listOf(
    NavItem(id = "principal", titleResId = R.string.nav_principal, icon = Icons.Filled.Home), // NUEVA PANTALLA PRINCIPAL
    NavItem(id = "proximo", titleResId = R.string.nav_proximo, icon = Icons.Filled.CalendarToday),
    NavItem(id = "mi_equipo", titleResId = R.string.nav_mi_equipo, icon = Icons.Filled.Group),
    NavItem(id = "partidos", titleResId = R.string.nav_partidos, icon = Icons.Filled.Dashboard), // Considera un icono diferente si "principal" también usa Dashboard
    NavItem(id = "jugadores", titleResId = R.string.nav_jugadores, icon = Icons.Filled.People),
    NavItem(id = "alineaciones", titleResId = R.string.nav_alineaciones, icon = Icons.Filled.AlignHorizontalLeft),
    NavItem(id = "estadisticas", titleResId = R.string.nav_estadisticas, icon = Icons.Filled.BarChart),
    NavItem(id = "record", titleResId = R.string.nav_record, icon = Icons.Filled.Star),
    NavItem(id = "elementos", titleResId = R.string.nav_elementos, icon = Icons.Filled.Settings),
    NavItem(id = "campos", titleResId = R.string.nav_campos, icon = Icons.Filled.Place)
)

@OptIn(ExperimentalMaterial3Api::class) // Anotación para APIs experimentales de Material 3
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FutbolAppTheme {
                AppContent() // Contenido principal de la app en un Composable separado
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent() {
    // Estado para el ítem seleccionado, inicializado con "principal"
    var selectedItem by remember { mutableStateOf<NavItem?>(navigationItemsList.firstOrNull { it.id == "principal" }) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp)) // Más espacio arriba
                Text(
                    text = stringResource(R.string.app_name), // Título de la app en el drawer
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                navigationItemsList.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = stringResource(item.titleResId)) },
                        label = { Text(stringResource(item.titleResId)) },
                        selected = item.id == selectedItem?.id,
                        onClick = {
                            selectedItem = item
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
                Spacer(Modifier.weight(1f)) // Empuja items fijos (si los hubiera) hacia abajo
                // Aquí podrías añadir items fijos en la parte inferior del drawer si es necesario
                // Divider()
                // NavigationDrawerItem(label = { Text("Ajustes") }, selected = false, onClick = { /* ... */ })
                Spacer(Modifier.height(12.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        // Mostrar el título de la pantalla seleccionada en el TopAppBar
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
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = stringResource(R.string.abrir_menu_navegacion)
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            // El Column aquí contendrá la pantalla actual
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding) // Padding del Scaffold (para TopAppBar, etc.)
                                        // El padding de 16.dp para el contenido específico de la pantalla se añade dentro de cada pantalla
            ) {
                // Navegación entre las diferentes pantallas (Composables)
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
                    else -> { // Contenido por defecto o si el item no se reconoce
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (selectedItem != null) {
                                Text("Contenido para ${stringResource(selectedItem!!.titleResId)}", style = MaterialTheme.typography.headlineMedium)
                            } else {
                                Text("Bienvenido a FutbolApp", style = MaterialTheme.typography.headlineMedium)
                            }
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Padding específico para esta pantalla
        verticalArrangement = Arrangement.Top, // El contenido empieza desde arriba
        horizontalAlignment = Alignment.Start // El contenido se alinea a la izquierda
    ) {
        Text(
            text = stringResource(R.string.titulo_principal_screen),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(R.string.descripcion_principal_screen),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Text(
            text = "Próximos Pasos:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text("- Definir qué datos se mostrarán aquí.")
        Text("- Implementar la lógica para obtener y mostrar esos datos (ej. desde una base de datos, API, etc.).")
        Text("- Usar componentes como LazyColumn si la lista de datos es larga.")
    }
}

// --- Composables Placeholder para las otras pantallas ---
// Debes desarrollar el contenido real de estas pantallas.

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
fun ElementosScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.contenido_elementos), style = MaterialTheme.typography.headlineSmall)
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

// Preview para ver cómo se ve AppContent en el editor
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FutbolAppTheme {
        AppContent()
    }
}

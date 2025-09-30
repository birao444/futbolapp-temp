package com.example.futbolapp

import android.os.Bundle
import android.util.Log
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.futbolapp.ui.screens.LoginScreen
import com.example.futbolapp.ui.screens.SignupScreen
import com.example.futbolapp.ui.screens.AIAssistantScreen
import com.example.futbolapp.ui.screens.RoleManagementScreen
import com.example.futbolapp.ui.theme.FutbolAppTheme
import com.example.futbolapp.utils.NavItem
import com.example.futbolapp.utils.RoleBasedNavigation
import com.example.futbolapp.viewmodel.RoleViewModel
import com.example.futbolapp.viewmodels.AuthViewModel
import com.example.futbolapp.viewmodels.TeamViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: com.example.futbolapp.viewmodels.ThemeViewModel = viewModel()
            val themeMode by themeViewModel.themeMode.collectAsState()
            val darkTheme = when (themeMode) {
                com.example.futbolapp.viewmodels.ThemeMode.LIGHT -> false
                com.example.futbolapp.viewmodels.ThemeMode.DARK -> true
                com.example.futbolapp.viewmodels.ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            
            FutbolAppTheme(darkTheme = darkTheme) {
                AppContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(
    authViewModel: AuthViewModel = viewModel(),
    teamViewModel: TeamViewModel = viewModel(),
    roleViewModel: RoleViewModel = viewModel()
) {
    // Observa currentUser del AuthViewModel.
    // collectAsStateWithLifecycle es la forma moderna y recomendada de observar un StateFlow desde Compose. Reacciona a los cambios de currentUser de manera eficiente y respetando el ciclo de vida.
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()

    // Determina si el usuario está logueado basándose en currentUser.
    // Esta será la única fuente de verdad para el estado de login.
    val isLoggedIn = currentUser != null

    Log.d("AppContent", "Recomponiendo AppContent. Usuario actual: ${currentUser?.uid ?: "null"}, isLoggedIn: $isLoggedIn")

    if (!isLoggedIn) {
        Log.d("AppContent", "Usuario NO logueado. Mostrando LoginScreen.")
        LoginScreen(
            onLoginSuccess = {
                // Esta lambda se llama DESDE LoginScreen después de un intento de login exitoso.
                // En este punto, el AuthStateListener en AuthViewModel ya debería haber
                // actualizado currentUser, y AppContent se recompondrá mostrando el contenido principal.
                // No necesitas hacer nada más aquí para cambiar el estado de login.
                Log.d("AppContent", "LoginScreen reportó onLoginSuccess.")
            },
            onNavigateToSignup = { /* TODO: Implementar navegación a signup */ }
        )
        // Importante: No renderizar el resto si no está logueado.
        // El 'return' no es estrictamente necesario aquí si el if/else cubre toda la lógica,
        // pero es una buena práctica para la claridad si la estructura se vuelve más compleja.
    } else {
        // Usuario SÍ está logueado, muestra el contenido principal de la app.
        Log.d("AppContent", "Usuario SÍ logueado (${currentUser?.uid}). Mostrando contenido principal.")

        // Estado del rol del usuario
        val currentUserRole by roleViewModel.currentUserRole.collectAsState()
        var currentTeamId by remember { mutableStateOf<String?>(null) }

        // Start real-time listening for teams when logged in
        LaunchedEffect(currentUser?.uid) {
            currentUser?.uid?.let { userId ->
                teamViewModel.startListeningToTeamsForUser(userId)
            }
        }

        // Obtener el primer equipo del usuario (puedes mejorar esto para seleccionar equipo)
        val teams by teamViewModel.teams.collectAsState()
        LaunchedEffect(teams) {
            if (teams.isNotEmpty() && currentTeamId == null) {
                currentTeamId = teams.first().id
            }
        }

        // Cargar rol del usuario cuando tengamos el teamId
        LaunchedEffect(currentUser?.uid, currentTeamId) {
            if (currentUser?.uid != null && currentTeamId != null) {
                roleViewModel.loadCurrentUserRole(currentUser.uid, currentTeamId!!)
            }
        }

        // Obtener items de navegación según el rol
        val navigationItems = remember(currentUserRole) {
            RoleBasedNavigation.getNavigationItemsForRole(currentUserRole)
        }

        // Pantalla inicial según el rol
        val defaultScreen = remember(currentUserRole) {
            RoleBasedNavigation.getDefaultScreenForRole(currentUserRole)
        }

        // Estado para el ítem seleccionado en el Navigation Drawer.
        // Se reinicia a "principal" si el currentUser cambia (ej. nuevo login).
        var selectedItem by remember(currentUser) {
            mutableStateOf<NavItem?>(navigationItems.firstOrNull { it.id == defaultScreen })
        }
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                
                // Header con información del usuario y rol
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    // Mostrar rol del usuario
                    currentUserRole?.let { role ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = role.displayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = RoleBasedNavigation.getRoleDescription(role),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
                
                Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

                // Items de navegación filtrados por rol
                navigationItems.forEach { item ->
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

                // Botón de Ajustes (siempre visible)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    NavigationDrawerItem(
                        icon = { Icon(RoleBasedNavigation.settingsNavItem.icon, contentDescription = stringResource(RoleBasedNavigation.settingsNavItem.titleResId)) },
                        label = {
                            Text(
                                stringResource(RoleBasedNavigation.settingsNavItem.titleResId),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        selected = RoleBasedNavigation.settingsNavItem.id == selectedItem?.id,
                        onClick = {
                            selectedItem = RoleBasedNavigation.settingsNavItem
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedItem?.id) {
                    "principal" -> PrincipalScreen(userRole = currentUserRole)
                    "proximo" -> ProximoPartidoScreen()
                    "mi_equipo" -> MiEquipoScreen()
                    "partidos" -> PartidosScreen(userRole = currentUserRole)
                    "jugadores" -> JugadoresScreen(userRole = currentUserRole)
                    "alineaciones" -> AlineacionesScreen(userRole = currentUserRole)
                    "estadisticas" -> EstadisticasScreen()
                    "record" -> RecordScreen()
                    "elementos" -> ElementosScreen()
                    "campos" -> CamposScreen(userRole = currentUserRole)
                    "ai_assistant" -> AIAssistantScreen()
                    "roles" -> {
                        if (currentTeamId != null && currentUser?.uid != null) {
                            RoleManagementScreen(
                                teamId = currentTeamId!!,
                                currentUserId = currentUser.uid
                            )
                        } else {
                            Text("Cargando...", modifier = Modifier.padding(16.dp))
                        }
                    }
                    "ajustes" -> AjustesScreen()
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

// Pantallas con información del rol
@Composable
fun PrincipalScreen(userRole: com.example.futbolapp.models.UserRole?) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            stringResource(R.string.titulo_principal_screen),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        userRole?.let { role ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Vista personalizada para: ${role.displayName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        RoleBasedNavigation.getRoleDescription(role),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        
        Text(
            stringResource(R.string.descripcion_principal_screen),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun PartidosScreen(userRole: com.example.futbolapp.models.UserRole?) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.contenido_partidos), style = MaterialTheme.typography.headlineSmall)
        Text(stringResource(R.string.desc_partidos), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top=8.dp))
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Mostrar opciones según el rol
        when (userRole) {
            com.example.futbolapp.models.UserRole.ENTRENADOR,
            com.example.futbolapp.models.UserRole.SEGUNDO,
            com.example.futbolapp.models.UserRole.COORDINADOR -> {
                Text("Puedes crear y gestionar partidos", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            com.example.futbolapp.models.UserRole.JUGADOR -> {
                Text("Solo puedes ver los partidos", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
            else -> {}
        }
    }
}

@Composable
fun JugadoresScreen(userRole: com.example.futbolapp.models.UserRole?) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.contenido_jugadores), style = MaterialTheme.typography.headlineSmall)
        Text(stringResource(R.string.desc_jugadores), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top=8.dp))
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when (userRole) {
            com.example.futbolapp.models.UserRole.ENTRENADOR,
            com.example.futbolapp.models.UserRole.SEGUNDO -> {
                Text("Puedes añadir, editar y eliminar jugadores", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            com.example.futbolapp.models.UserRole.FISIO -> {
                Text("Puedes ver jugadores y gestionar su estado físico", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            else -> {}
        }
    }
}

@Composable
fun AlineacionesScreen(userRole: com.example.futbolapp.models.UserRole?) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.contenido_alineaciones), style = MaterialTheme.typography.headlineSmall)
        Text(stringResource(R.string.desc_alineaciones), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top=8.dp))
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when (userRole) {
            com.example.futbolapp.models.UserRole.ENTRENADOR,
            com.example.futbolapp.models.UserRole.SEGUNDO -> {
                Text("Puedes crear y modificar alineaciones", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            com.example.futbolapp.models.UserRole.JUGADOR -> {
                Text("Solo puedes ver las alineaciones", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
            else -> {}
        }
    }
}

@Composable
fun CamposScreen(userRole: com.example.futbolapp.models.UserRole?) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.contenido_campos), style = MaterialTheme.typography.headlineSmall)
        Text(stringResource(R.string.desc_campos), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top=8.dp))
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when (userRole) {
            com.example.futbolapp.models.UserRole.ENTRENADOR,
            com.example.futbolapp.models.UserRole.COORDINADOR -> {
                Text("Puedes gestionar los campos de juego", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            else -> {}
        }
    }
}

// Pantallas sin cambios por rol
@Composable
fun ProximoPartidoScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.contenido_proximo), style = MaterialTheme.typography.headlineSmall)
        Text(stringResource(R.string.desc_proximo), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top=8.dp))
    }
}

@Composable
fun MiEquipoScreen(teamViewModel: TeamViewModel = viewModel()) {
    val teams by teamViewModel.teams.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
        Text(stringResource(R.string.contenido_mi_equipo), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))

        if (teams.isEmpty()) {
            Text("No tienes equipos registrados.", style = MaterialTheme.typography.bodyMedium)
        } else {
            teams.forEach { team ->
                Text("Equipo: ${team.name}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 8.dp))
                Text("Descripción: ${team.description}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 16.dp))
            }
        }
    }
}

@Composable
fun EstadisticasScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.contenido_estadisticas), style = MaterialTheme.typography.headlineSmall)
        Text(stringResource(R.string.desc_estadisticas), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top=8.dp))
    }
}

@Composable
fun RecordScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.contenido_record), style = MaterialTheme.typography.headlineSmall)
        Text(stringResource(R.string.desc_record), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top=8.dp))
    }
}

@Composable
fun ElementosScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.nav_elementos), style = MaterialTheme.typography.headlineSmall)
        Text(stringResource(R.string.desc_elementos), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top=8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(
    authViewModel: AuthViewModel = viewModel(),
    themeViewModel: com.example.futbolapp.viewmodels.ThemeViewModel = viewModel()
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val currentThemeMode by themeViewModel.themeMode.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.titulo_ajustes_screen),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(R.string.descripcion_ajustes_screen),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "Tema de la aplicación",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        androidx.compose.material3.Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.RadioButton(
                        selected = currentThemeMode == com.example.futbolapp.viewmodels.ThemeMode.LIGHT,
                        onClick = { themeViewModel.setThemeMode(com.example.futbolapp.viewmodels.ThemeMode.LIGHT) }
                    )
                    Text("Claro", modifier = Modifier.padding(start = 8.dp))
                }
                
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.RadioButton(
                        selected = currentThemeMode == com.example.futbolapp.viewmodels.ThemeMode.DARK,
                        onClick = { themeViewModel.setThemeMode(com.example.futbolapp.viewmodels.ThemeMode.DARK) }
                    )
                    Text("Oscuro", modifier = Modifier.padding(start = 8.dp))
                }
                
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.RadioButton(
                        selected = currentThemeMode == com.example.futbolapp.viewmodels.ThemeMode.SYSTEM,
                        onClick = { themeViewModel.setThemeMode(com.example.futbolapp.viewmodels.ThemeMode.SYSTEM) }
                    )
                    Text("Según el sistema", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        androidx.compose.material3.Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Cerrar Sesión", color = MaterialTheme.colorScheme.onError)
        }
    }

    if (showLogoutDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        scope.launch {
                            authViewModel.signOut()
                            showLogoutDialog = false
                        }
                    }
                ) {
                    Text("Sí, cerrar sesión")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FutbolAppTheme {
        AppContent()
    }
}

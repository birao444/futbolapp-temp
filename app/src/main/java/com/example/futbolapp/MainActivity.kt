package com.example.futbolapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Column
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.futbolapp.ui.LoginScreen
import com.example.futbolapp.ui.RoleBasedNavigation
import com.example.futbolapp.ui.theme.FutbolAppTheme
import com.example.futbolapp.viewmodels.AuthViewModel
import com.example.futbolapp.viewmodels.RoleViewModel
import com.example.futbolapp.Permission
import kotlinx.coroutines.launch

// Define NavItem y las listas de navegación (o impórtalas si están en otro archivo)
data class NavItem(
    val id: String,
    val titleResId: Int, // Referencia a un recurso de String (ej. R.string.nav_principal)
    val icon: ImageVector
)

// Asegúrate de tener estas strings en res/values/strings.xml
// <string name="app_name">FutbolApp</string>
// <string name="abrir_menu_navegacion">Abrir menú de navegación</string>
// <string name="nav_principal">Principal</string>
// <string name="nav_ajustes">Ajustes</string>
// ... (y para los demás items)

val navigationItemsList = listOf(
    NavItem("principal", R.string.nav_principal, Icons.Filled.Home),
    NavItem("proximo", R.string.nav_proximo_partido, Icons.Filled.Event), // Ejemplo
    NavItem("mi_equipo", R.string.nav_mi_equipo, Icons.Filled.Group),     // Ejemplo
    NavItem("ajustes", R.string.nav_ajustes, Icons.Filled.Settings)
    // Añade aquí todos tus items de navegación
)
val settingsNavItem = NavItem("ajustes", R.string.nav_ajustes, Icons.Filled.Settings) // O si lo manejas como un ítem especial


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

    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val isLoggedIn = currentUser != null

    Log.d("AppContent", "Recomponiendo. Usuario: ${currentUser?.uid ?: "null"}, isLoggedIn: $isLoggedIn")

    if (!isLoggedIn) {
        Log.d("AppContent", "Usuario NO logueado. Mostrando LoginScreen.")
        LoginScreen(
            onLoginSuccess = {
                Log.d("AppContent", "LoginScreen reportó onLoginSuccess.")
                // No es necesario cambiar isLoggedIn aquí, collectAsStateWithLifecycle lo hará
            },
            authViewModel = authViewModel
        )
    } else {
        Log.d("AppContent", "Usuario SÍ logueado (${currentUser?.uid}). Mostrando contenido principal con navegación basada en roles.")

        RoleBasedNavigation { userRole, canAccessScreen ->
            Log.d("AppContent", "Rol del usuario: $userRole")

            // Filtra los items de navegación basados en permisos de rol
            val visibleNavigationItems = remember(userRole) {
                navigationItemsList.filter { item ->
                    canAccessScreen(item.id)
                }.filterNot { it.id == "ajustes" } // Quitamos ajustes para ponerlo al final por separado
            }

            var selectedItem by remember(currentUser, userRole) { // Reinicia si el usuario o rol cambian
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

                        // Mostrar rol del usuario
                        Text(
                            text = "Rol: ${userRole.replaceFirstChar { it.uppercase() }}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.primary
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

                        Spacer(Modifier.weight(1f)) // Empuja el botón de Ajustes hacia abajo

                        // Botón de Ajustes (siempre visible al final del drawer)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp)
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Navegación de contenido basada en selectedItem.id con verificación de permisos
                        when (selectedItem?.id) {
                            "principal" -> PrincipalScreen(userRole = userRole)
                            "proximo" -> if (canAccessScreen("proximo_partido")) ProximoPartidoScreen(userRole = userRole) else AccesoDenegadoScreen(item = selectedItem)
                            "mi_equipo" -> if (canAccessScreen("mi_equipo")) MiEquipoScreen(userRole = userRole) else AccesoDenegadoScreen(item = selectedItem)
                            // Añade casos para todos tus NavItems con verificación de permisos
                            // "partidos" -> if (canAccessScreen("partidos")) PartidosScreen(userRole = userRole) else AccesoDenegadoScreen(item = selectedItem)
                            // "jugadores" -> if (canAccessScreen("jugadores")) JugadoresScreen(userRole = userRole) else AccesoDenegadoScreen(item = selectedItem)
                            // "alineaciones" -> if (canAccessScreen("alineaciones")) AlineacionesScreen(userRole = userRole) else AccesoDenegadoScreen(item = selectedItem)
                            // "estadisticas" -> if (canAccessScreen("estadisticas")) EstadisticasScreen(userRole = userRole) else AccesoDenegadoScreen(item = selectedItem)
                            // "record" -> if (canAccessScreen("record")) RecordScreen(userRole = userRole) else AccesoDenegadoScreen(item = selectedItem)
                            // "elementos" -> if (canAccessScreen("elementos")) ElementosScreen(userRole = userRole) else AccesoDenegadoScreen(item = selectedItem)
                            // "campos" -> if (canAccessScreen("campos")) CamposScreen(userRole = userRole) else AccesoDenegadoScreen(item = selectedItem)
                            "ajustes" -> AjustesScreen(authViewModel = authViewModel, userRole = userRole)
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
                                    Text("Tu rol: $userRole", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- PANTALLAS CON DIFERENTES VISTAS SEGÚN ROL ---
@Composable
fun PrincipalScreen(userRole: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "Bienvenido a FutbolApp",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            "Tu rol: ${userRole.replaceFirstChar { it.uppercase() }}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Contenido diferente según el rol
        when (userRole) {
            RoleManager.ENTRENADOR -> {
                Text("Como entrenador tienes acceso completo a todas las funciones:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("• Gestionar equipo y mejoras", style = MaterialTheme.typography.bodyMedium)
                Text("• Crear y editar alineaciones", style = MaterialTheme.typography.bodyMedium)
                Text("• Administrar partidos y estadísticas", style = MaterialTheme.typography.bodyMedium)
                Text("• Asignar roles a usuarios", style = MaterialTheme.typography.bodyMedium)
            }
            RoleManager.SEGUNDO -> {
                Text("Como segundo entrenador puedes:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("• Gestionar equipo y mejoras", style = MaterialTheme.typography.bodyMedium)
                Text("• Crear y editar alineaciones", style = MaterialTheme.typography.bodyMedium)
                Text("• Administrar partidos y estadísticas", style = MaterialTheme.typography.bodyMedium)
            }
            RoleManager.JUGADOR -> {
                Text("Como jugador puedes:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("• Ver información de tu equipo", style = MaterialTheme.typography.bodyMedium)
                Text("• Consultar próximos partidos", style = MaterialTheme.typography.bodyMedium)
                Text("• Ver tus estadísticas", style = MaterialTheme.typography.bodyMedium)
            }
            RoleManager.FISIO -> {
                Text("Como fisioterapeuta puedes:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("• Gestionar información de jugadores", style = MaterialTheme.typography.bodyMedium)
                Text("• Ver estadísticas médicas", style = MaterialTheme.typography.bodyMedium)
                Text("• Consultar próximos partidos", style = MaterialTheme.typography.bodyMedium)
            }
            RoleManager.COORDINADOR -> {
                Text("Como coordinador puedes:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("• Administrar partidos y campos", style = MaterialTheme.typography.bodyMedium)
                Text("• Ver estadísticas generales", style = MaterialTheme.typography.bodyMedium)
                Text("• Gestionar logística del equipo", style = MaterialTheme.typography.bodyMedium)
            }
            else -> {
                Text("Tu rol está pendiente de asignación por un entrenador.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun ProximoPartidoScreen(userRole: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "Próximos Partidos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Contenido diferente según permisos
        if (RoleManager.hasPermission(userRole, Permission.VIEW_MATCHES)) {
            Text("Aquí se mostrarán los próximos partidos de tu equipo.",
                style = MaterialTheme.typography.bodyLarge
            )

            if (RoleManager.hasPermission(userRole, Permission.MANAGE_MATCHES)) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Como ${userRole.replaceFirstChar { it.uppercase() }} puedes crear y editar partidos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Text("No tienes permisos para ver información de partidos.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun MiEquipoScreen(userRole: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "Mi Equipo",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Contenido diferente según permisos
        if (RoleManager.hasPermission(userRole, Permission.VIEW_TEAM)) {
            Text("Información de tu equipo:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (RoleManager.hasPermission(userRole, Permission.EDIT_TEAM)) {
                Text("• Puedes editar la información del equipo", style = MaterialTheme.typography.bodyMedium)
                Text("• Gestionar mejoras y parámetros", style = MaterialTheme.typography.bodyMedium)
            }

            if (RoleManager.hasPermission(userRole, Permission.MANAGE_PLAYERS)) {
                Text("• Administrar lista de jugadores", style = MaterialTheme.typography.bodyMedium)
            }

            if (RoleManager.hasPermission(userRole, Permission.MANAGE_IMPROVEMENTS)) {
                Text("• Configurar mejoras del equipo", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            Text("No tienes permisos para ver información del equipo.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun AjustesScreen(authViewModel: AuthViewModel, userRole: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "Ajustes",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            "Tu rol actual: ${userRole.replaceFirstChar { it.uppercase() }}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Mostrar opciones adicionales según el rol
        if (RoleManager.hasPermission(userRole, Permission.ASSIGN_ROLES)) {
            Text(
                "Como entrenador puedes gestionar roles de usuarios.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                Log.d("AjustesScreen", "Botón 'Cerrar Sesión' presionado.")
                authViewModel.signOut()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pantalla: $name", style = MaterialTheme.typography.headlineMedium)
        Text("(Contenido pendiente)")
    }
}

// Preview para AppContent (puede ser útil, pero requiere un AuthViewModel de prueba si quieres ver el estado logueado)
@Preview(showBackground = true)
@Composable
fun AppContentPreview_LoggedOut() {
    FutbolAppTheme {
        // Para previsualizar el estado deslogueado, podrías necesitar un AuthViewModel que
        // por defecto no tenga usuario, o pasar un ViewModel de prueba.
        // Por simplicidad, esta preview podría simplemente llamar a LoginScreen directamente.
        LoginScreen(onLoginSuccess = {})
    }
}

// Necesitarás añadir los recursos de string (R.string...) a tu archivo strings.xml
// Ejemplo de strings.xml:
/*
<resources>
    <string name="app_name">FutbolApp</string>
    <string name="abrir_menu_navegacion">Abrir menú de navegación</string>

    <string name="nav_principal">Principal</string>
    <string name="nav_proximo_partido">Próximo Partido</string>
    <string name="nav_mi_equipo">Mi Equipo</string>
    <string name="nav_ajustes">Ajustes</string>
    // ... y para el resto de tus items de navegación ...
</resources>
*/

package com.example.futbolapp.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.futbolapp.R
import com.example.futbolapp.models.UserRole

data class NavItem(
    val id: String,
    val titleResId: Int,
    val icon: ImageVector,
    val isSettings: Boolean = false,
    val requiredRoles: List<UserRole> = UserRole.values().toList() // Por defecto, todos los roles
)

object RoleBasedNavigation {
    
    // Definir todos los items de navegación con sus roles permitidos
    private val allNavItems = listOf(
        // PRINCIPAL - Todos pueden ver
        NavItem(
            id = "principal",
            titleResId = R.string.nav_principal,
            icon = Icons.Filled.Home,
            requiredRoles = listOf(
                UserRole.ADMIN_ENTRENADOR,
                UserRole.ENTRENADOR,
                UserRole.SEGUNDO,
                UserRole.JUGADOR,
                UserRole.FISIO,
                UserRole.COORDINADOR
            )
        ),
        
        // PRÓXIMO PARTIDO - Todos pueden ver
        NavItem(
            id = "proximo",
            titleResId = R.string.nav_proximo_partido,
            icon = Icons.Filled.CalendarToday,
            requiredRoles = listOf(
                UserRole.ADMIN_ENTRENADOR,
                UserRole.ENTRENADOR,
                UserRole.SEGUNDO,
                UserRole.JUGADOR,
                UserRole.FISIO,
                UserRole.COORDINADOR
            )
        ),
        
        // MI EQUIPO - Solo staff técnico y coordinador
        NavItem(
            id = "mi_equipo",
            titleResId = R.string.nav_mi_equipo,
            icon = Icons.Filled.Group,
            requiredRoles = listOf(
                UserRole.ADMIN_ENTRENADOR,
                UserRole.ENTRENADOR,
                UserRole.SEGUNDO,
                UserRole.COORDINADOR
            )
        ),

        // PARTIDOS - Staff técnico y coordinador pueden gestionar, jugadores solo ver
        NavItem(
            id = "partidos",
            titleResId = R.string.nav_partidos,
            icon = Icons.Filled.Dashboard,
            requiredRoles = listOf(
                UserRole.ADMIN_ENTRENADOR,
                UserRole.ENTRENADOR,
                UserRole.SEGUNDO,
                UserRole.JUGADOR,
                UserRole.COORDINADOR
            )
        ),

        // JUGADORES - Staff técnico y fisio
        NavItem(
            id = "jugadores",
            titleResId = R.string.nav_jugadores,
            icon = Icons.Filled.People,
            requiredRoles = listOf(
                UserRole.ADMIN_ENTRENADOR,
                UserRole.ENTRENADOR,
                UserRole.SEGUNDO,
                UserRole.FISIO
            )
        ),

        // ALINEACIONES - Staff técnico puede gestionar, jugadores ver
        NavItem(
            id = "alineaciones",
            titleResId = R.string.nav_alineaciones,
            icon = Icons.Filled.AlignHorizontalLeft,
            requiredRoles = listOf(
                UserRole.ADMIN_ENTRENADOR,
                UserRole.ENTRENADOR,
                UserRole.SEGUNDO,
                UserRole.JUGADOR
            )
        ),

        // ESTADÍSTICAS - Todos pueden ver
        NavItem(
            id = "estadisticas",
            titleResId = R.string.nav_estadisticas,
            icon = Icons.Filled.BarChart,
            requiredRoles = listOf(
                UserRole.ADMIN_ENTRENADOR,
                UserRole.ENTRENADOR,
                UserRole.SEGUNDO,
                UserRole.JUGADOR,
                UserRole.FISIO,
                UserRole.COORDINADOR
            )
        ),

        // HISTORIAL - Todos pueden ver
        NavItem(
            id = "record",
            titleResId = R.string.nav_record,
            icon = Icons.Filled.Star,
            requiredRoles = listOf(
                UserRole.ADMIN_ENTRENADOR,
                UserRole.ENTRENADOR,
                UserRole.SEGUNDO,
                UserRole.JUGADOR,
                UserRole.FISIO,
                UserRole.COORDINADOR
            )
        ),

        // ELEMENTOS ADICIONALES - Solo entrenador
        NavItem(
            id = "elementos",
            titleResId = R.string.nav_elementos,
            icon = Icons.Filled.Settings,
            requiredRoles = listOf(
                UserRole.ADMIN_ENTRENADOR,
                UserRole.ENTRENADOR
            )
        ),

        // CAMPOS - Entrenador y coordinador
        NavItem(
            id = "campos",
            titleResId = R.string.nav_campos,
            icon = Icons.Filled.Place,
            requiredRoles = listOf(
                UserRole.ADMIN_ENTRENADOR,
                UserRole.ENTRENADOR,
                UserRole.COORDINADOR
            )
        ),

        // ASISTENTE IA - Todos pueden usar
        NavItem(
            id = "ai_assistant",
            titleResId = R.string.nav_ai_assistant,
            icon = Icons.Filled.SmartToy,
            requiredRoles = listOf(
                UserRole.ADMIN_ENTRENADOR,
                UserRole.ENTRENADOR,
                UserRole.SEGUNDO,
                UserRole.JUGADOR,
                UserRole.FISIO,
                UserRole.COORDINADOR
            )
        ),

        // GESTIÓN DE ROLES - Solo entrenador
        NavItem(
            id = "roles",
            titleResId = R.string.nav_roles,
            icon = Icons.Filled.AdminPanelSettings,
            requiredRoles = listOf(
                UserRole.ADMIN_ENTRENADOR,
                UserRole.ENTRENADOR
            )
        )
    )
    
    // Item de ajustes (siempre visible para todos)
    val settingsNavItem = NavItem(
        id = "ajustes",
        titleResId = R.string.nav_ajustes,
        icon = Icons.Filled.Settings,
        isSettings = true,
        requiredRoles = listOf(
            UserRole.ADMIN_ENTRENADOR,
            UserRole.ENTRENADOR,
            UserRole.SEGUNDO,
            UserRole.JUGADOR,
            UserRole.FISIO,
            UserRole.COORDINADOR
        )
    )
    
    /**
     * Obtener items de navegación filtrados por rol
     */
    fun getNavigationItemsForRole(userRole: UserRole?): List<NavItem> {
        if (userRole == null) {
            // Si no hay rol, mostrar solo items básicos
            return allNavItems.filter { 
                it.id in listOf("principal", "proximo", "estadisticas")
            }
        }
        
        return allNavItems.filter { navItem ->
            navItem.requiredRoles.contains(userRole)
        }
    }
    
    /**
     * Verificar si un usuario con cierto rol puede acceder a una pantalla
     */
    fun canAccessScreen(screenId: String, userRole: UserRole?): Boolean {
        if (userRole == null) return false
        
        val navItem = allNavItems.find { it.id == screenId }
        return navItem?.requiredRoles?.contains(userRole) ?: false
    }
    
    /**
     * Obtener la pantalla inicial según el rol
     */
    fun getDefaultScreenForRole(userRole: UserRole?): String {
        return when (userRole) {
            UserRole.ADMIN_ENTRENADOR -> "principal"
            UserRole.ENTRENADOR -> "principal"
            UserRole.SEGUNDO -> "principal"
            UserRole.JUGADOR -> "proximo" // Los jugadores ven primero su próximo partido
            UserRole.FISIO -> "jugadores" // El fisio ve primero los jugadores
            UserRole.COORDINADOR -> "campos" // El coordinador ve primero los campos
            null -> "principal"
        }
    }

    /**
     * Obtener descripción del rol para mostrar en UI
     */
    fun getRoleDescription(userRole: UserRole): String {
        return when (userRole) {
            UserRole.ADMIN_ENTRENADOR -> "Acceso administrativo completo y control total del sistema"
            UserRole.ENTRENADOR -> "Acceso completo a todas las funciones"
            UserRole.SEGUNDO -> "Gestión de jugadores, alineaciones y partidos"
            UserRole.JUGADOR -> "Visualización de partidos, alineaciones y estadísticas"
            UserRole.FISIO -> "Gestión del estado físico de los jugadores"
            UserRole.COORDINADOR -> "Gestión de campos y organización de partidos"
        }
    }
}

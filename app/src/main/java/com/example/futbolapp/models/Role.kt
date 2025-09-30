package com.example.futbolapp.models

enum class UserRole(val displayName: String, val permissions: List<Permission>) {
    ADMIN_ENTRENADOR(
        "Admin Entrenador",
        listOf(
            Permission.MANAGE_TEAM,
            Permission.MANAGE_PLAYERS,
            Permission.MANAGE_LINEUPS,
            Permission.MANAGE_MATCHES,
            Permission.VIEW_STATISTICS,
            Permission.MANAGE_ROLES,
            Permission.MANAGE_FIELDS,
            Permission.MANAGE_IMPROVEMENTS,
            Permission.ADMIN_ACCESS,
            Permission.DELETE_DATA,
            Permission.MANAGE_USERS
        )
    ),
    ENTRENADOR(
        "Entrenador",
        listOf(
            Permission.MANAGE_TEAM,
            Permission.MANAGE_PLAYERS,
            Permission.MANAGE_LINEUPS,
            Permission.MANAGE_MATCHES,
            Permission.VIEW_STATISTICS,
            Permission.MANAGE_ROLES,
            Permission.MANAGE_FIELDS,
            Permission.MANAGE_IMPROVEMENTS
        )
    ),
    SEGUNDO(
        "Segundo Entrenador",
        listOf(
            Permission.MANAGE_PLAYERS,
            Permission.MANAGE_LINEUPS,
            Permission.VIEW_STATISTICS,
            Permission.MANAGE_MATCHES
        )
    ),
    JUGADOR(
        "Jugador",
        listOf(
            Permission.VIEW_STATISTICS,
            Permission.VIEW_LINEUPS,
            Permission.VIEW_MATCHES
        )
    ),
    FISIO(
        "Fisioterapeuta",
        listOf(
            Permission.VIEW_PLAYERS,
            Permission.MANAGE_PLAYER_HEALTH,
            Permission.VIEW_STATISTICS
        )
    ),
    COORDINADOR(
        "Coordinador",
        listOf(
            Permission.MANAGE_FIELDS,
            Permission.MANAGE_MATCHES,
            Permission.VIEW_STATISTICS,
            Permission.VIEW_PLAYERS
        )
    );

    fun hasPermission(permission: Permission): Boolean {
        return permissions.contains(permission)
    }
}

enum class Permission {
    MANAGE_TEAM,           // Gestionar información del equipo
    MANAGE_PLAYERS,        // Añadir, editar, eliminar jugadores
    MANAGE_LINEUPS,        // Crear y modificar alineaciones
    MANAGE_MATCHES,        // Crear y gestionar partidos
    VIEW_STATISTICS,       // Ver estadísticas
    MANAGE_ROLES,          // Asignar roles a usuarios
    MANAGE_FIELDS,         // Gestionar campos de juego
    MANAGE_IMPROVEMENTS,   // Gestionar mejoras del equipo
    VIEW_LINEUPS,          // Solo ver alineaciones
    VIEW_MATCHES,          // Solo ver partidos
    VIEW_PLAYERS,          // Solo ver jugadores
    MANAGE_PLAYER_HEALTH,  // Gestionar estado físico de jugadores
    ADMIN_ACCESS,          // Acceso administrativo completo
    DELETE_DATA,           // Eliminar datos permanentemente
    MANAGE_USERS           // Gestionar usuarios del sistema
}

data class TeamMember(
    val userId: String = "",
    val teamId: String = "",
    val role: UserRole = UserRole.JUGADOR,
    val name: String = "",
    val email: String = "",
    val joinedAt: Long = System.currentTimeMillis()
)

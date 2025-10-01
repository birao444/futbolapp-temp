package com.example.futbolapp

object RoleManager {

    // Definición de roles
    const val ADMIN_ENTRENADOR = "admin_entrenador"
    const val ENTRENADOR = "entrenador"
    const val SEGUNDO = "segundo"
    const val JUGADOR = "jugador"
    const val FISIO = "fisio"
    const val COORDINADOR = "coordinador"
    const val PENDIENTE = "pendiente"

    // Permisos por rol
    private val permissions = mapOf(
        ADMIN_ENTRENADOR to setOf(
            Permission.CREATE_TEAM,
            Permission.EDIT_TEAM,
            Permission.DELETE_TEAM,
            Permission.MANAGE_PLAYERS,
            Permission.CREATE_LINEUPS,
            Permission.EDIT_LINEUPS,
            Permission.VIEW_STATISTICS,
            Permission.EDIT_STATISTICS,
            Permission.MANAGE_MATCHES,
            Permission.MANAGE_IMPROVEMENTS,
            Permission.MANAGE_FIELDS,
            Permission.ASSIGN_ROLES,
            Permission.VIEW_ALL_DATA
        ),
        ENTRENADOR to setOf(
            Permission.CREATE_TEAM,
            Permission.EDIT_TEAM,
            Permission.DELETE_TEAM,
            Permission.MANAGE_PLAYERS,
            Permission.CREATE_LINEUPS,
            Permission.EDIT_LINEUPS,
            Permission.VIEW_STATISTICS,
            Permission.EDIT_STATISTICS,
            Permission.MANAGE_MATCHES,
            Permission.MANAGE_IMPROVEMENTS,
            Permission.MANAGE_FIELDS,
            Permission.ASSIGN_ROLES,
            Permission.VIEW_ALL_DATA
        ),
        SEGUNDO to setOf(
            Permission.EDIT_TEAM,
            Permission.MANAGE_PLAYERS,
            Permission.CREATE_LINEUPS,
            Permission.EDIT_LINEUPS,
            Permission.VIEW_STATISTICS,
            Permission.EDIT_STATISTICS,
            Permission.MANAGE_MATCHES,
            Permission.MANAGE_IMPROVEMENTS,
            Permission.MANAGE_FIELDS,
            Permission.VIEW_ALL_DATA
        ),
        JUGADOR to setOf(
            Permission.VIEW_TEAM,
            Permission.VIEW_MATCHES,
            Permission.VIEW_STATISTICS,
            Permission.VIEW_LINEUPS
        ),
        FISIO to setOf(
            Permission.VIEW_TEAM,
            Permission.MANAGE_PLAYERS,
            Permission.VIEW_STATISTICS,
            Permission.EDIT_STATISTICS,
            Permission.VIEW_MATCHES
        ),
        COORDINADOR to setOf(
            Permission.VIEW_TEAM,
            Permission.MANAGE_MATCHES,
            Permission.MANAGE_FIELDS,
            Permission.VIEW_STATISTICS,
            Permission.VIEW_ALL_DATA
        ),
        PENDIENTE to setOf(
            Permission.NONE
        )
    )

    // Verificar si un rol tiene un permiso específico
    fun hasPermission(role: String, permission: Permission): Boolean {
        return permissions[role]?.contains(permission) ?: false
    }

    // Obtener todos los permisos de un rol
    fun getPermissions(role: String): Set<Permission> {
        return permissions[role] ?: emptySet()
    }

    // Verificar si un rol puede acceder a una pantalla específica
    fun canAccessScreen(role: String, screenId: String): Boolean {
        if (role == ADMIN_ENTRENADOR) return true
        return when (screenId) {
            "mi_equipo" -> hasPermission(role, Permission.VIEW_TEAM) || hasPermission(role, Permission.EDIT_TEAM)
            "jugadores" -> hasPermission(role, Permission.MANAGE_PLAYERS) || hasPermission(role, Permission.VIEW_TEAM)
            "alineaciones" -> hasPermission(role, Permission.CREATE_LINEUPS) || hasPermission(role, Permission.VIEW_LINEUPS)
            "estadisticas" -> hasPermission(role, Permission.VIEW_STATISTICS)
            "partidos" -> hasPermission(role, Permission.MANAGE_MATCHES) || hasPermission(role, Permission.VIEW_MATCHES)
            "campos" -> hasPermission(role, Permission.MANAGE_FIELDS)
            "elementos" -> hasPermission(role, Permission.MANAGE_IMPROVEMENTS)
            "record" -> hasPermission(role, Permission.VIEW_STATISTICS)
            "proximo_partido" -> hasPermission(role, Permission.VIEW_MATCHES)
            "roles" -> hasPermission(role, Permission.ASSIGN_ROLES)
            "ai_assistant" -> role != PENDIENTE
            else -> true // Pantallas públicas como principal, ajustes
        }
    }

    // Obtener el nivel jerárquico de un rol (para ordenamiento)
    fun getRoleLevel(role: String): Int {
        return when (role) {
            ENTRENADOR -> 5
            SEGUNDO -> 4
            COORDINADOR -> 3
            FISIO -> 3
            JUGADOR -> 2
            PENDIENTE -> 1
            else -> 0
        }
    }

    // Verificar si un rol puede ser asignado por otro rol
    fun canAssignRole(assignerRole: String, targetRole: String): Boolean {
        val assignerLevel = getRoleLevel(assignerRole)
        val targetLevel = getRoleLevel(targetRole)
        return assignerLevel >= 4 && assignerLevel > targetLevel // Solo entrenador y segundo pueden asignar roles
    }
}

// Definición de permisos
enum class Permission {
    NONE,
    VIEW_TEAM,
    EDIT_TEAM,
    CREATE_TEAM,
    DELETE_TEAM,
    MANAGE_PLAYERS,
    VIEW_MATCHES,
    MANAGE_MATCHES,
    VIEW_LINEUPS,
    CREATE_LINEUPS,
    EDIT_LINEUPS,
    VIEW_STATISTICS,
    EDIT_STATISTICS,
    MANAGE_IMPROVEMENTS,
    MANAGE_FIELDS,
    ASSIGN_ROLES,
    VIEW_ALL_DATA
}

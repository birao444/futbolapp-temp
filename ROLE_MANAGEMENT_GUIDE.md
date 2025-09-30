# Guía de Gestión de Roles - FutbolApp

## 📋 Descripción General

El sistema de roles permite gestionar diferentes niveles de acceso y permisos dentro de un equipo de fútbol. Cada usuario puede tener un rol específico que determina qué acciones puede realizar en la aplicación.

## 👥 Roles Disponibles

### 1. **ENTRENADOR** (Máximo nivel de permisos)
- **Permisos completos:**
  - ✅ Gestionar información del equipo
  - ✅ Añadir, editar y eliminar jugadores
  - ✅ Crear y modificar alineaciones
  - ✅ Gestionar partidos
  - ✅ Ver estadísticas
  - ✅ **Asignar y modificar roles de otros usuarios**
  - ✅ Gestionar campos de juego
  - ✅ Gestionar mejoras del equipo

### 2. **SEGUNDO** (Segundo Entrenador)
- **Permisos:**
  - ✅ Gestionar jugadores
  - ✅ Crear y modificar alineaciones
  - ✅ Ver estadísticas
  - ✅ Gestionar partidos
  - ❌ No puede asignar roles

### 3. **JUGADOR**
- **Permisos limitados:**
  - ✅ Ver estadísticas
  - ✅ Ver alineaciones
  - ✅ Ver partidos
  - ❌ No puede editar información

### 4. **FISIO** (Fisioterapeuta)
- **Permisos especializados:**
  - ✅ Ver jugadores
  - ✅ Gestionar estado físico de jugadores
  - ✅ Ver estadísticas
  - ❌ No puede modificar alineaciones

### 5. **COORDINADOR**
- **Permisos administrativos:**
  - ✅ Gestionar campos de juego
  - ✅ Gestionar partidos
  - ✅ Ver estadísticas
  - ✅ Ver jugadores
  - ❌ No puede modificar alineaciones

## 🔧 Cómo Funciona el Sistema

### Estructura en Firebase Firestore

```
team_members/
  └── {userId}-{teamId}/
      ├── userId: "abc123"
      ├── teamId: "team456"
      ├── role: "ENTRENADOR"
      ├── name: "Juan Pérez"
      ├── email: "juan@example.com"
      └── joinedAt: 1234567890
```

### Flujo de Asignación de Roles

1. **Usuario se registra** → Firebase Authentication crea la cuenta
2. **Usuario se une a un equipo** → Se crea un documento en `team_members`
3. **Entrenador asigna rol** → Se actualiza el campo `role` en Firestore
4. **Usuario accede a funciones** → La app verifica permisos antes de cada acción

## 💻 Uso en el Código

### 1. Asignar un Rol

```kotlin
val roleManager = RoleManager()

// Asignar rol de jugador a un usuario
roleManager.assignRole(
    userId = "user123",
    teamId = "team456",
    role = UserRole.JUGADOR,
    name = "Carlos López",
    email = "carlos@example.com"
)
```

### 2. Verificar Permisos

```kotlin
// Verificar si un usuario puede gestionar jugadores
val canManagePlayers = roleManager.hasPermission(
    userId = "user123",
    teamId = "team456",
    permission = Permission.MANAGE_PLAYERS
)

if (canManagePlayers) {
    // Permitir editar jugadores
} else {
    // Mostrar mensaje de acceso denegado
}
```

### 3. Obtener Rol de Usuario

```kotlin
val userRole = roleManager.getUserRole(
    userId = "user123",
    teamId = "team456"
)

when (userRole) {
    UserRole.ENTRENADOR -> {
        // Mostrar opciones de administración
    }
    UserRole.JUGADOR -> {
        // Mostrar vista limitada
    }
    else -> {
        // Manejar otros roles
    }
}
```

### 4. Listar Miembros del Equipo

```kotlin
val teamMembers = roleManager.getTeamMembers("team456")

teamMembers.forEach { member ->
    println("${member.name} - ${member.role.displayName}")
}
```

### 5. Cambiar Rol de Usuario

```kotlin
// Solo el entrenador puede hacer esto
roleManager.updateRole(
    userId = "user123",
    teamId = "team456",
    newRole = UserRole.SEGUNDO
)
```

### 6. Eliminar Miembro del Equipo

```kotlin
roleManager.removeMember(
    userId = "user123",
    teamId = "team456"
)
```

## 🎨 Interfaz de Usuario

### Pantalla de Gestión de Roles

La pantalla `RoleManagementScreen` proporciona:

1. **Tarjeta de Información del Rol Actual**
   - Muestra tu rol y permisos

2. **Lista de Miembros del Equipo**
   - Nombre, email y rol de cada miembro
   - Iconos distintivos por rol

3. **Botones de Acción** (solo para Entrenador)
   - ➕ Añadir nuevo miembro
   - ✏️ Editar rol de miembro
   - 🗑️ Eliminar miembro

### Uso en MainActivity

```kotlin
// En tu navegación
composable("role_management/{teamId}") { backStackEntry ->
    val teamId = backStackEntry.arguments?.getString("teamId") ?: ""
    val currentUserId = authManager.currentUser?.uid ?: ""
    
    RoleManagementScreen(
        teamId = teamId,
        currentUserId = currentUserId
    )
}
```

## 🔐 Seguridad y Mejores Prácticas

### 1. Validación en el Cliente
```kotlin
// Siempre verificar permisos antes de mostrar UI
if (currentUserRole == UserRole.ENTRENADOR) {
    // Mostrar botón de gestión
}
```

### 2. Validación en el Servidor (Firestore Rules)
```javascript
// firestore.rules
match /team_members/{memberId} {
  allow read: if request.auth != null;
  allow write: if request.auth != null && 
    get(/databases/$(database)/documents/team_members/$(request.auth.uid + '-' + resource.data.teamId)).data.role == 'ENTRENADOR';
}
```

### 3. Manejo de Errores
```kotlin
try {
    val result = roleManager.assignRole(...)
    if (result.isSuccess) {
        // Éxito
    } else {
        // Manejar error
    }
} catch (e: Exception) {
    // Manejar excepción
}
```

## 📱 Ejemplo de Flujo Completo

### Escenario: Crear un equipo y asignar roles

```kotlin
// 1. Usuario se registra
val user = authManager.signUp("entrenador@team.com", "password123")

// 2. Crear equipo
val teamId = firestoreManager.createOrUpdateTeam(
    teamId = UUID.randomUUID().toString(),
    data = mapOf(
        "name" to "FC Barcelona",
        "userId" to user.uid
    )
)

// 3. Asignar rol de entrenador al creador
roleManager.assignRole(
    userId = user.uid,
    teamId = teamId,
    role = UserRole.ENTRENADOR,
    name = "Pep Guardiola",
    email = "entrenador@team.com"
)

// 4. Invitar y asignar roles a otros miembros
roleManager.assignRole(
    userId = "user2",
    teamId = teamId,
    role = UserRole.SEGUNDO,
    name = "Tito Vilanova",
    email = "segundo@team.com"
)

roleManager.assignRole(
    userId = "user3",
    teamId = teamId,
    role = UserRole.FISIO,
    name = "Dr. Pruna",
    email = "fisio@team.com"
)

// 5. Añadir jugadores
for (i in 1..11) {
    roleManager.assignRole(
        userId = "player$i",
        teamId = teamId,
        role = UserRole.JUGADOR,
        name = "Jugador $i",
        email = "jugador$i@team.com"
    )
}
```

## 🔄 Integración con ViewModel

```kotlin
class MyTeamViewModel : ViewModel() {
    private val roleManager = RoleManager()
    
    fun checkPermissionAndExecute(
        userId: String,
        teamId: String,
        permission: Permission,
        action: () -> Unit
    ) {
        viewModelScope.launch {
            val hasPermission = roleManager.hasPermission(userId, teamId, permission)
            if (hasPermission) {
                action()
            } else {
                // Mostrar mensaje de error
                _errorMessage.value = "No tienes permisos para esta acción"
            }
        }
    }
}
```

## 📊 Casos de Uso Comunes

### 1. Proteger Edición de Jugadores
```kotlin
if (roleManager.hasPermission(userId, teamId, Permission.MANAGE_PLAYERS)) {
    // Mostrar formulario de edición
} else {
    // Mostrar vista de solo lectura
}
```

### 2. Filtrar Opciones del Menú
```kotlin
val menuItems = buildList {
    add(MenuItem("Ver Equipo", Icons.Default.Group))
    
    if (userRole == UserRole.ENTRENADOR || userRole == UserRole.SEGUNDO) {
        add(MenuItem("Gestionar Alineaciones", Icons.Default.Dashboard))
    }
    
    if (userRole == UserRole.ENTRENADOR) {
        add(MenuItem("Gestionar Roles", Icons.Default.Settings))
    }
}
```

### 3. Notificaciones por Rol
```kotlin
when (userRole) {
    UserRole.ENTRENADOR -> {
        // Notificar sobre todo
    }
    UserRole.JUGADOR -> {
        // Solo notificar sobre partidos y convocatorias
    }
    UserRole.FISIO -> {
        // Notificar sobre lesiones
    }
}
```

## 🎯 Próximos Pasos

1. **Implementar invitaciones por email**
   - Enviar invitación a usuarios no registrados
   - Link de invitación con token

2. **Sistema de solicitudes**
   - Usuarios pueden solicitar unirse a un equipo
   - Entrenador aprueba o rechaza

3. **Historial de cambios de roles**
   - Registrar quién cambió qué rol y cuándo

4. **Roles personalizados**
   - Permitir crear roles custom con permisos específicos

5. **Jerarquía de equipos**
   - Equipos juveniles, reserva, primer equipo
   - Roles diferentes en cada categoría

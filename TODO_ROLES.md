# TODO - Sistema de Roles Implementado

## ✅ Completado

### Modelos de Datos
- [x] Crear enum `UserRole` con 5 roles (ENTRENADOR, SEGUNDO, JUGADOR, FISIO, COORDINADOR)
- [x] Crear enum `Permission` con todos los permisos necesarios
- [x] Crear data class `TeamMember` para representar miembros del equipo

### Firebase Backend
- [x] Crear `RoleManager` para gestionar roles en Firestore
- [x] Implementar método `assignRole()` - Asignar rol a usuario
- [x] Implementar método `getUserRole()` - Obtener rol de usuario
- [x] Implementar método `getTeamMember()` - Obtener información completa del miembro
- [x] Implementar método `getTeamMembers()` - Listar todos los miembros
- [x] Implementar método `getMembersByRole()` - Filtrar por rol
- [x] Implementar método `updateRole()` - Cambiar rol de usuario
- [x] Implementar método `removeMember()` - Eliminar miembro del equipo
- [x] Implementar método `hasPermission()` - Verificar permisos
- [x] Implementar método `isCoach()` - Verificar si es entrenador
- [x] Implementar método `getUserTeams()` - Obtener equipos del usuario

### ViewModel
- [x] Crear `RoleViewModel` para gestión de estado
- [x] Implementar StateFlows para teamMembers, currentUserRole, loading, messages
- [x] Implementar métodos para cargar datos
- [x] Implementar métodos para asignar/actualizar/eliminar roles
- [x] Implementar verificación de permisos

### UI
- [x] Crear `RoleManagementScreen` - Pantalla principal de gestión
- [x] Crear `RoleInfoCard` - Tarjeta con información del rol actual
- [x] Crear `MemberCard` - Tarjeta para cada miembro del equipo
- [x] Crear `AddMemberDialog` - Diálogo para añadir miembros
- [x] Crear `EditRoleDialog` - Diálogo para cambiar roles
- [x] Implementar iconos distintivos por rol

### Documentación
- [x] Crear `ROLE_MANAGEMENT_GUIDE.md` - Guía completa de uso
- [x] Documentar todos los roles y permisos
- [x] Incluir ejemplos de código
- [x] Documentar casos de uso comunes

### Seguridad
- [x] Crear `firestore.rules` con reglas de seguridad
- [x] Implementar validación de permisos por rol
- [x] Proteger operaciones sensibles (solo entrenador)

## 📋 Pendiente de Implementar

### Integración con MainActivity
- [x] Añadir navegación a RoleManagementScreen en MainActivity
- [x] Integrar RoleViewModel en las pantallas existentes
- [x] Añadir verificación de permisos en cada pantalla
- [x] Implementar menús dinámicos basados en roles
- [x] Mostrar información del rol en el drawer
- [x] Pantalla inicial personalizada según rol

### Sistema de Invitaciones
- [ ] Crear sistema de invitaciones por email
- [ ] Implementar tokens de invitación
- [ ] Crear pantalla de aceptar invitación
- [ ] Enviar notificaciones de invitación

### Búsqueda de Usuarios
- [ ] Implementar búsqueda de usuarios por email
- [ ] Crear índice en Firestore para búsquedas
- [ ] Añadir autocompletado en AddMemberDialog

### Mejoras de UI
- [ ] Añadir Snackbar para mensajes de éxito/error
- [ ] Implementar animaciones en cambios de rol
- [ ] Añadir confirmación antes de eliminar miembro
- [ ] Mejorar diseño responsive

### Notificaciones
- [ ] Notificar cuando se asigna un rol
- [ ] Notificar cuando se cambia un rol
- [ ] Notificar cuando se elimina del equipo

### Historial
- [ ] Crear colección `role_history` en Firestore
- [ ] Registrar todos los cambios de roles
- [ ] Crear pantalla de historial de cambios
- [ ] Mostrar quién hizo cada cambio y cuándo

### Validaciones Adicionales
- [ ] Validar que siempre haya al menos un entrenador
- [ ] Impedir que el entrenador se elimine a sí mismo
- [ ] Validar formato de email en AddMemberDialog
- [ ] Añadir límite de miembros por equipo

### Testing
- [ ] Crear tests unitarios para RoleManager
- [ ] Crear tests para RoleViewModel
- [ ] Crear tests de UI para RoleManagementScreen
- [ ] Probar reglas de seguridad de Firestore

### Características Avanzadas
- [ ] Roles personalizados (custom roles)
- [ ] Permisos granulares por usuario
- [ ] Jerarquía de equipos (juvenil, reserva, primer equipo)
- [ ] Transferencia de propiedad del equipo
- [ ] Roles temporales (ej: capitán por un partido)

## 🔧 Configuración Necesaria

### Firebase Console
- [ ] Subir `firestore.rules` a Firebase Console
- [ ] Crear índices compuestos si es necesario
- [ ] Configurar notificaciones push (opcional)

### Archivo google-services.json
- [ ] Descargar desde Firebase Console
- [ ] Colocar en carpeta `app/`
- [ ] Añadir a .gitignore

### Build Configuration
- [ ] Verificar que google-services plugin esté configurado
- [ ] Verificar dependencias de Firebase en build.gradle.kts

## 📝 Notas de Implementación

### Próximos Pasos Inmediatos
1. Integrar RoleManagementScreen en la navegación principal
2. Añadir verificación de permisos en pantallas existentes
3. Implementar sistema de búsqueda de usuarios
4. Subir reglas de Firestore a Firebase Console
5. Probar flujo completo de asignación de roles

### Consideraciones Importantes
- El sistema está diseñado para un equipo por usuario inicialmente
- Se puede extender para múltiples equipos por usuario
- Las reglas de Firestore deben ser probadas exhaustivamente
- Considerar implementar caché local para roles (mejor performance)

### Mejoras de Performance
- Implementar caché de roles en DataStore
- Usar listeners de Firestore para actualizaciones en tiempo real
- Optimizar queries con índices compuestos
- Implementar paginación en lista de miembros (si hay muchos)

## 🎯 Prioridades

### Alta Prioridad
1. Integración con MainActivity y navegación
2. Verificación de permisos en pantallas existentes
3. Sistema de búsqueda de usuarios
4. Subir reglas de Firestore

### Media Prioridad
1. Sistema de invitaciones
2. Notificaciones
3. Mejoras de UI
4. Historial de cambios

### Baja Prioridad
1. Roles personalizados
2. Jerarquía de equipos
3. Características avanzadas
4. Optimizaciones de performance

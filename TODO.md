j# TODO: Implementación de la Interfaz de Fútbol App

## Pasos del Plan Aprobado

1. **[Completado]** Crear el paquete de navegación y el archivo DrawerContent.kt:
   - Directorio creado: app/src/main/java/com/example/futbolapp/ui/navigation/
   - Archivo creado: app/src/main/java/com/example/futbolapp/ui/navigation/DrawerContent.kt
   - Implementado: Composable NavigationDrawerContent con ítems en español usando iconos custom de drawable.

2. **[Completado]** Actualizar strings.xml:
   - Editado app/src/main/res/values/strings.xml
   - Agregados strings en español para títulos de ítems (proximo_partido, mi_equipo, etc.) y placeholders de contenido (contenido_mi_equipo, etc.).

3. **[Completado]** Crear iconos en drawable:
   - Creados 9 archivos XML vectoriales en app/src/main/res/drawable/: ic_proximo_partido.xml, ic_mi_equipo.xml, ic_partidos.xml, ic_jugadores.xml, ic_alineaciones.xml, ic_estadisticas.xml, ic_record.xml, ic_elementos.xml, ic_campos.xml.
   - Vectores simples con colores aproximados (rojo para la mayoría, verde para campos) para representar los elementos de la imagen.

4. **[Completado]** Editar MainActivity.kt:
   - Actualizado el setContent para incluir NavigationDrawerContent, Scaffold con TopAppBar y lógica de estado para selección de ítems.
   - Integrado DrawerContent con estados remember y mutableStateOf para manejar la selección.
   - Contenido dinámico con when para mostrar placeholders basados en el ítem seleccionado, usando strings en español.
   - Removidas funciones obsoletas (Greeting y Preview).

5. **[Pendiente]** Pasos de verificación:
   - Compilar el proyecto (en Android Studio: Build > Make Project).
   - Ejecutar la app en emulador/dispositivo (Run > Run 'app').
   - Probar: Abrir drawer (deslizar izquierda), seleccionar ítems y verificar cambio de contenido.
   - Si hay errores, corregir y actualizar TODO.md.

**Notas:**
- Todo en Jetpack Compose para mantener consistencia.
- Funcionalidad básica: UI estática con placeholders; no datos reales por ahora.
- Actualizaciones: Marcar [Completado] al finalizar cada paso.

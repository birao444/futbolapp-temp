// Top-level build file where you can add configuration options common to all sub-projects/modules.

// Bloque 'plugins' para definir versiones de plugins globales
plugins {
    id("com.android.application") version "8.2.2" apply false // O la última versión estable de AGP
    id("com.android.library") version "8.2.2" apply false // Si tienes módulos de librería
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false // O la última versión estable de Kotlin
    id("com.google.gms.google-services") version "4.4.1" apply false // Si usas Firebase (revisa la última)
    // Añade aquí otros plugins a nivel de proyecto si los tienes, ej. Hilt, Safe Args, etc.
    // id 'com.google.dagger.hilt.android' version '2.51' apply false // Ejemplo Hilt
}

// El bloque 'allprojects { repositories { ... } }' ha sido eliminado.

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

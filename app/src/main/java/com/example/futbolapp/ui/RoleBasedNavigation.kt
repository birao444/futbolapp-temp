package com.example.futbolapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.futbolapp.Permission
import com.example.futbolapp.RoleManager
import com.example.futbolapp.viewmodels.RoleViewModel

@Composable
fun RoleBasedNavigation(
    roleViewModel: RoleViewModel = viewModel(),
    content: @Composable (userRole: String, canAccessScreen: (String) -> Boolean) -> Unit
) {
    val userRole by roleViewModel.userRole.collectAsState()
    val isLoading by roleViewModel.isLoading.collectAsState()

    if (isLoading) {
        // Mostrar loading mientras se carga el rol
        LoadingScreen()
    } else {
        content(userRole) { screenId ->
            roleViewModel.canAccessScreen(screenId)
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

// Función helper para verificar permisos en composables
@Composable
fun hasPermission(permission: com.example.futbolapp.Permission, roleViewModel: RoleViewModel = viewModel()): Boolean {
    return roleViewModel.hasPermission(permission)
}

// Función helper para verificar acceso a pantallas
@Composable
fun canAccessScreen(screenId: String, roleViewModel: RoleViewModel = viewModel()): Boolean {
    return roleViewModel.canAccessScreen(screenId)
}

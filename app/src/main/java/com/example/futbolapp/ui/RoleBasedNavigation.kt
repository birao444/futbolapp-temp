package com.example.futbolapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.futbolapp.RoleManager
import com.example.futbolapp.viewmodels.AuthViewModel
import com.example.futbolapp.viewmodels.RoleViewModel

@Composable
fun RoleBasedNavigation(
    authViewModel: AuthViewModel = viewModel(),
    roleViewModel: RoleViewModel = viewModel(),
    content: @Composable (userRole: String, canAccessScreen: (String) -> Boolean) -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val userRole by roleViewModel.userRole.collectAsState()
    val isLoading by roleViewModel.isLoading.collectAsState()

    // Si no hay usuario autenticado, no mostrar nada (debería manejarse en MainActivity)
    if (currentUser == null) {
        return
    }

    if (isLoading) {
        // Mostrar loading mientras se carga el rol
        LoadingScreen()
    } else {
        content(userRole) { screenId ->
            RoleManager.canAccessScreen(userRole, screenId)
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

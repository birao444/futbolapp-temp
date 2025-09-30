package com.example.futbolapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.futbolapp.viewmodels.AuthViewModel
import com.example.futbolapp.viewmodels.AuthState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isSignUp) "Registrarse" else "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isSignUp) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (isSignUp) {
                    authViewModel.signUp(email, password, name)
                } else {
                    authViewModel.signIn(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSignUp) "Registrarse" else "Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { isSignUp = !isSignUp }) {
            Text(if (isSignUp) "¿Ya tienes cuenta? Inicia sesión" else "¿No tienes cuenta? Regístrate")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (authState) {
            is AuthState.Loading -> CircularProgressIndicator()
            is AuthState.Error -> Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
            else -> {}
        }
    }
}

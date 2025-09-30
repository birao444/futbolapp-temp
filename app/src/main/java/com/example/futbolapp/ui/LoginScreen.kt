package com.example.futbolapp.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.futbolapp.viewmodels.AuthViewModel
import com.example.futbolapp.ui.theme.FutbolAppTheme

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

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
        Spacer(modifier = Modifier.height(16.dp))

        if (isSignUp) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                if (isSignUp) {
                    authViewModel.signUp(email, password, name,
                        onSuccess = {
                            isLoading = false
                            Log.d("LoginScreen", "Registro exitoso")
                            onLoginSuccess()
                        },
                        onError = { errorMsg ->
                            isLoading = false
                            Toast.makeText(context, "Error registro: $errorMsg", Toast.LENGTH_LONG).show()
                            Log.e("LoginScreen", "Error en signUp: $errorMsg")
                        }
                    )
                } else {
                    authViewModel.signIn(email, password,
                        onSuccess = {
                            isLoading = false
                            Log.d("LoginScreen", "Inicio de sesión exitoso")
                            onLoginSuccess()
                        },
                        onError = { errorMsg ->
                            isLoading = false
                            Toast.makeText(context, "Error inicio sesión: $errorMsg", Toast.LENGTH_LONG).show()
                            Log.e("LoginScreen", "Error en signIn: $errorMsg")
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text(if (isSignUp) "Crear Cuenta" else "Acceder")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isSignUp) "¿Ya tienes cuenta? Inicia Sesión" else "¿No tienes cuenta? Regístrate",
            modifier = Modifier.clickable { isSignUp = !isSignUp }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FutbolAppTheme {
        LoginScreen(onLoginSuccess = {})
    }
}

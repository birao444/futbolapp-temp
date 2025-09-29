package com.example.futbolapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.futbolapp.R
import com.example.futbolapp.ui.navigation.NavigationDrawerContent
import com.example.futbolapp.ui.screens.*
import com.example.futbolapp.ui.theme.FutbolAppTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FutbolAppTheme {
                val navController = rememberNavController()

                NavigationDrawerContent(navController = navController) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text(stringResource(R.string.app_name)) },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("home") {
                                // Default screen or welcome
                                androidx.compose.foundation.layout.Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Bienvenido a FutbolApp. Desliza desde la izquierda para abrir el menú.",
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }
                            }
                            composable("proximo") { ProximoPartidoScreen() }
                            composable("mi_equipo") { MiEquipoScreen() }
                            composable("partidos") { PartidosScreen() }
                            composable("jugadores") { JugadoresScreen() }
                            composable("alineaciones") { AlineacionesScreen() }
                            composable("estadisticas") { EstadisticasScreen() }
                            composable("record") { RecordScreen() }
                            composable("elementos") { ElementosScreen() }
                            composable("campos") { CamposScreen() }
                        }
                    }
                }
            }
        }
    }
}

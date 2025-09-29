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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.futbolapp.R
import com.example.futbolapp.ui.navigation.NavItem
import com.example.futbolapp.ui.navigation.NavigationDrawerContent
import com.example.futbolapp.ui.theme.FutbolAppTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FutbolAppTheme {
                var selectedItem by remember { mutableStateOf<NavItem?>(null) }
                val onItemSelected: (NavItem) -> Unit = { item ->
                    selectedItem = item
                }

                NavigationDrawerContent(
                    selectedItem = selectedItem,
                    onItemSelected = onItemSelected
                ) {
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
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = when (selectedItem?.id) {
                                    "proximo" -> stringResource(R.string.contenido_proximo)
                                    "mi_equipo" -> stringResource(R.string.contenido_mi_equipo)
                                    "partidos" -> stringResource(R.string.contenido_partidos)
                                    "jugadores" -> stringResource(R.string.contenido_jugadores)
                                    "alineaciones" -> stringResource(R.string.contenido_alineaciones)
                                    "estadisticas" -> stringResource(R.string.contenido_estadisticas)
                                    "record" -> stringResource(R.string.contenido_record)
                                    "elementos" -> stringResource(R.string.contenido_elementos)
                                    "campos" -> stringResource(R.string.contenido_campos)
                                    else -> "Bienvenido a FutbolApp. Desliza desde la izquierda para abrir el menú."
                                },
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

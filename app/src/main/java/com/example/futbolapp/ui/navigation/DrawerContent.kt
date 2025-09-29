package com.example.futbolapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.futbolapp.R
import kotlinx.coroutines.launch

data class NavItem(
    val id: String,
    val title: String,
    val iconRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerContent(
    navController: androidx.navigation.NavController,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val items = listOf(
        NavItem("proximo", stringResource(R.string.proximo_partido), R.drawable.ic_proximo_partido),
        NavItem("mi_equipo", stringResource(R.string.mi_equipo), R.drawable.ic_mi_equipo),
        NavItem("partidos", stringResource(R.string.partidos), R.drawable.ic_partidos),
        NavItem("jugadores", stringResource(R.string.jugadores), R.drawable.ic_jugadores),
        NavItem("alineaciones", stringResource(R.string.alineaciones), R.drawable.ic_alineaciones),
        NavItem("estadisticas", stringResource(R.string.estadisticas), R.drawable.ic_estadisticas),
        NavItem("record", stringResource(R.string.record), R.drawable.ic_record),
        NavItem("elementos", stringResource(R.string.elementos), R.drawable.ic_elementos),
        NavItem("campos", stringResource(R.string.campos), R.drawable.ic_campos)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                items(count = items.size) { index ->
                    val item = items[index]
                    NavigationDrawerItem(
                        icon = { Icon(painter = painterResource(item.iconRes), contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch {
                                drawerState.close()
                            }
                            navController.navigate(item.id)
                        },
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        },
        content = content
    )
}

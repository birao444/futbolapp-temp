@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.futbolapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.futbolapp.models.TeamMember
import com.example.futbolapp.models.UserRole
import com.example.futbolapp.viewmodel.RoleViewModel
@Composable
fun RoleManagementScreen(
    teamId: String,
    currentUserId: String,
    viewModel: RoleViewModel = viewModel()
) {
    val teamMembers by viewModel.teamMembers.collectAsState()
    val currentUserRole by viewModel.currentUserRole.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var showAddMemberDialog by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf<TeamMember?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(teamId) {
        viewModel.loadTeamMembers(teamId)
        viewModel.loadCurrentUserRole(currentUserId, teamId)
    }

    // Mostrar mensajes
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            // Aquí podrías mostrar un Snackbar
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            // Aquí podrías mostrar un Snackbar
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Roles") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            // Solo el entrenador puede añadir miembros
            if (currentUserRole == UserRole.ENTRENADOR) {
                FloatingActionButton(
                    onClick = { showAddMemberDialog = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir miembro")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Información del rol actual
                currentUserRole?.let { role ->
                    RoleInfoCard(role)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Lista de miembros
                Text(
                    text = "Miembros del Equipo (${teamMembers.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(teamMembers) { member ->
                        MemberCard(
                            member = member,
                            canEdit = currentUserRole == UserRole.ENTRENADOR,
                            onEdit = {
                                selectedMember = member
                                showEditDialog = true
                            },
                            onRemove = {
                                viewModel.removeMember(member.userId, teamId)
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo para añadir miembro
    if (showAddMemberDialog) {
        AddMemberDialog(
            onDismiss = { showAddMemberDialog = false },
            onConfirm = { email, name, role ->
                // Aquí necesitarías buscar el userId por email
                // Por ahora usamos un placeholder
                viewModel.assignRole("userId", teamId, role, name, email)
                showAddMemberDialog = false
            }
        )
    }

    // Diálogo para editar rol
    if (showEditDialog && selectedMember != null) {
        EditRoleDialog(
            member = selectedMember!!,
            onDismiss = {
                showEditDialog = false
                selectedMember = null
            },
            onConfirm = { newRole ->
                viewModel.updateRole(selectedMember!!.userId, teamId, newRole)
                showEditDialog = false
                selectedMember = null
            }
        )
    }
}

@Composable
fun RoleInfoCard(role: UserRole) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getRoleIcon(role),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Tu Rol",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = role.displayName,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Permisos:",
                style = MaterialTheme.typography.labelSmall
            )
            role.permissions.forEach { permission ->
                Text(
                    text = "• ${permission.name.replace("_", " ")}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun MemberCard(
    member: TeamMember,
    canEdit: Boolean,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = getRoleIcon(member.role),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = member.name,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = member.role.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = member.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (canEdit) {
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = onRemove) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberDialog(
    onDismiss: () -> Unit,
    onConfirm: (email: String, name: String, role: UserRole) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.JUGADOR) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Miembro") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedRole.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rol") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        UserRole.values().forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.displayName) },
                                onClick = {
                                    selectedRole = role
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (email.isNotBlank() && name.isNotBlank()) {
                        onConfirm(email, name, selectedRole)
                    }
                }
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRoleDialog(
    member: TeamMember,
    onDismiss: () -> Unit,
    onConfirm: (UserRole) -> Unit
) {
    var selectedRole by remember { mutableStateOf(member.role) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar Rol") },
        text = {
            Column {
                Text("Cambiar rol de ${member.name}")
                Spacer(modifier = Modifier.height(16.dp))
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedRole.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Nuevo Rol") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        UserRole.values().forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.displayName) },
                                onClick = {
                                    selectedRole = role
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedRole) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

fun getRoleIcon(role: UserRole): ImageVector {
    return when (role) {
        UserRole.ENTRENADOR -> Icons.Default.Star
        UserRole.SEGUNDO -> Icons.Default.Person
        UserRole.JUGADOR -> Icons.Default.Group
        UserRole.FISIO -> Icons.Default.Favorite
        UserRole.COORDINADOR -> Icons.Default.Settings
    }
}

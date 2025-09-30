package com.example.futbolapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.futbolapp.R
import com.example.futbolapp.ai.AIAssistant
import com.example.futbolapp.models.UserRole
import com.example.futbolapp.viewmodel.RoleViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    roleViewModel: RoleViewModel = viewModel()
) {
    val aiAssistant = remember { AIAssistant() }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var messages by remember { mutableStateOf(listOf<Message>()) }
    var inputText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var currentUserRole by remember { mutableStateOf<UserRole?>(null) }

    // Obtener el rol del usuario actual
    LaunchedEffect(Unit) {
        currentUserRole = roleViewModel.currentUserRole.value
    }

    // Auto-scroll al último mensaje
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = stringResource(R.string.ai_assistant_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = stringResource(R.string.ai_assistant_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Mensajes
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message = message)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isProcessing) {
                item {
                    ProcessingIndicator()
                }
            }
        }

        // Input area
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.ai_placeholder)) },
                    enabled = !isProcessing,
                    maxLines = 3,
                    shape = RoundedCornerShape(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() && !isProcessing) {
                            coroutineScope.launch {
                                val userMessage = Message(inputText, MessageType.USER)
                                messages = messages + userMessage
                                inputText = ""
                                isProcessing = true

                                try {
                                    // Procesar el mensaje con IA
                                    val parsedInfo = aiAssistant.processNaturalLanguage(userMessage.content, currentUserRole)
                                    val aiResponse = aiAssistant.generateResponse(parsedInfo, currentUserRole)

                                    val aiMessage = Message(aiResponse, MessageType.AI)
                                    messages = messages + aiMessage

                                    // Si es una acción ejecutable, mostrar confirmación
                                    if (parsedInfo.taskType != AIAssistant.TaskType.GENERAL_QUERY) {
                                        val confirmationMessage = Message(
                                            "¿Quieres que ejecute esta acción?",
                                            MessageType.AI,
                                            isAction = true,
                                            actionData = parsedInfo
                                        )
                                        messages = messages + confirmationMessage
                                    }
                                } catch (e: Exception) {
                                    val errorMessage = Message(
                                        "Lo siento, hubo un error procesando tu mensaje. Inténtalo de nuevo.",
                                        MessageType.AI
                                    )
                                    messages = messages + errorMessage
                                } finally {
                                    isProcessing = false
                                }
                            }
                        }
                    },
                    enabled = inputText.isNotBlank() && !isProcessing
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = stringResource(R.string.send_message),
                        tint = if (inputText.isNotBlank() && !isProcessing)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Ejemplos de uso
        if (messages.isEmpty()) {
            ExamplesSection(onExampleClick = { example ->
                inputText = example
            })
        }
    }
}

@Composable
private fun MessageBubble(message: Message) {
    val isUser = message.type == MessageType.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // Avatar de IA
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "🤖",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = if (isUser) 16.dp else 4.dp,
                        topEnd = if (isUser) 4.dp else 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .background(
                    if (isUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.content,
                color = if (isUser) MaterialTheme.colorScheme.onPrimary
                       else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )

            if (message.isAction) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* TODO: Ejecutar acción */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sí", style = MaterialTheme.typography.labelSmall)
                    }
                    OutlinedButton(
                        onClick = { /* TODO: Cancelar */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("No", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            // Avatar de usuario
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.secondary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "👤",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ProcessingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Procesando...",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ExamplesSection(onExampleClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Ejemplos de uso:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val examples = listOf(
            "Tengo un partido el 20 de este mes a las 17:00 contra Real Madrid",
            "Añadir jugador Juan Pérez al equipo",
            "Programar entrenamiento mañana a las 10:00",
            "Actualizar estado de lesión de Pedro García"
        )

        examples.forEach { example ->
            OutlinedButton(
                onClick = { onExampleClick(example) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = example,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

data class Message(
    val content: String,
    val type: MessageType,
    val isAction: Boolean = false,
    val actionData: AIAssistant.ParsedTaskInfo? = null
)

enum class MessageType {
    USER, AI
}

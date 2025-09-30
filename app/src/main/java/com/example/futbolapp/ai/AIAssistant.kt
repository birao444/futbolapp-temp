package com.example.futbolapp.ai

import android.util.Log
import com.example.futbolapp.models.Match
import com.example.futbolapp.models.Field
import com.example.futbolapp.models.UserRole
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class AIAssistant {

    private val TAG = "AIAssistant"

    data class ParsedMatchInfo(
        val date: Date? = null,
        val time: String? = null,
        val opponent: String? = null,
        val field: String? = null,
        val isHome: Boolean = true
    )

    data class ParsedTaskInfo(
        val taskType: TaskType,
        val matchInfo: ParsedMatchInfo? = null,
        val playerName: String? = null,
        val notes: String? = null
    )

    enum class TaskType {
        CREATE_MATCH,
        SCHEDULE_TRAINING,
        ADD_PLAYER,
        UPDATE_PLAYER_STATUS,
        CREATE_LINEUP,
        GENERAL_QUERY
    }

    /**
     * Procesa texto natural y devuelve información estructurada
     */
    fun processNaturalLanguage(text: String, userRole: UserRole?): ParsedTaskInfo {
        val lowerText = text.lowercase()

        // Detectar tipo de tarea
        val taskType = when {
            // Partidos
            lowerText.contains("partido") || lowerText.contains("match") -> TaskType.CREATE_MATCH
            lowerText.contains("entrenamiento") || lowerText.contains("training") -> TaskType.SCHEDULE_TRAINING
            lowerText.contains("jugador") || lowerText.contains("player") -> TaskType.ADD_PLAYER
            lowerText.contains("alineación") || lowerText.contains("lineup") -> TaskType.CREATE_LINEUP
            lowerText.contains("lesión") || lowerText.contains("lesion") || lowerText.contains("estado") -> TaskType.UPDATE_PLAYER_STATUS
            else -> TaskType.GENERAL_QUERY
        }

        // Extraer información específica según el tipo
        return when (taskType) {
            TaskType.CREATE_MATCH -> parseMatchInfo(text)
            TaskType.SCHEDULE_TRAINING -> parseTrainingInfo(text)
            TaskType.ADD_PLAYER -> parsePlayerInfo(text)
            TaskType.UPDATE_PLAYER_STATUS -> parsePlayerStatusInfo(text)
            TaskType.CREATE_LINEUP -> parseLineupInfo(text)
            TaskType.GENERAL_QUERY -> ParsedTaskInfo(TaskType.GENERAL_QUERY, notes = text)
        }
    }

    /**
     * Parsea información de partidos
     */
    private fun parseMatchInfo(text: String): ParsedTaskInfo {
        val matchInfo = ParsedMatchInfo()

        // Patrones para fechas
        val datePatterns = listOf(
            Pattern.compile("(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4})"), // DD/MM/YYYY
            Pattern.compile("(\\d{1,2})\\s+de\\s+([a-zA-Z]+)\\s+de\\s+(\\d{4})"), // DD de MES de YYYY
            Pattern.compile("mañana|pasado mañana|próximo|este mes|este año")
        )

        // Patrones para horas
        val timePatterns = listOf(
            Pattern.compile("(\\d{1,2}):(\\d{2})"), // HH:MM
            Pattern.compile("(\\d{1,2})\\s*h\\w*"), // HH h
            Pattern.compile("(\\d{1,2})\\s*horas?") // HH horas
        )

        // Extraer fecha
        val date = extractDate(text)

        // Extraer hora
        val time = extractTime(text)

        // Extraer rival
        val opponent = extractOpponent(text)

        // Extraer campo
        val field = extractField(text)

        // Determinar si es local o visitante
        val isHome = !text.contains("visitante") && !text.contains("fuera")

        return ParsedTaskInfo(
            taskType = TaskType.CREATE_MATCH,
            matchInfo = ParsedMatchInfo(
                date = date,
                time = time,
                opponent = opponent,
                field = field,
                isHome = isHome
            )
        )
    }

    /**
     * Parsea información de entrenamientos
     */
    private fun parseTrainingInfo(text: String): ParsedTaskInfo {
        // Similar a partidos pero para entrenamientos
        val date = extractDate(text)
        val time = extractTime(text)
        val field = extractField(text)

        return ParsedTaskInfo(
            taskType = TaskType.SCHEDULE_TRAINING,
            matchInfo = ParsedMatchInfo(
                date = date,
                time = time,
                field = field
            )
        )
    }

    /**
     * Parsea información de jugadores
     */
    private fun parsePlayerInfo(text: String): ParsedTaskInfo {
        // Extraer nombre del jugador
        val namePattern = Pattern.compile("([A-Z][a-z]+\\s+[A-Z][a-z]+)")
        val matcher = namePattern.matcher(text)

        val playerName = if (matcher.find()) {
            matcher.group(1)
        } else {
            // Buscar nombres simples
            val simpleNamePattern = Pattern.compile("jugador\\s+([A-Z][a-z]+)")
            val simpleMatcher = simpleNamePattern.matcher(text)
            if (simpleMatcher.find()) simpleMatcher.group(1) else null
        }

        return ParsedTaskInfo(
            taskType = TaskType.ADD_PLAYER,
            playerName = playerName,
            notes = text
        )
    }

    /**
     * Parsea información de estado de jugadores
     */
    private fun parsePlayerStatusInfo(text: String): ParsedTaskInfo {
        val playerName = parsePlayerInfo(text).playerName
        return ParsedTaskInfo(
            taskType = TaskType.UPDATE_PLAYER_STATUS,
            playerName = playerName,
            notes = text
        )
    }

    /**
     * Parsea información de alineaciones
     */
    private fun parseLineupInfo(text: String): ParsedTaskInfo {
        return ParsedTaskInfo(
            taskType = TaskType.CREATE_LINEUP,
            notes = text
        )
    }

    /**
     * Extrae fecha del texto
     */
    private fun extractDate(text: String): Date? {
        val calendar = Calendar.getInstance()

        // Patrones de fecha
        val datePatterns = listOf(
            Pattern.compile("(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4})"), // DD/MM/YYYY
            Pattern.compile("(\\d{1,2})\\s+de\\s+([a-zA-Z]+)\\s+de\\s+(\\d{4})"), // DD de MES de YYYY
            Pattern.compile("(\\d{1,2})\\s+de\\s+([a-zA-Z]+)") // DD de MES
        )

        for (pattern in datePatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                return when (pattern.pattern()) {
                    datePatterns[0].pattern() -> { // DD/MM/YYYY
                        val day = matcher.group(1)?.toInt() ?: 1
                        val month = (matcher.group(2)?.toInt() ?: 1) - 1
                        val year = matcher.group(3)?.toInt() ?: calendar.get(Calendar.YEAR)
                        calendar.set(year, month, day)
                        calendar.time
                    }
                    datePatterns[1].pattern() -> { // DD de MES de YYYY
                        val day = matcher.group(1)?.toInt() ?: 1
                        val monthName = matcher.group(2)?.lowercase()
                        val year = matcher.group(3)?.toInt() ?: calendar.get(Calendar.YEAR)
                        val month = getMonthNumber(monthName)
                        calendar.set(year, month, day)
                        calendar.time
                    }
                    datePatterns[2].pattern() -> { // DD de MES
                        val day = matcher.group(1)?.toInt() ?: 1
                        val monthName = matcher.group(2)?.lowercase()
                        val month = getMonthNumber(monthName)
                        calendar.set(calendar.get(Calendar.YEAR), month, day)
                        calendar.time
                    }
                    else -> null
                }
            }
        }

        // Palabras clave
        return when {
            text.contains("mañana") -> {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                calendar.time
            }
            text.contains("pasado mañana") -> {
                calendar.add(Calendar.DAY_OF_MONTH, 2)
                calendar.time
            }
            text.contains("próximo") -> {
                calendar.add(Calendar.DAY_OF_MONTH, 7)
                calendar.time
            }
            else -> null
        }
    }

    /**
     * Extrae hora del texto
     */
    private fun extractTime(text: String): String? {
        val timePatterns = listOf(
            Pattern.compile("(\\d{1,2}):(\\d{2})"), // HH:MM
            Pattern.compile("(\\d{1,2})\\s*h\\w*"), // HH h
            Pattern.compile("(\\d{1,2})\\s*horas?") // HH horas
        )

        for (pattern in timePatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val hour = matcher.group(1)?.toInt() ?: 0
                val minute = if (pattern.pattern() == timePatterns[0].pattern()) {
                    matcher.group(2)?.toInt() ?: 0
                } else 0

                return String.format("%02d:%02d", hour, minute)
            }
        }

        return null
    }

    /**
     * Extrae nombre del rival
     */
    private fun extractOpponent(text: String): String? {
        val opponentPatterns = listOf(
            Pattern.compile("contra\\s+([A-Z][a-zA-Z\\s]+)"),
            Pattern.compile("vs\\s+([A-Z][a-zA-Z\\s]+)"),
            Pattern.compile("versus\\s+([A-Z][a-zA-Z\\s]+)"),
            Pattern.compile("([A-Z][a-zA-Z\\s]+)\\s+FC"),
            Pattern.compile("([A-Z][a-zA-Z\\s]+)\\s+CF")
        )

        for (pattern in opponentPatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                return matcher.group(1)?.trim()
            }
        }

        return null
    }

    /**
     * Extrae nombre del campo
     */
    private fun extractField(text: String): String? {
        val fieldPatterns = listOf(
            Pattern.compile("en\\s+([A-Z][a-zA-Z\\s]+(?:Estadio|Campo|Polideportivo))"),
            Pattern.compile("campo\\s+([A-Z][a-zA-Z\\s]+)"),
            Pattern.compile("estadio\\s+([A-Z][a-zA-Z\\s]+)")
        )

        for (pattern in fieldPatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                return matcher.group(1)?.trim()
            }
        }

        return null
    }

    /**
     * Convierte nombre de mes a número
     */
    private fun getMonthNumber(monthName: String?): Int {
        return when (monthName?.lowercase()) {
            "enero", "january" -> 0
            "febrero", "february" -> 1
            "marzo", "march" -> 2
            "abril", "april" -> 3
            "mayo", "may" -> 4
            "junio", "june" -> 5
            "julio", "july" -> 6
            "agosto", "august" -> 7
            "septiembre", "september" -> 8
            "octubre", "october" -> 9
            "noviembre", "november" -> 10
            "diciembre", "december" -> 11
            else -> Calendar.getInstance().get(Calendar.MONTH)
        }
    }

    /**
     * Genera respuesta inteligente basada en la información parseada
     */
    fun generateResponse(parsedInfo: ParsedTaskInfo, userRole: UserRole?): String {
        return when (parsedInfo.taskType) {
            TaskType.CREATE_MATCH -> generateMatchResponse(parsedInfo.matchInfo, userRole)
            TaskType.SCHEDULE_TRAINING -> generateTrainingResponse(parsedInfo.matchInfo, userRole)
            TaskType.ADD_PLAYER -> generatePlayerResponse(parsedInfo.playerName, userRole)
            TaskType.UPDATE_PLAYER_STATUS -> generateStatusResponse(parsedInfo.playerName, parsedInfo.notes, userRole)
            TaskType.CREATE_LINEUP -> generateLineupResponse(userRole)
            TaskType.GENERAL_QUERY -> generateGeneralResponse(parsedInfo.notes, userRole)
        }
    }

    private fun generateMatchResponse(matchInfo: ParsedMatchInfo?, userRole: UserRole?): String {
        if (userRole != UserRole.ENTRENADOR && userRole != UserRole.SEGUNDO && userRole != UserRole.COORDINADOR) {
            return "Lo siento, solo el entrenador, segundo entrenador o coordinador pueden programar partidos."
        }

        return buildString {
            append("📅 He entendido que quieres programar un partido")
            matchInfo?.let { info ->
                info.date?.let { date ->
                    append(" el ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)}")
                }
                info.time?.let { time ->
                    append(" a las $time")
                }
                info.opponent?.let { opponent ->
                    append(" contra $opponent")
                }
                info.field?.let { field ->
                    append(" en $field")
                }
                if (info.isHome) {
                    append(" (como local)")
                } else {
                    append(" (como visitante)")
                }
            }
            append(".\n\n¿Quieres que proceda a crearlo?")
        }
    }

    private fun generateTrainingResponse(matchInfo: ParsedMatchInfo?, userRole: UserRole?): String {
        if (userRole != UserRole.ENTRENADOR && userRole != UserRole.SEGUNDO) {
            return "Lo siento, solo el entrenador o segundo entrenador pueden programar entrenamientos."
        }

        return buildString {
            append("🏃 He entendido que quieres programar un entrenamiento")
            matchInfo?.let { info ->
                info.date?.let { date ->
                    append(" el ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)}")
                }
                info.time?.let { time ->
                    append(" a las $time")
                }
                info.field?.let { field ->
                    append(" en $field")
                }
            }
            append(".\n\n¿Procedo a programarlo?")
        }
    }

    private fun generatePlayerResponse(playerName: String?, userRole: UserRole?): String {
        if (userRole != UserRole.ENTRENADOR && userRole != UserRole.SEGUNDO) {
            return "Lo siento, solo el entrenador o segundo entrenador pueden añadir jugadores."
        }

        return if (playerName != null) {
            "👤 He entendido que quieres añadir al jugador $playerName al equipo.\n\n¿Es correcto? ¿Quieres añadir más información sobre él?"
        } else {
            "👤 Parece que quieres añadir un jugador, pero no pude identificar el nombre.\n\n¿Puedes darme más detalles sobre el jugador?"
        }
    }

    private fun generateStatusResponse(playerName: String?, notes: String?, userRole: UserRole?): String {
        val allowedRoles = listOf(UserRole.ENTRENADOR, UserRole.SEGUNDO, UserRole.FISIO)
        if (userRole !in allowedRoles) {
            return "Lo siento, solo el entrenador, segundo entrenador o fisioterapeuta pueden actualizar el estado de los jugadores."
        }

        return if (playerName != null) {
            "🏥 He entendido que hay una actualización sobre el estado de $playerName.\n\n¿Puedes darme más detalles sobre la lesión o condición?"
        } else {
            "🏥 Parece que hay información sobre el estado de un jugador.\n\n¿Puedes especificar qué jugador y qué condición?"
        }
    }

    private fun generateLineupResponse(userRole: UserRole?): String {
        if (userRole != UserRole.ENTRENADOR && userRole != UserRole.SEGUNDO) {
            return "Lo siento, solo el entrenador o segundo entrenador pueden crear alineaciones."
        }

        return "⚽ He entendido que quieres crear una alineación.\n\n¿Para qué partido? ¿Quieres que te ayude a seleccionar los jugadores?"
    }

    private fun generateGeneralResponse(notes: String?, userRole: UserRole?): String {
        return when {
            notes?.contains("ayuda") == true || notes?.contains("help") == true -> {
                "🤖 Soy tu asistente de fútbol. Puedo ayudarte a:\n\n" +
                "📅 Programar partidos y entrenamientos\n" +
                "👤 Gestionar jugadores\n" +
                "⚽ Crear alineaciones\n" +
                "🏥 Actualizar estados de jugadores\n\n" +
                "Solo dime qué necesitas y te ayudo."
            }
            else -> {
                "🤖 No estoy seguro de qué necesitas. ¿Puedes darme más detalles sobre lo que quieres hacer?"
            }
        }
    }

    /**
     * Ejecuta la acción basada en la información parseada
     */
    suspend fun executeAction(
        parsedInfo: ParsedTaskInfo,
        userRole: UserRole?,
        teamId: String
    ): String {
        return try {
            when (parsedInfo.taskType) {
                TaskType.CREATE_MATCH -> {
                    // Aquí iría la lógica para crear el partido en Firebase
                    // Por ahora solo devolvemos confirmación
                    "✅ Partido programado exitosamente"
                }
                TaskType.SCHEDULE_TRAINING -> {
                    "✅ Entrenamiento programado exitosamente"
                }
                TaskType.ADD_PLAYER -> {
                    parsedInfo.playerName?.let { name ->
                        "✅ Jugador $name añadido al equipo"
                    } ?: "❌ No pude identificar el nombre del jugador"
                }
                TaskType.UPDATE_PLAYER_STATUS -> {
                    parsedInfo.playerName?.let { name ->
                        // TODO: Implement status update logic
                        "✅ Estado de $name actualizado"
                    } ?: "❌ No pude identificar el jugador"
                }
                TaskType.CREATE_LINEUP -> {
                    "✅ Alineación creada exitosamente"
                }
                TaskType.GENERAL_QUERY -> {
                    generateGeneralResponse(parsedInfo.notes, userRole)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error ejecutando acción IA", e)
            "❌ Hubo un error procesando tu solicitud. Inténtalo de nuevo."
        }
    }
}

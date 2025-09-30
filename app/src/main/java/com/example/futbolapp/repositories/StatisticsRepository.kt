package com.example.futbolapp.repositories

import com.example.futbolapp.firebase.FirebaseFirestoreManager
import com.example.futbolapp.models.Statistics

class StatisticsRepository(
    private val firestoreManager: FirebaseFirestoreManager = FirebaseFirestoreManager()
) {

    suspend fun createStatistics(stats: Statistics): String {
        val statsId = firestoreManager.db.collection("statistics").document().id
        val statsData = mapOf(
            "id" to statsId,
            "playerId" to stats.playerId,
            "matchId" to stats.matchId,
            "teamId" to stats.teamId,
            "goals" to stats.goals,
            "assists" to stats.assists,
            "minutesPlayed" to stats.minutesPlayed,
            "shots" to stats.shots,
            "shotsOnTarget" to stats.shotsOnTarget,
            "passes" to stats.passes,
            "passesCompleted" to stats.passesCompleted,
            "tackles" to stats.tackles,
            "interceptions" to stats.interceptions,
            "saves" to stats.saves,
            "yellowCards" to stats.yellowCards,
            "redCards" to stats.redCards,
            "createdAt" to System.currentTimeMillis()
        )
        firestoreManager.createOrUpdateStatistics(statsId, statsData)
        return statsId
    }

    suspend fun updateStatistics(statsId: String, updates: Map<String, Any>) {
        firestoreManager.createOrUpdateStatistics(statsId, updates)
    }

    suspend fun getStatisticsForPlayer(playerId: String): List<Statistics> {
        val statsData = firestoreManager.getStatisticsForPlayer(playerId)
        return statsData.mapNotNull { mapToStatistics(it) }
    }

    suspend fun getStatisticsForMatch(matchId: String): List<Statistics> {
        // This would need to be implemented in FirebaseFirestoreManager
        // For now, return empty list
        return emptyList()
    }

    private fun mapToStatistics(data: Map<String, Any>): Statistics {
        return Statistics(
            id = data["id"] as? String ?: "",
            playerId = data["playerId"] as? String ?: "",
            matchId = data["matchId"] as? String ?: "",
            teamId = data["teamId"] as? String ?: "",
            goals = (data["goals"] as? Number)?.toInt() ?: 0,
            assists = (data["assists"] as? Number)?.toInt() ?: 0,
            minutesPlayed = (data["minutesPlayed"] as? Number)?.toInt() ?: 0,
            shots = (data["shots"] as? Number)?.toInt() ?: 0,
            shotsOnTarget = (data["shotsOnTarget"] as? Number)?.toInt() ?: 0,
            passes = (data["passes"] as? Number)?.toInt() ?: 0,
            passesCompleted = (data["passesCompleted"] as? Number)?.toInt() ?: 0,
            tackles = (data["tackles"] as? Number)?.toInt() ?: 0,
            interceptions = (data["interceptions"] as? Number)?.toInt() ?: 0,
            saves = (data["saves"] as? Number)?.toInt() ?: 0,
            yellowCards = (data["yellowCards"] as? Number)?.toInt() ?: 0,
            redCards = (data["redCards"] as? Number)?.toInt() ?: 0
        )
    }
}

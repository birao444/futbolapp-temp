package com.example.futbolapp.repositories

import com.example.futbolapp.firebase.FirebaseFirestoreManager
import com.example.futbolapp.models.Lineup

class LineupRepository(
    private val firestoreManager: FirebaseFirestoreManager = FirebaseFirestoreManager()
) {

    suspend fun createLineup(lineup: Lineup): String {
        val lineupId = firestoreManager.db.collection("lineups").document().id
        val lineupData = mapOf(
            "id" to lineupId,
            "matchId" to lineup.matchId,
            "teamId" to lineup.teamId,
            "formation" to lineup.formation,
            "startingPlayers" to lineup.startingPlayers,
            "substitutePlayers" to lineup.substitutePlayers,
            "captainId" to lineup.captainId,
            "createdAt" to System.currentTimeMillis()
        )
        firestoreManager.createOrUpdateLineup(lineupId, lineupData)
        return lineupId
    }

    suspend fun updateLineup(lineupId: String, updates: Map<String, Any>) {
        firestoreManager.createOrUpdateLineup(lineupId, updates)
    }

    suspend fun getLineupForMatch(matchId: String): List<Lineup> {
        val lineupsData = firestoreManager.getLineupForMatch(matchId)
        return lineupsData.mapNotNull { mapToLineup(it) }
    }

    suspend fun deleteLineup(lineupId: String) {
        // This would need to be implemented in FirebaseFirestoreManager
        // For now, do nothing
    }

    private fun mapToLineup(data: Map<String, Any>): Lineup {
        return Lineup(
            id = data["id"] as? String ?: "",
            matchId = data["matchId"] as? String ?: "",
            teamId = data["teamId"] as? String ?: "",
            formation = data["formation"] as? String ?: "",
            startingPlayers = (data["startingPlayers"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            substitutePlayers = (data["substitutePlayers"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            captainId = data["captainId"] as? String ?: ""
        )
    }
}

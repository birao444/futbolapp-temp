package com.example.futbolapp.repositories

import com.example.futbolapp.firebase.FirebaseFirestoreManager
import com.example.futbolapp.models.Player

class PlayerRepository(
    private val firestoreManager: FirebaseFirestoreManager = FirebaseFirestoreManager()
) {

    suspend fun createPlayer(player: Player): String {
        val playerId = firestoreManager.db.collection("players").document().id
        val playerData = mapOf(
            "id" to playerId,
            "teamId" to player.teamId,
            "name" to player.name,
            "position" to player.position,
            "number" to player.number,
            "age" to player.age,
            "height" to player.height,
            "weight" to player.weight,
            "nationality" to player.nationality,
            "contractUntil" to player.contractUntil,
            "salary" to player.salary,
            "createdAt" to System.currentTimeMillis()
        )
        firestoreManager.createOrUpdatePlayer(playerId, playerData)
        return playerId
    }

    suspend fun updatePlayer(playerId: String, updates: Map<String, Any>) {
        firestoreManager.createOrUpdatePlayer(playerId, updates)
    }

    suspend fun getPlayer(playerId: String): Player? {
        // This would need to be implemented in FirebaseFirestoreManager
        // For now, return null
        return null
    }

    suspend fun getPlayersForTeam(teamId: String): List<Player> {
        val playersData = firestoreManager.getPlayersForTeam(teamId)
        return playersData.mapNotNull { mapToPlayer(it) }
    }

    suspend fun deletePlayer(playerId: String) {
        // This would need to be implemented in FirebaseFirestoreManager
        // For now, do nothing
    }

    private fun mapToPlayer(data: Map<String, Any>): Player {
        return Player(
            id = data["id"] as? String ?: "",
            teamId = data["teamId"] as? String ?: "",
            name = data["name"] as? String ?: "",
            position = data["position"] as? String ?: "",
            number = (data["number"] as? Number)?.toInt() ?: 0,
            age = (data["age"] as? Number)?.toInt() ?: 0,
            height = (data["height"] as? Number)?.toDouble() ?: 0.0,
            weight = (data["weight"] as? Number)?.toDouble() ?: 0.0,
            nationality = data["nationality"] as? String ?: "",
            contractUntil = data["contractUntil"] as? Long ?: 0L,
            salary = (data["salary"] as? Number)?.toDouble() ?: 0.0
        )
    }
}

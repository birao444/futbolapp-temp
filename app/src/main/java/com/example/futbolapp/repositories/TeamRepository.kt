package com.example.futbolapp.repositories

import com.example.futbolapp.firebase.FirebaseFirestoreManager
import com.example.futbolapp.models.Team

class TeamRepository(
    private val firestoreManager: FirebaseFirestoreManager = FirebaseFirestoreManager()
) {

    suspend fun createTeam(team: Team) {
        val data = mapOf(
            "name" to team.name,
            "userId" to team.userId,
            "description" to team.description,
            "createdAt" to team.createdAt
        )
        firestoreManager.createOrUpdateTeam(team.id, data)
    }

    suspend fun getTeam(teamId: String): Team? {
        val data = firestoreManager.getTeam(teamId)
        return data?.let {
            Team(
                id = teamId,
                name = it["name"] as? String ?: "",
                userId = it["userId"] as? String ?: "",
                description = it["description"] as? String ?: "",
                createdAt = it["createdAt"] as? Long ?: 0L
            )
        }
    }

    suspend fun getTeamsForUser(userId: String): List<Team> {
        val dataList = firestoreManager.getTeamsForUser(userId)
        return dataList.map {
            Team(
                id = it["id"] as? String ?: "",
                name = it["name"] as? String ?: "",
                userId = it["userId"] as? String ?: "",
                description = it["description"] as? String ?: "",
                createdAt = it["createdAt"] as? Long ?: 0L
            )
        }
    }

    suspend fun updateTeam(team: Team) {
        createTeam(team) // same as create
    }
}

package com.example.futbolapp.repositories

import com.example.futbolapp.firebase.FirebaseFirestoreManager
import com.example.futbolapp.models.Team
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TeamRepository(
    private val firestoreManager: FirebaseFirestoreManager = FirebaseFirestoreManager()
) {

    private val _currentTeam = MutableStateFlow<Team?>(null)
    val currentTeam: StateFlow<Team?> = _currentTeam

    private var teamListener: ListenerRegistration? = null
    private var teamsListener: ListenerRegistration? = null

    suspend fun createTeam(team: Team): String {
        val teamId = firestoreManager.db.collection("teams").document().id
        val teamData = mapOf(
            "id" to teamId,
            "name" to team.name,
            "userId" to team.userId,
            "description" to team.description,
            "createdAt" to team.createdAt
        )
        firestoreManager.createOrUpdateTeam(teamId, teamData)
        return teamId
    }

    suspend fun updateTeam(teamId: String, updates: Map<String, Any>) {
        firestoreManager.createOrUpdateTeam(teamId, updates)
    }

    suspend fun getTeam(teamId: String): Team? {
        val data = firestoreManager.getTeam(teamId)
        return data?.let {
            Team(
                id = it["id"] as? String ?: "",
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

    fun listenToTeam(teamId: String) {
        teamListener = firestoreManager.listenToTeam(teamId) { data ->
            val team = data?.let {
                Team(
                    id = it["id"] as? String ?: "",
                    name = it["name"] as? String ?: "",
                    userId = it["userId"] as? String ?: "",
                    description = it["description"] as? String ?: "",
                    createdAt = it["createdAt"] as? Long ?: 0L
                )
            }
            _currentTeam.value = team
        }
    }

    fun listenToTeamsForUser(userId: String, onUpdate: (List<Map<String, Any>>) -> Unit) {
        teamsListener = firestoreManager.db.collection("teams")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                val teamsData = snapshot?.documents?.mapNotNull { it.data } ?: emptyList()
                onUpdate(teamsData)
            }
    }

    fun stopListening() {
        teamListener?.remove()
        teamListener = null
        teamsListener?.remove()
        teamsListener = null
    }
}

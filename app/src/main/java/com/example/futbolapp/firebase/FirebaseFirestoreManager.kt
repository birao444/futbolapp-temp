package com.example.futbolapp.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreManager {
    val db = FirebaseFirestore.getInstance()

    // Collection references
    private val usersCollection = db.collection("users")
    private val teamsCollection = db.collection("teams")
    private val matchesCollection = db.collection("matches")
    private val rolesCollection = db.collection("roles")
    private val playersCollection = db.collection("players")
    private val lineupsCollection = db.collection("lineups")
    private val statisticsCollection = db.collection("statistics")
    private val fieldsCollection = db.collection("fields")
    private val improvementsCollection = db.collection("improvements")

    // User data operations
    suspend fun createOrUpdateUserData(userId: String, data: Map<String, Any>) {
        usersCollection.document(userId).set(data).await()
    }

    suspend fun getUserData(userId: String): Map<String, Any>? {
        val document = usersCollection.document(userId).get().await()
        return document.data
    }

    // Team operations
    suspend fun createOrUpdateTeam(teamId: String, data: Map<String, Any>) {
        teamsCollection.document(teamId).set(data).await()
    }

    suspend fun getTeam(teamId: String): Map<String, Any>? {
        val document = teamsCollection.document(teamId).get().await()
        return document.data
    }

    suspend fun getTeamsForUser(userId: String): List<Map<String, Any>> {
        val query = teamsCollection.whereEqualTo("userId", userId)
        val result = query.get().await()
        return result.documents.mapNotNull { it.data }
    }

    // Match operations
    suspend fun createOrUpdateMatch(matchId: String, data: Map<String, Any>) {
        matchesCollection.document(matchId).set(data).await()
    }

    suspend fun getMatch(matchId: String): Map<String, Any>? {
        val document = matchesCollection.document(matchId).get().await()
        return document.data
    }

    suspend fun getUpcomingMatchesForTeam(teamId: String): List<Map<String, Any>> {
        val query = matchesCollection.whereEqualTo("homeTeamId", teamId).whereGreaterThan("date", System.currentTimeMillis())
        val result = query.get().await()
        return result.documents.mapNotNull { it.data }
    }

    // Role operations
    suspend fun assignRole(userId: String, teamId: String, role: String) {
        val data = mapOf("userId" to userId, "teamId" to teamId, "role" to role)
        rolesCollection.document("$userId-$teamId").set(data).await()
    }

    suspend fun getRole(userId: String, teamId: String): String? {
        val document = rolesCollection.document("$userId-$teamId").get().await()
        return document.getString("role")
    }

    // Player operations
    suspend fun createOrUpdatePlayer(playerId: String, data: Map<String, Any>) {
        playersCollection.document(playerId).set(data).await()
    }

    suspend fun getPlayersForTeam(teamId: String): List<Map<String, Any>> {
        val query = playersCollection.whereEqualTo("teamId", teamId)
        val result = query.get().await()
        return result.documents.mapNotNull { it.data }
    }

    // Lineup operations
    suspend fun createOrUpdateLineup(lineupId: String, data: Map<String, Any>) {
        lineupsCollection.document(lineupId).set(data).await()
    }

    suspend fun getLineupForMatch(matchId: String): List<Map<String, Any>> {
        val query = lineupsCollection.whereEqualTo("matchId", matchId)
        val result = query.get().await()
        return result.documents.mapNotNull { it.data }
    }

    // Statistics operations
    suspend fun createOrUpdateStatistics(statsId: String, data: Map<String, Any>) {
        statisticsCollection.document(statsId).set(data).await()
    }

    suspend fun getStatisticsForPlayer(playerId: String): List<Map<String, Any>> {
        val query = statisticsCollection.whereEqualTo("playerId", playerId)
        val result = query.get().await()
        return result.documents.mapNotNull { it.data }
    }

    // Field operations
    suspend fun createOrUpdateField(fieldId: String, data: Map<String, Any>) {
        fieldsCollection.document(fieldId).set(data).await()
    }

    suspend fun getFields(): List<Map<String, Any>> {
        val result = fieldsCollection.get().await()
        return result.documents.mapNotNull { it.data }
    }

    // Improvements operations
    suspend fun createOrUpdateImprovement(improvementId: String, data: Map<String, Any>) {
        improvementsCollection.document(improvementId).set(data).await()
    }

    suspend fun getImprovementsForTeam(teamId: String): List<Map<String, Any>> {
        val query = improvementsCollection.whereEqualTo("teamId", teamId)
        val result = query.get().await()
        return result.documents.mapNotNull { it.data }
    }

    // Real-time listeners
    fun listenToUpcomingMatches(teamId: String, onUpdate: (List<Map<String, Any>>) -> Unit): ListenerRegistration {
        val query = matchesCollection.whereEqualTo("homeTeamId", teamId).whereGreaterThan("date", System.currentTimeMillis())
        return query.addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            val matches = snapshot?.documents?.mapNotNull { it.data } ?: emptyList()
            onUpdate(matches)
        }
    }

    fun listenToTeam(teamId: String, onUpdate: (Map<String, Any>?) -> Unit): ListenerRegistration {
        return teamsCollection.document(teamId).addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            onUpdate(snapshot?.data)
        }
    }

    fun listenToTeamsForUser(userId: String, onUpdate: (List<Map<String, Any>>) -> Unit): ListenerRegistration {
        val query = teamsCollection.whereEqualTo("userId", userId)
        return query.addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            val teams = snapshot?.documents?.mapNotNull { it.data } ?: emptyList()
            onUpdate(teams)
        }
    }
}

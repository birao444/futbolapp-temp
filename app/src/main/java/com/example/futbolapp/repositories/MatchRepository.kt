package com.example.futbolapp.repositories

import com.example.futbolapp.firebase.FirebaseFirestoreManager
import com.example.futbolapp.models.Match
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MatchRepository(
    private val firestoreManager: FirebaseFirestoreManager = FirebaseFirestoreManager()
) {

    private val _upcomingMatches = MutableStateFlow<List<Match>>(emptyList())
    val upcomingMatches: Flow<List<Match>> = _upcomingMatches.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    suspend fun createMatch(match: Match): String {
        val matchId = firestoreManager.db.collection("matches").document().id
        val matchData = mapOf(
            "id" to matchId,
            "homeTeamId" to match.homeTeamId,
            "awayTeamId" to match.awayTeamId,
            "homeTeamName" to match.homeTeamName,
            "awayTeamName" to match.awayTeamName,
            "date" to match.date,
            "field" to match.field,
            "status" to match.status,
            "homeScore" to match.homeScore,
            "awayScore" to match.awayScore,
            "createdAt" to System.currentTimeMillis()
        )
        firestoreManager.createOrUpdateMatch(matchId, matchData)
        return matchId
    }

    suspend fun updateMatch(matchId: String, updates: Map<String, Any>) {
        firestoreManager.createOrUpdateMatch(matchId, updates)
    }

    suspend fun getMatch(matchId: String): Match? {
        val data = firestoreManager.getMatch(matchId)
        return data?.let { mapToMatch(it) }
    }

    suspend fun getMatchesForTeam(teamId: String): List<Match> {
        val upcomingData = firestoreManager.getUpcomingMatchesForTeam(teamId)
        return upcomingData.mapNotNull { mapToMatch(it) }
    }

    fun listenToUpcomingMatches(teamId: String) {
        listenerRegistration?.remove()
        listenerRegistration = firestoreManager.listenToUpcomingMatches(teamId) { matchesData ->
            val matches = matchesData.mapNotNull { mapToMatch(it) }
            _upcomingMatches.value = matches
        }
    }

    fun stopListening() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }

    private fun mapToMatch(data: Map<String, Any>): Match {
        return Match(
            id = data["id"] as? String ?: "",
            homeTeamId = data["homeTeamId"] as? String ?: "",
            awayTeamId = data["awayTeamId"] as? String ?: "",
            homeTeamName = data["homeTeamName"] as? String ?: "",
            awayTeamName = data["awayTeamName"] as? String ?: "",
            date = data["date"] as? Long ?: 0L,
            field = data["field"] as? String ?: "",
            status = data["status"] as? String ?: "scheduled",
            homeScore = (data["homeScore"] as? Number)?.toInt() ?: 0,
            awayScore = (data["awayScore"] as? Number)?.toInt() ?: 0
        )
    }
}

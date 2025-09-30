package com.example.futbolapp.models

data class Lineup(
    val id: String = "",
    val matchId: String = "",
    val teamId: String = "",
    val formation: String = "",
    val startingPlayers: List<String> = emptyList(),
    val substitutePlayers: List<String> = emptyList(),
    val captainId: String = ""
)

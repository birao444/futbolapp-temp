package com.example.futbolapp.models

data class Statistics(
    val id: String = "",
    val playerId: String = "",
    val matchId: String = "",
    val teamId: String = "",
    val minutesPlayed: Int = 0,
    val shots: Int = 0,
    val shotsOnTarget: Int = 0,
    val passes: Int = 0,
    val passesCompleted: Int = 0,
    val tackles: Int = 0,
    val interceptions: Int = 0,
    val saves: Int = 0,
    val goals: Int = 0,
    val assists: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0
)

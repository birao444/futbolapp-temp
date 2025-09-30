package com.example.futbolapp.models

data class Statistics(
    val id: String = "",
    val playerId: String = "",
    val matchId: String = "",
    val goals: Int = 0,
    val assists: Int = 0,
    val minutesPlayed: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0
)

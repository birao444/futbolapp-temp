package com.example.futbolapp.models

data class Lineup(
    val id: String = "",
    val matchId: String = "",
    val playerId: String = "",
    val position: String = "" // e.g., GK, DF, MF, FW
)

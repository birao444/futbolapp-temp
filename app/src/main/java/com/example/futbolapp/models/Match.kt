package com.example.futbolapp.models

data class Match(
    val id: String = "",
    val homeTeamId: String = "",
    val awayTeamId: String = "",
    val homeTeamName: String = "",
    val awayTeamName: String = "",
    val date: Long = 0L,
    val field: String = "",
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val status: String = "scheduled" // scheduled, in_progress, finished
)

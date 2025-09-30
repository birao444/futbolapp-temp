package com.example.futbolapp.models

data class Match(
    val id: String = "",
    val homeTeamId: String = "",
    val awayTeamId: String = "",
    val date: Long = 0L,
    val fieldId: String = "",
    val status: String = "scheduled" // scheduled, in_progress, finished
)

package com.example.futbolapp.models

data class Player(
    val id: String = "",
    val name: String = "",
    val position: String = "",
    val teamId: String = "",
    val userId: String = "", // assigned user
    val stats: Map<String, Int> = emptyMap() // e.g., goals, assists, etc.
)

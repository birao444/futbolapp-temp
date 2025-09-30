package com.example.futbolapp.models

data class Team(
    val id: String = "",
    val name: String = "",
    val userId: String = "", // owner
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

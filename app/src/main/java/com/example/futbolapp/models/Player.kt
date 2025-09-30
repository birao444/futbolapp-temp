package com.example.futbolapp.models

data class Player(
    val id: String = "",
    val teamId: String = "",
    val name: String = "",
    val number: Int = 0,
    val age: Int = 0,
    val height: Double = 0.0,
    val weight: Double = 0.0,
    val nationality: String = "",
    val contractUntil: Long = 0L,
    val salary: Double = 0.0,
    val position: String = ""
)

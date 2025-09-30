package com.example.futbolapp.models

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val role: String = "jugador" // entrenador, segundo, jugador, fisio, coordinador
)

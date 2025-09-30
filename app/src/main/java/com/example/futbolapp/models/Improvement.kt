package com.example.futbolapp.models

data class Improvement(
    val id: String = "",
    val teamId: String = "",
    val type: String = "",
    val description: String = "",
    val cost: Double = 0.0,
    val duration: Long = 0L,
    val effect: String = "",
    val isCompleted: Boolean = false
)

package com.example.futbolapp.models

data class Field(
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val capacity: Int = 0,
    val surface: String = "",
    val dimensions: String = "",
    val facilities: List<String> = emptyList()
)

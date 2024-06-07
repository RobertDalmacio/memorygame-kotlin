package com.example.memorygame.models

data class PreviousGame (
    val id: String,
    val gameDifficulty: String,
    val numberOfMoves: Int,
    val completionTime: String,
    val timestamp: String,
)

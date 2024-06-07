package com.example.memorygame.models

enum class BoardSize(val numCards: Int) {
    EASY(8), // 6 X 2
    MEDIUM(18), // 6 X 3
    HARD(24);  // 6 X 4

    fun getWidth(): Int {
        return when (this) {
            EASY -> 2
            MEDIUM -> 3
            HARD -> 4
        }
    }

    fun getHeight(): Int {
        return numCards / getWidth()
    }

    fun getNumPairs(): Int {
        return numCards / 2
    }
}
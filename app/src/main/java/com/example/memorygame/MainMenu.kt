package com.example.memorygame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class MainMenu : AppCompatActivity() {
    companion object {
        private const val CREATE_REQUEST_CODE = 165
    }

    private lateinit var ibEasy: ImageButton
    private lateinit var ibMedium: ImageButton
    private lateinit var ibHard: ImageButton
    private lateinit var ibNewGame: ImageButton
    private lateinit var ibGameHistory: ImageButton

    private var easyModeSelected = true
    private var mediumModeSelected = false
    private var hardModeSelected = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        ibEasy = findViewById(R.id.ib_easy)
        ibMedium = findViewById(R.id.ib_medium)
        ibHard = findViewById(R.id.ib_hard)
        ibNewGame = findViewById(R.id.ib_newgame)
        ibGameHistory = findViewById(R.id.ib_gamehistory)

        ibEasy.setOnClickListener {
            if (!easyModeSelected) {
                easyModeSelected = true
                mediumModeSelected = false
                hardModeSelected = false
                ibEasy.setImageResource(R.drawable.button_easy_selected)
                ibMedium.setImageResource(R.drawable.button_medium)
                ibHard.setImageResource(R.drawable.button_hard)
            }
        }

        ibMedium.setOnClickListener {
            if (!mediumModeSelected) {
                easyModeSelected = false
                mediumModeSelected = true
                hardModeSelected = false
                ibEasy.setImageResource(R.drawable.button_easy)
                ibMedium.setImageResource(R.drawable.button_medium_selected)
                ibHard.setImageResource(R.drawable.button_hard)
            }
        }

        ibHard.setOnClickListener {
            if (!hardModeSelected) {
                easyModeSelected = false
                mediumModeSelected = false
                hardModeSelected = true
                ibEasy.setImageResource(R.drawable.button_easy)
                ibMedium.setImageResource(R.drawable.button_medium)
                ibHard.setImageResource(R.drawable.button_hard_selected)
            }
        }

        ibNewGame.setOnClickListener {
            val intent = Intent(this, GameBoard::class.java).also {
                it.putExtra("fromMainMenu", true)
                it.putExtra("easyModeSelected", easyModeSelected)
                it.putExtra("mediumModeSelected", mediumModeSelected)
                it.putExtra("hardModeSelected", hardModeSelected)
                startActivity(it)
            }
        }

        ibGameHistory.setOnClickListener {
            val intent = Intent(this, GameHistory::class.java)
            startActivityForResult(intent, CREATE_REQUEST_CODE)
        }
    }
}
package com.example.memorygame

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import com.example.memorygame.models.PreviousGame

class GameHistory : AppCompatActivity() {
    companion object {
        private const val CREATE_REQUEST_CODE = 105
    }

    private lateinit var ibMainMenu: ImageButton
    private lateinit var ibEasyFilter: ImageButton
    private lateinit var ibMediumFilter: ImageButton
    private lateinit var ibHardFilter: ImageButton
    private lateinit var ibAllFilter: ImageButton
    private lateinit var ibMovesCompleted: ImageButton
    private lateinit var ibCompletionTime: ImageButton
    private lateinit var lvGameHistory: ListView
    private lateinit var previousGamesList: ArrayList<PreviousGame>

    private var easyFilterSelected = false
    private var mediumFilterSelected = false
    private var hardFilterSelected = false
    private var allFilterSelected = true
    private var movesCompletedSelected = false
    private var completionTimeSelected = false

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_game_history, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_deleteGameHistory -> {
                showAlertDialog("Are you sure you want to delete your Game History?", null, View.OnClickListener {
                    val sharedPreferences = getSharedPreferences("game_data", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()
                    val intent = Intent(this, MainMenu::class.java)
                    startActivityForResult(intent, CREATE_REQUEST_CODE)
                })
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_history)

        ibMainMenu = findViewById(R.id.ibMainMenu)
        ibEasyFilter = findViewById(R.id.ibEasyFilter)
        ibMediumFilter = findViewById(R.id.ibMediumFilter)
        ibHardFilter = findViewById(R.id.ibHardFilter)
        ibAllFilter = findViewById(R.id.ibAllFilter)
        ibMovesCompleted = findViewById(R.id.ibMovesCompleted)
        ibCompletionTime = findViewById(R.id.ibCompletionTime)
        lvGameHistory = findViewById(R.id.lvGameHistory)

        val sharedPreferences = this.getSharedPreferences("game_data", Context.MODE_PRIVATE)

        previousGamesList = ArrayList()
        var filteredList: ArrayList<PreviousGame>

        loadPastGameData(sharedPreferences)

        ibMainMenu.setOnClickListener {
            val intent = Intent(this, MainMenu::class.java)
            startActivityForResult(intent, CREATE_REQUEST_CODE)
        }

        ibEasyFilter.setOnClickListener {
            if (!easyFilterSelected) {
                easyFilterSelected = true
                mediumFilterSelected = false
                hardFilterSelected = false
                allFilterSelected = false
                ibEasyFilter.setImageResource(R.drawable.button_game_history_easy_selected)
                ibMediumFilter.setImageResource(R.drawable.button_game_history_medium)
                ibHardFilter.setImageResource(R.drawable.button_game_history_hard)
                ibAllFilter.setImageResource(R.drawable.button_game_history_all)
                filteredList = previousGamesList.filter { it.gameDifficulty == "EASY" } as ArrayList<PreviousGame>
                if (movesCompletedSelected) {
                    filteredList.sortBy { it.numberOfMoves }
                } else if (completionTimeSelected) {
                    filteredList.sortBy { it.completionTime }
                }
                lvGameHistory.adapter = GameHistoryAdapter(this, filteredList)
            }
        }

        ibMediumFilter.setOnClickListener {
            if (!mediumFilterSelected) {
                easyFilterSelected = false
                mediumFilterSelected = true
                hardFilterSelected = false
                allFilterSelected = false
                ibEasyFilter.setImageResource(R.drawable.button_game_history_easy)
                ibMediumFilter.setImageResource(R.drawable.button_game_history_medium_selected)
                ibHardFilter.setImageResource(R.drawable.button_game_history_hard)
                ibAllFilter.setImageResource(R.drawable.button_game_history_all)
                filteredList = previousGamesList.filter { it.gameDifficulty == "MEDIUM" } as ArrayList<PreviousGame>
                if (movesCompletedSelected) {
                    filteredList.sortBy { it.numberOfMoves }
                } else if (completionTimeSelected) {
                    filteredList.sortBy { it.completionTime }
                }
                lvGameHistory.adapter = GameHistoryAdapter(this, filteredList)
            }
        }

        ibHardFilter.setOnClickListener {
            if (!hardFilterSelected) {
                easyFilterSelected = false
                mediumFilterSelected = false
                hardFilterSelected = true
                allFilterSelected = false
                ibEasyFilter.setImageResource(R.drawable.button_game_history_easy)
                ibMediumFilter.setImageResource(R.drawable.button_game_history_medium)
                ibHardFilter.setImageResource(R.drawable.button_game_history_hard_selected)
                ibAllFilter.setImageResource(R.drawable.button_game_history_all)
                filteredList = previousGamesList.filter { it.gameDifficulty == "HARD" } as ArrayList<PreviousGame>
                if (movesCompletedSelected) {
                    filteredList.sortBy { it.numberOfMoves }
                } else if (completionTimeSelected) {
                    filteredList.sortBy { it.completionTime }
                }
                lvGameHistory.adapter = GameHistoryAdapter(this, filteredList)
            }
        }

        ibAllFilter.setOnClickListener {
            if (!allFilterSelected) {
                easyFilterSelected = false
                mediumFilterSelected = false
                hardFilterSelected = false
                allFilterSelected = true
                ibEasyFilter.setImageResource(R.drawable.button_game_history_easy)
                ibMediumFilter.setImageResource(R.drawable.button_game_history_medium)
                ibHardFilter.setImageResource(R.drawable.button_game_history_hard)
                ibAllFilter.setImageResource(R.drawable.button_game_history_all_selected)
                if (movesCompletedSelected) {
                    previousGamesList.sortBy { it.numberOfMoves }
                } else if (completionTimeSelected) {
                    previousGamesList.sortBy { it.completionTime }
                }
                lvGameHistory.adapter = GameHistoryAdapter(this, previousGamesList)
            }
        }

        ibMovesCompleted.setOnClickListener {
            if (easyFilterSelected) {
                filteredList = previousGamesList.filter { it.gameDifficulty == "EASY" } as ArrayList<PreviousGame>
            } else if (mediumFilterSelected) {
                filteredList = previousGamesList.filter { it.gameDifficulty == "MEDIUM" } as ArrayList<PreviousGame>
            } else if (hardFilterSelected) {
                filteredList = previousGamesList.filter { it.gameDifficulty == "HARD" } as ArrayList<PreviousGame>
            } else {
                filteredList = previousGamesList
            }
            if (!movesCompletedSelected) {
                movesCompletedSelected = true
                completionTimeSelected = false
                ibMovesCompleted.setImageResource(R.drawable.button_game_history_moves_completed_selected)
                ibCompletionTime.setImageResource(R.drawable.button_game_history_completion_time)
                filteredList.sortBy { it.numberOfMoves }
                lvGameHistory.adapter = GameHistoryAdapter(this, filteredList)
            } else {
                movesCompletedSelected = false
                completionTimeSelected = false
                ibMovesCompleted.setImageResource(R.drawable.button_game_history_moves_completed)
                ibCompletionTime.setImageResource(R.drawable.button_game_history_completion_time)
                filteredList.sortBy {it.id}
                lvGameHistory.adapter = GameHistoryAdapter(this, filteredList)
            }
        }

        ibCompletionTime.setOnClickListener {
            if (easyFilterSelected) {
                filteredList = previousGamesList.filter { it.gameDifficulty == "EASY" } as ArrayList<PreviousGame>
            } else if (mediumFilterSelected) {
                filteredList = previousGamesList.filter { it.gameDifficulty == "MEDIUM" } as ArrayList<PreviousGame>
            } else if (hardFilterSelected) {
                filteredList = previousGamesList.filter { it.gameDifficulty == "HARD" } as ArrayList<PreviousGame>
            } else {
                filteredList = previousGamesList
            }
            if (!completionTimeSelected) {
                movesCompletedSelected = false
                completionTimeSelected = true
                ibMovesCompleted.setImageResource(R.drawable.button_game_history_moves_completed)
                ibCompletionTime.setImageResource(R.drawable.button_game_history_completion_time_selected)
                filteredList.sortBy { it.completionTime }
                lvGameHistory.adapter = GameHistoryAdapter(this, filteredList)
            } else {
                movesCompletedSelected = false
                completionTimeSelected = false
                ibMovesCompleted.setImageResource(R.drawable.button_game_history_moves_completed)
                ibCompletionTime.setImageResource(R.drawable.button_game_history_completion_time)
                filteredList.sortBy {it.id}
                lvGameHistory.adapter = GameHistoryAdapter(this, filteredList)
            }
        }

        lvGameHistory.adapter = GameHistoryAdapter(this, previousGamesList)
    }

    private fun loadPastGameData(sharedPreferences: SharedPreferences) {
        var count = sharedPreferences.getInt(GameBoard.Constants.NUMBER_OF_GAMES_PLAYED, 0)

        for (i in 1..count) {
            val pastGameDifficulty = sharedPreferences.getString("${GameBoard.Constants.GAME_DIFFICULTY}$i", "")
            val pastNumberOfMoves = sharedPreferences.getInt("${GameBoard.Constants.NUMBER_OF_MOVES}$i", 0)
            val pastCompletionTime = sharedPreferences.getString("${GameBoard.Constants.COMPLETION_TIME}$i", "")
            val pastTimestamp= sharedPreferences.getString("${GameBoard.Constants.TIMESTAMP}$i", "")

            val pastGame = PreviousGame(
                "Game $i",
                pastGameDifficulty!!,
                pastNumberOfMoves!!,
                pastCompletionTime!!,
                pastTimestamp!!
            )

            previousGamesList.add(pastGame)
        }
    }

    private fun showAlertDialog(title: String, view: View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel") { _, _ ->
            }
            .setPositiveButton("OK") { _, _ ->
                positiveClickListener.onClick(null)
            }.show()
    }
}
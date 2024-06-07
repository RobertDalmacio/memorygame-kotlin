package com.example.memorygame

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.models.BoardSize
import com.example.memorygame.models.MemoryGame
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class GameBoard : AppCompatActivity() {
    companion object {
        private const val CREATE_REQUEST_CODE = 248
    }

    private lateinit var clRoot: ConstraintLayout
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView
    private lateinit var tvTimer: TextView

    private lateinit var memoryGame: MemoryGame
    private lateinit var adapter: GameBoardAdapter
    private var boardSize: BoardSize = BoardSize.EASY

    private var isRunning = false
    private var timerSeconds = 0

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            timerSeconds++
            val hours = timerSeconds / 3600
            val minutes = (timerSeconds % 3600) / 60
            val seconds = timerSeconds % 60

            val time = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            tvTimer.text = time

            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_board)

        clRoot = findViewById(R.id.clRoot)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)
        tvTimer = findViewById(R.id.tvTimer)

        val fromMainMenu = intent.getBooleanExtra("fromMainMenu", false)
        val easyModeSelected = intent.getBooleanExtra("easyModeSelected", true)
        val mediumModeSelected = intent.getBooleanExtra("mediumModeSelected", false)
        val hardModeSelected = intent.getBooleanExtra("hardModeSelected", false)

        if (fromMainMenu) {
            if (easyModeSelected && !mediumModeSelected && !hardModeSelected)
                boardSize = BoardSize.EASY
            else if (!easyModeSelected && mediumModeSelected && !hardModeSelected)
                boardSize = BoardSize.MEDIUM
            else if (!easyModeSelected && !mediumModeSelected && hardModeSelected)
                boardSize = BoardSize.HARD
        }

        setupBoard()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_game_board, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        stopTimer()
        when (item.itemId) {
            R.id.mi_refresh -> {
                if (memoryGame.getNumOfCardFlips() > 0 && !memoryGame.haveWonGame()) {
                    showAlertDialog("Restart current game?", null, View.OnClickListener {
                        setupBoard()
                    })
                } else {
                    setupBoard()
                }
                return true
            }
            R.id.mi_new_size -> {
                showNewSizeDialog()
                return true
            }
            R.id.mi_game_history -> {
                showGameHistoryDialog()
                return true
            }
            R.id.mi_main_menu -> {
                showMainMenuDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startTimer() {
        if (!isRunning) {
            handler.postDelayed(runnable, 1000)
            isRunning = true
        }
    }

    private fun stopTimer() {
        if (isRunning) {
            handler.removeCallbacks(runnable)
            isRunning = false
        }
    }

    private fun resetTimer() {
        stopTimer()
        timerSeconds = 0
        tvTimer.text = "00:00:00"
    }


    private fun showMainMenuDialog() {
        showAlertDialog("Quit playing current game and navigate to Main Menu?", null, View.OnClickListener {
            // Navigate to Game History Activity
            val intent = Intent(this, MainMenu::class.java)
            startActivityForResult(intent, CREATE_REQUEST_CODE)
        })
    }

    private fun showGameHistoryDialog() {
        showAlertDialog("Quit playing current game and navigate to Game History?", null, View.OnClickListener {
            // Navigate to Game History Activity
            val intent = Intent(this, GameHistory::class.java)
            startActivityForResult(intent, CREATE_REQUEST_CODE)
        })
    }

    private fun showNewSizeDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when (boardSize) {
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbHard)
        }
        showAlertDialog("Choose new size", boardSizeView, View.OnClickListener {
            // Set a new value for the board size
            boardSize = when (radioGroupSize.checkedRadioButtonId) {
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            setupBoard()
        })
    }

    private fun showAlertDialog(title: String, view: View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel") { _, _ ->
                startTimer()
            }
            .setPositiveButton("OK") { _, _ ->
                positiveClickListener.onClick(null)
            }.show()
    }

    private fun setupBoard() {
        when (boardSize) {
            BoardSize.EASY -> {
                tvNumMoves.text = "Easy: 4 X 2"
                tvNumPairs.text = "Pairs: 0 / 4"
            }
            BoardSize.MEDIUM -> {
                tvNumMoves.text = "Medium: 6 X 3"
                tvNumPairs.text = "Pairs: 0 / 9"
            }
            BoardSize.HARD -> {
                tvNumMoves.text = "Hard: 6 X 4"
                tvNumPairs.text = "Pairs: 0 / 12"
            }
        }
        memoryGame = MemoryGame(boardSize)
        adapter = GameBoardAdapter(this, boardSize, memoryGame.cards, object: GameBoardAdapter.CardClickListener {
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)
            }
        })
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())

        resetTimer()
    }

    private fun saveGame(sharedPrefs: SharedPreferences) {
        var numberOfGamesPlayed = sharedPrefs.getInt(Constants.NUMBER_OF_GAMES_PLAYED, 0)
        numberOfGamesPlayed++

        with (sharedPrefs.edit()) {
            putInt(Constants.NUMBER_OF_GAMES_PLAYED, numberOfGamesPlayed)

            putString(Constants.GAME_DIFFICULTY + numberOfGamesPlayed, boardSize.toString())

            putInt(Constants.NUMBER_OF_MOVES + numberOfGamesPlayed, memoryGame.getNumMoves())

            putString(Constants.COMPLETION_TIME + numberOfGamesPlayed, tvTimer.text.toString())

            val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/y H:m:ss"))
            putString(Constants.TIMESTAMP + numberOfGamesPlayed, dateTime)

            apply()
        }
    }

    private fun updateGameWithFlip(position: Int) {
        if (memoryGame.getNumOfCardFlips() == 1) {
            startTimer()
        }

        if (memoryGame.haveWonGame()) {
            // Alert the user of an invalid move
            Snackbar.make(clRoot, "You already Won!", Snackbar.LENGTH_LONG).show()
            return
        }
        if (memoryGame.isCardFaceUp(position)) {
            // Alert the user of an invalid move
            Snackbar.make(clRoot, "Invalid Move!", Snackbar.LENGTH_SHORT).show()
            return
        }
        // Actually flip over the card
        if (memoryGame.flipCard(position)) {
            tvNumPairs.text = "Pairs: ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
            if (memoryGame.haveWonGame()) {
                Snackbar.make(clRoot, "You won! Congratulations!", Snackbar.LENGTH_LONG).show()
                stopTimer()
                saveGame(this.getSharedPreferences("game_data", Context.MODE_PRIVATE))
            }
        }
        tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }

    class Constants {
        companion object {
            const val NUMBER_OF_GAMES_PLAYED = "numberOfGamesPlayed"
            const val NUMBER_OF_MOVES = "numberOfMoves"
            const val COMPLETION_TIME = "completionTime"
            const val GAME_DIFFICULTY = "gameDifficulty"
            const val TIMESTAMP = "timestamp"
        }
    }
}
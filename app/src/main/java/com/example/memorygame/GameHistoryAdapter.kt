package com.example.memorygame

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.memorygame.models.PreviousGame

class GameHistoryAdapter(
    private val context : Activity,
    private val arrayList: ArrayList<PreviousGame>) : ArrayAdapter<PreviousGame>(context, R.layout.list_game_item, arrayList)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.list_game_item, null)

        val numberOfMovesData : TextView = view.findViewById(R.id.tv_numberOfMovesData)
        val gameDifficultyData : TextView = view.findViewById(R.id.tv_gameDifficultyData)
        val completionTimeData : TextView = view.findViewById(R.id.tv_completionTimeData)
        val timestampData : TextView = view.findViewById(R.id.tv_dateCompletedData)

        numberOfMovesData.text = "Moves: " + arrayList[position].numberOfMoves.toString()
        gameDifficultyData.text = arrayList[position].gameDifficulty
        completionTimeData.text = "Duration: " + arrayList[position].completionTime
        timestampData.text = arrayList[position].timestamp

        return view
    }
}
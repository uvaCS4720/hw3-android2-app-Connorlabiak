package edu.nd.pmcburne.hwapp.one

import androidx.room.*

@Entity(tableName = "game_table")
data class Game(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val gender: String,
    val home: String,
    val homeScore: String,
    val away: String,
    val awayScore: String,
    val gameState: String,
    val startTime: String? = null,
    val curPeriod: String? = null,
    val timeRem: String? = null,
    val winner: String? = null
)

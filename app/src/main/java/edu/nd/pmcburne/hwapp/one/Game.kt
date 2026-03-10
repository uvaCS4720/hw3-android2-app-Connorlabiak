package edu.nd.pmcburne.hwapp.one

import androidx.room.*

@Entity(tableName = "game_table")
data class Game(
    @PrimaryKey val id: Int,
    val home: String,
    val homeScore: Int,
    val away: String,
    val awayScore: Int,
    val progress: String,
    val startTime: String? = null,
    val curPeriod: String? = null,
    val timeRem: Int? = null,
    val winner: String? = null
)

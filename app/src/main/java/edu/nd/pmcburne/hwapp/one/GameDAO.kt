package edu.nd.pmcburne.hwapp.one

import androidx.room.*

@Dao
interface GameDAO {
    @Query("SELECT * FROM game_table")
    suspend fun getAll(): List<Game>

    @Query("SELECT * FROM game_table WHERE date = :date AND gender = :gender")
    suspend fun getAllFromDate(gender: String, date: String): List<Game>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<Game>)
}
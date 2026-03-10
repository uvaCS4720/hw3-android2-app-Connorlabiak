package edu.nd.pmcburne.hwapp.one

import androidx.room.*

@Dao
interface GameDAO {
    @Query("SELECT * FROM game_table")
    fun getAll(): List<Game>

    @Query("SELECT * FROM game_table WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Game>

}
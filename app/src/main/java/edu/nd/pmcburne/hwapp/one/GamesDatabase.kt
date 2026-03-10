package edu.nd.pmcburne.hwapp.one

import androidx.room.*

@Database(entities = [Game::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun gameDao(): GameDAO
}
package edu.nd.pmcburne.hwapp.one.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameDao {
    @Query("SELECT * FROM games WHERE date = :date AND gender = :gender ORDER BY id")
    suspend fun getGamesByDateAndGender(date: String, gender: String): List<GameEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntity>)
    
    @Query("DELETE FROM games WHERE date = :date AND gender = :gender")
    suspend fun deleteGamesByDateAndGender(date: String, gender: String)
}

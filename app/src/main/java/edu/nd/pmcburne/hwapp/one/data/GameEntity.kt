package edu.nd.pmcburne.hwapp.one.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey
    val id: String,
    val date: String, // Format: yyyy-MM-dd
    val gender: String, // "men" or "women"
    val awayTeamName: String,
    val homeTeamName: String,
    val awayScore: Int?,
    val homeScore: Int?,
    val status: String, // "upcoming", "in-progress", "finished"
    val startTime: String?,
    val period: String?,
    val timeRemaining: String?,
    val winner: String? // "away", "home", or null
)

package edu.nd.pmcburne.hwapp.one.data

import com.google.gson.annotations.SerializedName

// API Response Models - Updated to match actual API structure
data class ScoreboardResponse(
    @SerializedName("games") val games: List<GameWrapper>?
)

data class GameWrapper(
    @SerializedName("game") val game: GameData?
)

data class GameData(
    @SerializedName("gameID") val gameID: String,
    @SerializedName("away") val away: TeamData?,
    @SerializedName("home") val home: TeamData?,
    @SerializedName("gameState") val gameState: String?, // "final", "live", "pre"
    @SerializedName("currentPeriod") val currentPeriod: String?, // "FINAL", "1st", "2nd", "HALFTIME", etc.
    @SerializedName("contestClock") val contestClock: String?, // "09:37", "0:00", etc.
    @SerializedName("startTime") val startTime: String?, // "11:30 AM ET"
    @SerializedName("startDate") val startDate: String? // "03/13/2026"
)

data class TeamData(
    @SerializedName("score") val score: String?,
    @SerializedName("names") val names: TeamNames?,
    @SerializedName("winner") val winner: Boolean?
)

data class TeamNames(
    @SerializedName("short") val short: String?
)

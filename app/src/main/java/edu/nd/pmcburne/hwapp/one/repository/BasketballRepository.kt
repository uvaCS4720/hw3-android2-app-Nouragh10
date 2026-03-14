package edu.nd.pmcburne.hwapp.one.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import edu.nd.pmcburne.hwapp.one.data.*
import edu.nd.pmcburne.hwapp.one.network.RetrofitClient
import java.text.SimpleDateFormat
import java.util.*

class BasketballRepository(private val context: Context) {
    private val apiService = RetrofitClient.apiService
    private val database = AppDatabase.getDatabase(context)
    private val gameDao = database.gameDao()
    
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    suspend fun getGames(date: String, gender: String): List<Game> {
        // Always try to get from database first
        val cachedGames = gameDao.getGamesByDateAndGender(date, gender)
        
        // If network is available, fetch from API and update database
        if (isNetworkAvailable()) {
            try {
                val year = date.substring(0, 4)
                val month = date.substring(5, 7)
                val day = date.substring(8, 10)
                
                val response = apiService.getScoreboard(gender, year, month, day)
                val games = parseApiResponse(response, date, gender)
                
                // Update database
                val entities = games.map { game ->
                    GameEntity(
                        id = game.id,
                        date = game.date,
                        gender = game.gender,
                        awayTeamName = game.awayTeamName,
                        homeTeamName = game.homeTeamName,
                        awayScore = game.awayScore,
                        homeScore = game.homeScore,
                        status = when (game.status) {
                            GameStatus.UPCOMING -> "upcoming"
                            GameStatus.IN_PROGRESS -> "in-progress"
                            GameStatus.FINISHED -> "finished"
                        },
                        startTime = game.startTime,
                        period = game.period,
                        timeRemaining = game.timeRemaining,
                        winner = game.winner
                    )
                }
                gameDao.insertGames(entities)
                
                return games
            } catch (e: Exception) {
                e.printStackTrace()
                // If API call fails, return cached data
                return cachedGames.map { it.toGame() }
            }
        }
        
        // Return cached data if no network
        return cachedGames.map { it.toGame() }
    }
    
    private fun parseApiResponse(response: ScoreboardResponse, date: String, gender: String): List<Game> {
        val games = mutableListOf<Game>()
        
        response.games?.forEach { gameWrapper ->
            val gameData = gameWrapper.game ?: return@forEach
            
            val awayTeam = gameData.away
            val homeTeam = gameData.home
            
            if (awayTeam == null || homeTeam == null) return@forEach
            
            val awayTeamName = awayTeam.names?.short ?: return@forEach
            val homeTeamName = homeTeam.names?.short ?: return@forEach
            
            // Determine game status
            val gameState = gameData.gameState ?: "pre"
            val gameStatus = when {
                gameState == "final" -> GameStatus.FINISHED
                gameState == "live" -> GameStatus.IN_PROGRESS
                else -> GameStatus.UPCOMING
            }
            
            // Parse scores
            val awayScore = awayTeam.score?.takeIf { it.isNotBlank() }?.toIntOrNull()
            val homeScore = homeTeam.score?.takeIf { it.isNotBlank() }?.toIntOrNull()
            
            // Parse period
            val period = when (gameStatus) {
                GameStatus.IN_PROGRESS -> {
                    val currentPeriod = gameData.currentPeriod ?: ""
                    when {
                        currentPeriod.contains("HALFTIME", ignoreCase = true) -> "Halftime"
                        currentPeriod == "1st" -> if (gender == "women") "Q1" else "1st Half"
                        currentPeriod == "2nd" -> if (gender == "women") "Q2" else "2nd Half"
                        currentPeriod.contains("3rd", ignoreCase = true) -> if (gender == "women") "Q3" else null
                        currentPeriod.contains("4th", ignoreCase = true) -> if (gender == "women") "Q4" else null
                        else -> currentPeriod
                    }
                }
                else -> null
            }
            
            // Parse time remaining
            val timeRemaining = when (gameStatus) {
                GameStatus.IN_PROGRESS -> {
                    val clock = gameData.contestClock ?: "0:00"
                    if (clock != "0:00" && !clock.contains("HALFTIME", ignoreCase = true)) {
                        clock
                    } else null
                }
                else -> null
            }
            
            // Start time for upcoming games
            val startTime = when (gameStatus) {
                GameStatus.UPCOMING -> gameData.startTime ?: "TBD"
                else -> null
            }
            
            // Determine winner
            val winner = when (gameStatus) {
                GameStatus.FINISHED -> {
                    when {
                        awayTeam.winner == true -> "away"
                        homeTeam.winner == true -> "home"
                        awayScore != null && homeScore != null -> {
                            if (awayScore > homeScore) "away" else "home"
                        }
                        else -> null
                    }
                }
                else -> null
            }
            
            games.add(
                Game(
                    id = gameData.gameID,
                    date = date,
                    gender = gender,
                    awayTeamName = awayTeamName,
                    homeTeamName = homeTeamName,
                    awayScore = awayScore,
                    homeScore = homeScore,
                    status = gameStatus,
                    startTime = startTime,
                    period = period,
                    timeRemaining = timeRemaining,
                    winner = winner
                )
            )
        }
        
        return games
    }
}

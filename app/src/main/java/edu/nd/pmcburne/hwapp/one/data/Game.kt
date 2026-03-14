package edu.nd.pmcburne.hwapp.one.data

data class Game(
    val id: String,
    val date: String,
    val gender: String,
    val awayTeamName: String,
    val homeTeamName: String,
    val awayScore: Int?,
    val homeScore: Int?,
    val status: GameStatus,
    val startTime: String?,
    val period: String?,
    val timeRemaining: String?,
    val winner: String?
)

enum class GameStatus {
    UPCOMING,
    IN_PROGRESS,
    FINISHED
}

fun GameEntity.toGame(): Game {
    return Game(
        id = id,
        date = date,
        gender = gender,
        awayTeamName = awayTeamName,
        homeTeamName = homeTeamName,
        awayScore = awayScore,
        homeScore = homeScore,
        status = when (status) {
            "upcoming" -> GameStatus.UPCOMING
            "in-progress" -> GameStatus.IN_PROGRESS
            "finished" -> GameStatus.FINISHED
            else -> GameStatus.UPCOMING
        },
        startTime = startTime,
        period = period,
        timeRemaining = timeRemaining,
        winner = winner
    )
}

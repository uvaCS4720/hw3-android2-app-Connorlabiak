package edu.nd.pmcburne.hwapp.one.responseObjects

data class GameX(
    val away: Away,
    val bracketId: String,
    val bracketRegion: String,
    val bracketRound: String,
    val contestClock: String,
    val contestName: String,
    val currentPeriod: String,
    val finalMessage: String,
    val gameID: String,
    val gameState: String,
    val home: Home,
    val liveVideoEnabled: Boolean,
    val network: String,
    val startDate: String,
    val startTime: String,
    val startTimeEpoch: String,
    val title: String,
    val url: String,
    val videoState: String
)
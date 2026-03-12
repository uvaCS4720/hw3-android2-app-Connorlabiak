package edu.nd.pmcburne.hwapp.one.responseObjects

data class Home(
    val conferences: List<Conference>,
    val description: String,
    val names: Names,
    val rank: String,
    val score: String,
    val seed: String,
    val winner: Boolean
)
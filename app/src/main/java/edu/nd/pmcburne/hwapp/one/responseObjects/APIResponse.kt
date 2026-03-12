package edu.nd.pmcburne.hwapp.one.responseObjects

//Generated with Kotlin Data Class from JSON Plugin
data class APIResponse(
    val games: List<Game>,
    val inputMD5Sum: String,
    val instanceId: String,
    val updated_at: String
)
package edu.nd.pmcburne.hwapp.one

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hwapp.one.Game
import edu.nd.pmcburne.hwapp.one.responseObjects.APIResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import kotlin.collections.List

class MainViewModel(application: Application): AndroidViewModel(application) {
    private val dao = GamesDatabase.getDatabase(application.applicationContext).gameDao()
    private val _currentList: MutableStateFlow<List<Game>> = MutableStateFlow(emptyList())
    val currentList: StateFlow<List<Game>> = _currentList.asStateFlow()

    private val _isMen: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isMen: StateFlow<Boolean> = _isMen.asStateFlow()
    fun toggleIsMen() {
        _isMen.value = !_isMen.value
        updateGames(
            gender = if (_isMen.value) "men" else "women",
            date = _curDate.value
        )
    }

    private val _curDate: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    val curDate: StateFlow<LocalDate> = _curDate.asStateFlow()
    @OptIn(ExperimentalMaterial3Api::class)
    fun setNewDate(millis: Long?) {
        _curDate.value = Instant.ofEpochMilli(millis?: 0)
            .atZone(ZoneId.of("UTC"))
            .toLocalDate()
        Log.i("NEW Date", Instant.ofEpochMilli(millis?: 0)
            .atZone(ZoneId.of("UTC"))
            .toLocalDate().toString())
        updateGames(
            gender = if (_isMen.value) "men" else "women",
            date = _curDate.value
        )
    }

    private val _showDatePicker: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showDatePicker: StateFlow<Boolean> = _showDatePicker.asStateFlow()
    fun toggleDatePicker() {_showDatePicker.value = !_showDatePicker.value}

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        updateGames("men", LocalDate.now())
    }

    fun updateGames(gender: String, date: LocalDate) {
        Log.i("", "updating games with gender: $gender and date: $date")
        viewModelScope.launch {
            _isLoading.value = true
            if (checkInternetConnection()) {
                _currentList.value = convertResponseToGame(
                    gender = gender,
                    response = getGamesFromAPI(gender, date)
                )
                Log.i("Updated local games list.", _currentList.value.toString())
                saveGamesToDB()
            }
            else
                updateCurrentGamesFromDB(gender, date)
            _isLoading.value = false
        }
    }

    //Asked Gemini how to check internet connection.
    private fun checkInternetConnection(): Boolean {
        val connectivityManager = application.applicationContext
            .getSystemService(ConnectivityManager::class.java) ?: return false

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun convertResponseToGame(gender: String, response: APIResponse): List<Game> {
        val returnList: MutableList<Game> = mutableListOf()
        response.games.forEach {gamePlaceholder ->
            val gamex = gamePlaceholder.game
            returnList.add(Game(
                gender = gender,
                date = gamex.startDate,
                home = gamex.home.names.short,
                homeScore = gamex.home.score,
                away = gamex.away.names.short,
                awayScore = gamex.away.score,
                gameState = gamex.gameState,
                startTime = gamex.startTime,
                curPeriod = gamex.currentPeriod,
                timeRem = gamex.contestClock,
                winner = if (gamex.home.winner) gamex.home.names.short else if (gamex.away.winner) gamex.away.names.short else null
            ))
        }
        return returnList
    }

    private suspend fun getGamesFromAPI(gender: String, date: LocalDate): APIResponse {
        val year = date.year.toString()
        val month = date.monthValue.toString()
        val day = date.dayOfMonth.toString()
        return Requester.api.getGames(
            gender = gender,
            year = year,
            month = if (month.length == 1) "0"+month else month,
            day = if (day.length == 1) "0"+day else day
        )
    }

    private suspend fun saveGamesToDB() {
        dao.insertGames(_currentList.value)
    }

    private suspend fun updateCurrentGamesFromDB(gender: String, date: LocalDate) {
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        _currentList.value = dao.getAllFromDate(
            gender = gender,
            date = formatter.format(date)
        )
    }
}
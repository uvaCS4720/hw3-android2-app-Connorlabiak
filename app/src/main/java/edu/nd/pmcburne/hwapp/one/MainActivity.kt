package edu.nd.pmcburne.hwapp.one

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.nd.pmcburne.hwapp.one.ui.theme.HWStarterRepoTheme
import java.time.Instant
import java.time.ZoneId

class MainActivity : ComponentActivity() {
    val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentList by viewModel.currentList.collectAsStateWithLifecycle()
            val isMen by viewModel.isMen.collectAsStateWithLifecycle()
            val curDate by viewModel.curDate.collectAsStateWithLifecycle()
            val showDatePicker by viewModel.showDatePicker.collectAsStateWithLifecycle()
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = curDate
                    .atStartOfDay(ZoneId.of("UTC"))
                    .toInstant()
                    .toEpochMilli()
            )
            val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
            HWStarterRepoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column {
                            Toolbar(
                                isMen = isMen,
                                curDatePickerState = datePickerState,
                                showDatePicker = showDatePicker,
                                onGenderChange = { viewModel.toggleIsMen() },
                                onDateChange = { viewModel.setNewDate(datePickerState.selectedDateMillis) },
                                onToggleDataPickerState = {
                                    viewModel.toggleDatePicker()
                                },
                                onRefresh = {
                                    viewModel.updateGames(
                                        gender = if (isMen) "men" else "women",
                                        date = curDate
                                    )
                                },
                                isLoading = isLoading
                            )
                            if (currentList.isEmpty()) {
                                Text("No Games to Display", modifier = Modifier.align(Alignment.CenterHorizontally))
                            }
                            else
                                GamesList(currentList)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun GamesList(list: List<Game>) {
        LazyColumn(modifier = Modifier.padding(14.dp)) {
            itemsIndexed(items = list) { index, item ->
                GamePane(item)
            }

        }
    }

    @Composable
    fun GamePane(game: Game) {
        Surface(
            modifier = Modifier.padding(vertical = 6.dp),
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                Row {
                    Text(
                        text = game.home + " vs " + game.away,

                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (game.gameState == "final")
                        Text("Final")
                    if (game.gameState == "pre")
                        Text(game.startTime ?: "Unknown")
                    if (game.gameState == "live")
                        Text((game.curPeriod?:"//") + " " + game.timeRem)

                }
                if (game.winner == null) {
                    Text(
                        text = game.home + " " + game.homeScore,
                        modifier = Modifier
                            .padding(top = 7.dp)
                            .padding(start = 25.dp)
                    )
                    Text(
                        text = game.away + " " + game.awayScore,
                        modifier = Modifier.padding(start = 25.dp)
                    )
                }
                else {
                    Text(
                        text = game.home + " " + game.homeScore + if (game.winner == game.home) "\uD83C\uDFC6" else "",
                        modifier = Modifier
                            .padding(top = 7.dp)
                            .padding(start = 25.dp)
                    )
                    Text(
                        text = game.away + " " + game.awayScore + if (game.winner == game.away) "\uD83C\uDFC6" else "",
                        modifier = Modifier.padding(start = 25.dp)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Toolbar(
        isLoading: Boolean,
        isMen: Boolean,
        curDatePickerState: DatePickerState,
        showDatePicker: Boolean,
        onGenderChange: () -> Unit,
        onDateChange: () -> Unit,
        onToggleDataPickerState: () -> Unit,
        onRefresh: () -> Unit
    ) {
        TopAppBar(
            modifier = Modifier.height(100.dp),
            title = { Text("Scoreboard for ${
                Instant.ofEpochMilli(curDatePickerState.selectedDateMillis?: 0)
                .atZone(ZoneId.of("UTC"))
                .toLocalDate()
            }") },
            actions = {
                MenWomenButton(isMen) {onGenderChange()}
                DatePicker(
                    showDate = showDatePicker,
                    curState = curDatePickerState,
                    onDismiss = {
                        onToggleDataPickerState()
                        onDateChange()
                    },
                    onShow = {onToggleDataPickerState()}
                )
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        RefreshButton { onRefresh() }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }

    @Composable
    fun MenWomenButton(isMen: Boolean, onClick: () -> Unit) {
        IconButton(
            onClick = {
                onClick()
            },
            content = {
                if (isMen)
                    Icon(
                        painter = painterResource(R.drawable.face_24px),
                        contentDescription = "Edit Item"
                    )
                else
                    Icon(
                        painter = painterResource(R.drawable.face_4_24px),
                        contentDescription = "Edit Item"
                    )
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
//Largely used code from https://developer.android.com/develop/ui/compose/components/datepickers
    fun DatePicker(
        showDate: Boolean,
        curState: DatePickerState,
        onDismiss: () -> Unit,
        onShow: () -> Unit
    ) {
        if (showDate) {
            DatePickerDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    TextButton(onClick = {
                        onDismiss()
                    }) {
                        Text("OK")
                    }
                },
            ) {
                DatePicker(state = curState)
            }
        }
        IconButton(
            onClick = {
                onShow()
            },
            content = {
                Icon(
                    painter = painterResource(R.drawable.date_range_24px),
                    contentDescription = "Edit Item"
                )
            }
        )
    }

    @Composable
    fun RefreshButton(onClick: () -> Unit) {
        IconButton(
            onClick = {
                onClick()
            },
            content = {
                Icon(
                    painter = painterResource(R.drawable.refresh_24px),
                    contentDescription = "Edit Item"
                )
            }
        )
    }
}
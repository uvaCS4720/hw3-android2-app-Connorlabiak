package edu.nd.pmcburne.hwapp.one

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.nd.pmcburne.hwapp.one.ui.theme.HWStarterRepoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HWStarterRepoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) { Toolbar() }
                }
            }
        }
    }

    @Composable
    fun GamesList(list: List<Game>) {
        LazyColumn(modifier = Modifier.padding(14.dp)) {
            itemsIndexed(items = list) { index, item ->
                GamePane(
                    Game(
                        home = "UVA",
                        homeScore = 111,
                        away = "UNC",
                        awayScore = 101,
                        progress = "Finished",
                        winner = "UVA"
                    )
                )
            }

        }
    }

    @Composable
    fun GamePane(game: Game) {
        Surface(
            shape = MaterialTheme.shapes.large,
            shadowElevation = 1.dp,
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                Row {
                    Text(
                        text = game.home + " vs " + game.away,

                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (game.progress == "Finished")
                        Text("Final - " + game.winner + " won!")
                    if (game.progress == "Upcoming")
                        Text(game.startTime ?: "Unknown")
                    if (game.progress == "In Progress")
                        Text((game.curPeriod?:"//") + " " + game.timeRem)

                }
                Text(
                    text = game.home + " " + game.homeScore,
                    modifier = Modifier.padding(top = 7.dp).padding(start = 25.dp)
                )
                Text(
                    text = game.away + " " + game.awayScore,
                    modifier = Modifier.padding(start = 25.dp)
                )
            }
        }
    }

    @Preview
    @Composable
    fun GamePanePreview() {
        GamePane(Game(
            home = "UVA",
            homeScore =  111,
            away = "UNC",
            awayScore = 101,
            progress = "Finished",
            winner = "UVA"
        ))
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Toolbar() {
        var isMen by remember { mutableStateOf(true) }
        var curDatePickerState = rememberDatePickerState()
        var showDatePicker by remember { mutableStateOf(false) }
        TopAppBar(
            title = { Text("Scoreboard") },
            actions = {
                MenWomenButton(isMen) { newBool -> isMen = newBool }
                DatePicker(
                    showDate = showDatePicker,
                    curState = curDatePickerState,
                    onDateSelected = { newDateState -> curDatePickerState = newDateState },
                    onDismiss = { showDatePicker = !showDatePicker }
                )
                RefreshButton {}
            }
        )
    }

    @Composable
    fun MenWomenButton(isMen: Boolean, onClick: (Boolean) -> Unit) {
        IconButton(
            onClick = {
                onClick(!isMen)
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
        onDateSelected: (DatePickerState) -> Unit,
        onDismiss: () -> Unit
    ) {
        if (showDate) {
            DatePickerDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    TextButton(onClick = {
                        onDateSelected(curState)
                        onDismiss()
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = curState)
            }
        }
        IconButton(
            onClick = {
                onDismiss()
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
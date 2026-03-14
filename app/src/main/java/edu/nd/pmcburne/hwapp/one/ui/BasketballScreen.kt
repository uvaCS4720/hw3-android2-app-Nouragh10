package edu.nd.pmcburne.hwapp.one.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import edu.nd.pmcburne.hwapp.one.data.Game
import edu.nd.pmcburne.hwapp.one.data.GameStatus
import edu.nd.pmcburne.hwapp.one.viewmodel.BasketballViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasketballScreen(
    viewModel: BasketballViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedGender by viewModel.selectedGender.collectAsState()
    val games by viewModel.games.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var showDatePicker by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "College Basketball Scores",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Date Picker Button
        Button(
            onClick = { showDatePicker = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Select Date: ${formatDateForDisplay(selectedDate)}")
        }
        
        // Gender Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedGender == "men",
                onClick = { if (selectedGender != "men") viewModel.toggleGender() },
                label = { Text("Men's") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = selectedGender == "women",
                onClick = { if (selectedGender != "women") viewModel.toggleGender() },
                label = { Text("Women's") },
                modifier = Modifier.weight(1f)
            )
        }
        
        // Refresh Button
        Button(
            onClick = { 
                viewModel.refresh()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            enabled = !isLoading
        ) {
            Text("Refresh")
        }
        
        // Loading Indicator
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        // Games List
        if (!isLoading && games.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No games found for this date",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(games) { game ->
                GameCard(game = game)
            }
        }
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            initialDate = parseDate(selectedDate),
            onDateSelected = { date ->
                viewModel.setDate(formatDate(date))
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun GameCard(game: Game) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Away Team
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = game.awayTeamName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                if (game.status != GameStatus.UPCOMING) {
                    Text(
                        text = game.awayScore?.toString() ?: "-",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Home Team
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = game.homeTeamName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                if (game.status != GameStatus.UPCOMING) {
                    Text(
                        text = game.homeScore?.toString() ?: "-",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Status/Time Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                when (game.status) {
                    GameStatus.UPCOMING -> {
                        Text(
                            text = "Start: ${game.startTime ?: "TBD"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    GameStatus.IN_PROGRESS -> {
                        Column {
                            Text(
                                text = game.period ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = game.timeRemaining ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    GameStatus.FINISHED -> {
                        Text(
                            text = "Final",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        if (game.winner != null) {
                            val winnerName = if (game.winner == "away") game.awayTeamName else game.homeTeamName
                            Text(
                                text = "Winner: $winnerName",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: Calendar,
    onDateSelected: (Calendar) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.timeInMillis
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Date") },
        text = {
            DatePicker(state = datePickerState)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = millis
                        onDateSelected(calendar)
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun formatDateForDisplay(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

fun formatDate(calendar: Calendar): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(calendar.time)
}

fun parseDate(dateString: String): Calendar {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = sdf.parse(dateString) ?: Date()
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar
}

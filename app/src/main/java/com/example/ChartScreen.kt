package com.example

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale

@Composable
fun ChartScreen(
    onBack: () -> Unit,
    viewModel: BeerViewModel = viewModel(factory = BeerViewModelFactory((LocalContext.current.applicationContext as BeerApplication).database.beerDao()))
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val langCode by dataStoreManager.language.collectAsState(initial = "en")
    val currentLocale = remember(langCode) {
        when (langCode) {
            "ru" -> Locale("ru")
            "zh" -> Locale.SIMPLIFIED_CHINESE
            else -> Locale.ENGLISH
        }
    }

    val consumption by viewModel.consumption.collectAsState()
    
    val calendar = Calendar.getInstance()
    var displayMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var displayYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }

    val consumedDays = remember(consumption, displayMonth, displayYear) {
        consumption.map { item ->
            val cal = Calendar.getInstance().apply { timeInMillis = item.date }
            Triple(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR), cal.get(Calendar.DAY_OF_MONTH))
        }.toSet()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Top Navigation Header matching Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(
                onClick = onBack,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.backToReminder)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = strings.calendarTitle,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (displayMonth == 0) { displayMonth = 11; displayYear-- } else { displayMonth-- }
            }) { Text("<") }
            Text(
                text = "${DateFormatSymbols(currentLocale).months[displayMonth]} $displayYear",
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = {
                if (displayMonth == 11) { displayMonth = 0; displayYear++ } else { displayMonth++ }
            }) { Text(">") }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Weekday column headers
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            strings.weekdayHeaders.forEach { header ->
                Text(
                    text = header,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        val tempCal = Calendar.getInstance().apply { set(displayYear, displayMonth, 1) }
        val daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) - 1 // 1: Sunday, 2: Monday...

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.weight(1f)
        ) {
            // Empty slots for days before the 1st
            items(firstDayOfWeek) {
                Box(modifier = Modifier.size(40.dp))
            }
            items(daysInMonth) { day ->
                val dayOfMonth = day + 1
                val isConsumed = consumedDays.contains(Triple(displayMonth, displayYear, dayOfMonth))
                
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isConsumed) {
                        Canvas(modifier = Modifier.size(30.dp)) {
                            drawCircle(color = Color(0xFFFFC107))
                        }
                    }
                    Text(text = dayOfMonth.toString())
                }
            }
        }
    }
}

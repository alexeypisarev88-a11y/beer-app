package com.example

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Calendar

@Composable
fun ChartScreen(viewModel: BeerViewModel = viewModel(factory = BeerViewModelFactory((androidx.compose.ui.platform.LocalContext.current.applicationContext as BeerApplication).database.beerDao()))) {
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (displayMonth == 0) { displayMonth = 11; displayYear-- } else { displayMonth-- }
            }) { Text("<") }
            Text(
                text = "${java.text.DateFormatSymbols().months[displayMonth]} $displayYear",
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = {
                if (displayMonth == 11) { displayMonth = 0; displayYear++ } else { displayMonth++ }
            }) { Text(">") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        val tempCal = Calendar.getInstance().apply { set(displayYear, displayMonth, 1) }
        val daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) - 1 // 1: Sunday, 2: Monday...

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.weight(1f)
        ) {
            // Empty slots for days before the 1st
            items(firstDayOfWeek - 1) {
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
        
        Spacer(modifier = Modifier.height(16.dp))
        BeerGlassAnimation(isFilled = consumption.isNotEmpty())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.addConsumption(1) }, modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) {
            Text("Add 1 Beer")
        }
    }
}

package com.example

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) { Text("Back to Reminder") }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Dark Theme")
            Spacer(modifier = Modifier.weight(1f))
            Switch(checked = darkTheme, onCheckedChange = onThemeChange)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Reminder Time", style = MaterialTheme.typography.titleMedium)
        val context = androidx.compose.ui.platform.LocalContext.current
        val dataStoreManager = remember { DataStoreManager(context) }
        val reminderTime by dataStoreManager.reminderTime.collectAsState(initial = Pair(18, 0))
        
        Button(onClick = {
            android.app.TimePickerDialog(
                context,
                { _, hour, minute -> 
                    kotlinx.coroutines.GlobalScope.launch { dataStoreManager.saveReminderTime(hour, minute) }
                },
                reminderTime.first,
                reminderTime.second,
                true
            ).show()
        }) {
            Text("Set time: ${"%02d".format(reminderTime.first)}:${"%02d".format(reminderTime.second)}")
        }
    }
}

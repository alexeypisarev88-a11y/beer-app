package com.example

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.MyApplicationTheme
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
        val context = androidx.compose.ui.platform.LocalContext.current
        val dataStoreManager = remember { DataStoreManager(context) }
        val isDarkTheme by dataStoreManager.isDarkTheme.collectAsState(initial = false)
        val scope = rememberCoroutineScope()
        
        MyApplicationTheme(darkTheme = isDarkTheme) {
            val view = androidx.compose.ui.platform.LocalView.current
            androidx.compose.runtime.SideEffect {
                val window = (view.context as android.app.Activity).window
                androidx.core.view.WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
            }
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                BeerReminderScreen(
                    modifier = Modifier.padding(innerPadding),
                    isDarkTheme = isDarkTheme,
                    onThemeChange = { scope.launch { dataStoreManager.setDarkTheme(it) } }
                )
            }
        }
        }
    }
}

@Composable
fun BeerReminderScreen(modifier: Modifier = Modifier, isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val isEnabled by dataStoreManager.isEnabled.collectAsState(initial = false)
    val reminderTime by dataStoreManager.reminderTime.collectAsState(initial = Pair(18, 0))
    val scope = rememberCoroutineScope()

    var showChart by remember { mutableStateOf(false) }
    var showAchievements by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    LaunchedEffect(isEnabled, reminderTime) {
        if (isEnabled) {
            ReminderScheduler.scheduleReminder(context, reminderTime.first, reminderTime.second)
        } else {
            ReminderScheduler.cancelReminder(context)
        }
    }

    if (showChart) {
        androidx.activity.compose.BackHandler { showChart = false }
        Column(modifier = modifier) {
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { showChart = false }, modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) { Text("Back to Reminder") }
            Spacer(modifier = Modifier.height(16.dp))
            ChartScreen()
        }
    } else if (showAchievements) {
        androidx.activity.compose.BackHandler { showAchievements = false }
        Column(modifier = modifier) {
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { showAchievements = false }, modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) { Text("Back to Reminder") }
            Spacer(modifier = Modifier.height(16.dp))
            AchievementsScreen()
        }
    } else if (showSettings) {
        androidx.activity.compose.BackHandler { showSettings = false }
        SettingsScreen(onBack = { showSettings = false }, darkTheme = isDarkTheme, onThemeChange = onThemeChange)
    } else {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            scope.launch { dataStoreManager.setReminderEnabled(isGranted) }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val beerDao = (androidx.compose.ui.platform.LocalContext.current.applicationContext as BeerApplication).database.beerDao()
            val allConsumption by beerDao.getAllConsumption().collectAsState(initial = emptyList())

            BeerGlassAnimation(
                isFilled = allConsumption.isNotEmpty(), 
                modifier = Modifier.size(128.dp).clickable {
                    scope.launch {
                        beerDao.insertConsumption(BeerConsumption(date = System.currentTimeMillis(), count = 1))
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Beer Reminder", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Switch(
                checked = isEnabled,
                onCheckedChange = { checked ->
                    if (checked) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                        ) {
                            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            scope.launch { dataStoreManager.setReminderEnabled(true) }
                        }
                    } else {
                        scope.launch { dataStoreManager.setReminderEnabled(false) }
                    }
                },
                modifier = Modifier.testTag("reminder_switch")
            )
            Spacer(modifier = Modifier.height(16.dp))
            val buttonModifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
            Button(onClick = { showChart = true }, modifier = buttonModifier) {
                Text("Calendar")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { showAchievements = true }, modifier = buttonModifier) {
                Text("View Achievements")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { showSettings = true }, modifier = buttonModifier) {
                Text("Settings")
            }
        }
    }
}

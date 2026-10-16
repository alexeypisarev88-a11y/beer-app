package com.example

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
    var showChart by remember { mutableStateOf(false) }
    var showAchievements by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

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
        val context = androidx.compose.ui.platform.LocalContext.current
        val dataStoreManager = remember { DataStoreManager(context) }
        val isEnabled by dataStoreManager.isEnabled.collectAsState(initial = false)
        val reminderTime by dataStoreManager.reminderTime.collectAsState(initial = Pair(18, 0))
        val scope = rememberCoroutineScope()
        
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                scope.launch { dataStoreManager.setReminderEnabled(true) }
            }
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
            val scope = rememberCoroutineScope()
            val dataStoreManager = remember { DataStoreManager(context) }
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
                onCheckedChange = {
                    if (it && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        scope.launch { dataStoreManager.setReminderEnabled(it) }
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
            
            LaunchedEffect(isEnabled, reminderTime) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
                val intent = android.content.Intent(context, AlarmReceiver::class.java)
                val pendingIntent = android.app.PendingIntent.getBroadcast(
                    context, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
                )

                if (isEnabled) {
                    val calendar = java.util.Calendar.getInstance()
                    val target = java.util.Calendar.getInstance().apply {
                        set(java.util.Calendar.HOUR_OF_DAY, reminderTime.first)
                        set(java.util.Calendar.MINUTE, reminderTime.second)
                        set(java.util.Calendar.SECOND, 0)
                    }
                    if (target.before(calendar)) target.add(java.util.Calendar.DAY_OF_YEAR, 1)

                    try {
                        alarmManager.setRepeating(
                            android.app.AlarmManager.RTC_WAKEUP,
                            target.timeInMillis,
                            android.app.AlarmManager.INTERVAL_DAY,
                            pendingIntent
                        )
                    } catch (e: SecurityException) {
                        // Handle permission error
                    }
                } else {
                    alarmManager.cancel(pendingIntent)
                }
            }
        }
    }
}

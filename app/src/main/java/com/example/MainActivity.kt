package com.example

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            val dataStoreManager = remember { DataStoreManager(context) }
            val isDarkTheme by dataStoreManager.isDarkTheme.collectAsState(initial = false)
            val languageCode by dataStoreManager.language.collectAsState(initial = "en")
            val strings = remember(languageCode) { AppStrings.forLanguage(languageCode) }
            val scope = rememberCoroutineScope()
            
            CompositionLocalProvider(LocalAppStrings provides strings) {
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
}

@Composable
fun BeerReminderScreen(modifier: Modifier = Modifier, isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val strings = LocalAppStrings.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val isEnabled by dataStoreManager.isEnabled.collectAsState(initial = true)
    val reminderTime by dataStoreManager.reminderTime.collectAsState(initial = Pair(19, 0))
    val glassType by dataStoreManager.glassType.collectAsState(initial = BeerGlassType.PINT)
    val scope = rememberCoroutineScope()

    var showChart by remember { mutableStateOf(false) }
    var showAchievements by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showPermissionRationaleDialog by remember { mutableStateOf(false) }

    val notificationManagerCompat = remember(context) { NotificationManagerCompat.from(context) }

    fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED && notificationManagerCompat.areNotificationsEnabled()
        } else {
            notificationManagerCompat.areNotificationsEnabled()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scope.launch { dataStoreManager.setReminderEnabled(true) }
        } else {
            showPermissionRationaleDialog = true
        }
    }

    // Startup check: verify notification permission and prompt user if not granted
    LaunchedEffect(Unit) {
        if (!isNotificationPermissionGranted()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val postGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!postGranted) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    showPermissionRationaleDialog = true
                }
            } else {
                showPermissionRationaleDialog = true
            }
        }
    }

    // Auto-dismiss rationale dialog and sync if permissions are enabled in Settings
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (isNotificationPermissionGranted()) {
            showPermissionRationaleDialog = false
        }
    }

    LaunchedEffect(isEnabled, reminderTime) {
        if (isEnabled) {
            ReminderScheduler.scheduleReminder(context, reminderTime.first, reminderTime.second)
        } else {
            ReminderScheduler.cancelReminder(context)
        }
    }

    if (showPermissionRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionRationaleDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = strings.enableNotificationsTitle,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = strings.enableNotificationsText,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionRationaleDialog = false
                        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }
                        } else {
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val fallbackIntent = Intent(Settings.ACTION_SETTINGS)
                            context.startActivity(fallbackIntent)
                        }
                    }
                ) {
                    Text(strings.openSettings)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationaleDialog = false }) {
                    Text(strings.later)
                }
            }
        )
    }

    if (showChart) {
        androidx.activity.compose.BackHandler { showChart = false }
        ChartScreen(onBack = { showChart = false })
    } else if (showAchievements) {
        androidx.activity.compose.BackHandler { showAchievements = false }
        AchievementsScreen(onBack = { showAchievements = false })
    } else if (showSettings) {
        androidx.activity.compose.BackHandler { showSettings = false }
        SettingsScreen(onBack = { showSettings = false }, darkTheme = isDarkTheme, onThemeChange = onThemeChange)
    } else {
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

            val isConsumedToday = remember(allConsumption) {
                val todayCal = java.util.Calendar.getInstance()
                val todayYear = todayCal.get(java.util.Calendar.YEAR)
                val todayDay = todayCal.get(java.util.Calendar.DAY_OF_YEAR)
                allConsumption.any {
                    val cal = java.util.Calendar.getInstance().apply { timeInMillis = it.date }
                    cal.get(java.util.Calendar.YEAR) == todayYear && cal.get(java.util.Calendar.DAY_OF_YEAR) == todayDay
                }
            }

            BeerGlassAnimation(
                isFilled = isConsumedToday, 
                glassType = glassType,
                modifier = Modifier
                    .size(128.dp)
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) {
                        triggerPouringVibration(context)
                        scope.launch {
                            beerDao.insertConsumption(BeerConsumption(date = System.currentTimeMillis(), count = 1))
                        }
                    }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(strings.appName, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))
            val buttonModifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
            Button(onClick = { showChart = true }, modifier = buttonModifier) {
                Text(strings.calendar)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { showAchievements = true }, modifier = buttonModifier) {
                Text(strings.achievements)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { showSettings = true }, modifier = buttonModifier) {
                Text(strings.settings)
            }
        }
    }
}

fun triggerPouringVibration(context: Context) {
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? android.os.Vibrator
        }

        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Waveform: bubbling pulses that build up in frequency & intensity
                val timings = longArrayOf(0, 35, 45, 35, 45, 40, 40, 45, 35, 50, 30, 70)
                val amplitudes = intArrayOf(0, 35, 0, 55, 0, 80, 0, 110, 0, 140, 0, 190)
                vibrator.vibrate(android.os.VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 35, 45, 35, 45, 40, 40, 70), -1)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

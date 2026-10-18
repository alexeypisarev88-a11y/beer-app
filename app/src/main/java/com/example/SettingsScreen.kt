package com.example

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val isEnabled by dataStoreManager.isEnabled.collectAsState(initial = false)
    val reminderTime by dataStoreManager.reminderTime.collectAsState(initial = Pair(18, 0))
    val selectedGlassType by dataStoreManager.glassType.collectAsState(initial = BeerGlassType.PINT)
    val currentLangCode by dataStoreManager.language.collectAsState(initial = "en")
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        scope.launch { dataStoreManager.setReminderEnabled(isGranted) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Top Navigation Header
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
            text = strings.settingsTitle,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section 1: Notifications & Schedule (Must be first)
        Text(
            text = strings.sectionNotifications,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            val formattedTime = "${"%02d".format(reminderTime.first)}:${"%02d".format(reminderTime.second)}"
            val openTimePicker = {
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        scope.launch { dataStoreManager.saveReminderTime(hour, minute) }
                    },
                    reminderTime.first,
                    reminderTime.second,
                    true
                ).show()
            }

            SettingRow(
                icon = Icons.Default.Notifications,
                title = strings.dailyReminders,
                subtitle = if (isEnabled) strings.remindersActiveAt(formattedTime) else strings.remindersDisabled,
                trailing = {
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
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            SettingRow(
                icon = Icons.Default.Alarm,
                title = strings.reminderTime,
                subtitle = strings.dailyAlertAt(formattedTime),
                onClick = openTimePicker,
                trailing = {
                    SuggestionChip(
                        onClick = openTimePicker,
                        label = {
                            Text(
                                text = formattedTime,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 2: Language Selection
        Text(
            text = strings.sectionLanguage,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {
                AppLanguage.entries.forEachIndexed { index, appLang ->
                    val isSelected = appLang.code == currentLangCode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch { dataStoreManager.setLanguage(appLang) }
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (appLang) {
                                    AppLanguage.ENGLISH -> "🇬🇧"
                                    AppLanguage.RUSSIAN -> "🇷🇺"
                                    AppLanguage.CHINESE -> "🇨🇳"
                                },
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = appLang.nativeName,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            if (appLang.nativeName != appLang.title) {
                                Text(
                                    text = appLang.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                scope.launch { dataStoreManager.setLanguage(appLang) }
                            }
                        )
                    }

                    if (index < AppLanguage.entries.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 3: Appearance
        Text(
            text = strings.sectionAppearance,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            SettingRow(
                icon = Icons.Default.DarkMode,
                title = strings.darkTheme,
                subtitle = if (darkTheme) strings.darkThemeEnabled else strings.lightThemeEnabled,
                trailing = {
                    Switch(
                        checked = darkTheme,
                        onCheckedChange = onThemeChange
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 4: Glass Shape
        Text(
            text = strings.sectionGlassShape,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {
                BeerGlassType.entries.forEachIndexed { index, type ->
                    val isSelected = type == selectedGlassType
                    val (typeName, typeDesc) = when (type) {
                        BeerGlassType.PINT -> Pair(strings.pintName, strings.pintDesc)
                        BeerGlassType.MUG -> Pair(strings.mugName, strings.mugDesc)
                        BeerGlassType.WEIZEN -> Pair(strings.weizenName, strings.weizenDesc)
                        BeerGlassType.TULIP -> Pair(strings.tulipName, strings.tulipDesc)
                        BeerGlassType.BOOT -> Pair(strings.bootName, strings.bootDesc)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch { dataStoreManager.setGlassType(type) }
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            BeerGlassAnimation(
                                isFilled = true,
                                glassType = type,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = typeName,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = typeDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                scope.launch { dataStoreManager.setGlassType(type) }
                            }
                        )
                    }

                    if (index < BeerGlassType.entries.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }

        // Section 5: Developer Options (DEBUG only)
        if (BuildConfig.DEBUG) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = strings.sectionDevOptions,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                SettingRow(
                    icon = Icons.Default.BugReport,
                    title = strings.testNotification,
                    subtitle = strings.testNotificationSubtitle,
                    onClick = { ReminderScheduler.sendNotification(context) },
                    trailing = {
                        FilledTonalButton(
                            onClick = { ReminderScheduler.sendNotification(context) },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(strings.sendButton)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        trailing()
    }
}

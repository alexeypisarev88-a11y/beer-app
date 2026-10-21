package com.example

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val wakeLock = powerManager?.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "BeerReminder:AlarmWakeLock"
        )
        wakeLock?.acquire(30_000)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dataStoreManager = DataStoreManager(context)
                val isEnabled = dataStoreManager.isEnabled.first()
                val reminderTime = dataStoreManager.reminderTime.first()

                // Crucial: Always reschedule for tomorrow immediately
                if (isEnabled) {
                    ReminderScheduler.scheduleNextDayReminder(context, reminderTime.first, reminderTime.second)
                } else {
                    return@launch
                }

                // Check if already notified today
                val todayCal = Calendar.getInstance()
                val currentYear = todayCal.get(Calendar.YEAR)
                val currentDay = todayCal.get(Calendar.DAY_OF_YEAR)
                val lastNotified = dataStoreManager.lastNotified.first()
                if (lastNotified.first == currentYear && lastNotified.second == currentDay) {
                    return@launch
                }

                // Check if user already drank beer today
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val startOfDay = calendar.timeInMillis
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                val endOfDay = calendar.timeInMillis

                val db = (context.applicationContext as BeerApplication).database
                val consumedToday = try {
                    db.beerDao().hasConsumedToday(startOfDay, endOfDay)
                } catch (e: Exception) {
                    false
                }

                if (consumedToday) {
                    // Skip reminder if user already drank beer today
                    return@launch
                }

                // Check POST_NOTIFICATIONS on Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                    if (!hasPermission) {
                        return@launch
                    }
                }

                // Send notification
                ReminderScheduler.sendNotification(context)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    if (wakeLock?.isHeld == true) {
                        wakeLock.release()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                pendingResult.finish()
            }
        }
    }
}

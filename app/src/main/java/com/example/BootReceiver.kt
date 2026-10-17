package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val dataStoreManager = DataStoreManager(context)
                    val isEnabled = dataStoreManager.isEnabled.first()
                    if (isEnabled) {
                        val reminderTime = dataStoreManager.reminderTime.first()
                        ReminderScheduler.scheduleReminder(context, reminderTime.first, reminderTime.second)
                    }

                    val calendar = java.util.Calendar.getInstance().apply {
                        set(java.util.Calendar.HOUR_OF_DAY, 0)
                        set(java.util.Calendar.MINUTE, 0)
                        set(java.util.Calendar.SECOND, 0)
                        set(java.util.Calendar.MILLISECOND, 0)
                    }
                    val startOfDay = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
                    val endOfDay = calendar.timeInMillis
                    val db = (context.applicationContext as BeerApplication).database
                    val consumedToday = try { db.beerDao().hasConsumedToday(startOfDay, endOfDay) } catch (e: Exception) { false }
                    DynamicIconManager.updateAppIcon(context, consumedToday)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}

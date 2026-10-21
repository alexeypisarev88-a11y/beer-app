package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val wakeLock = powerManager?.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "BeerReminder:BootWakeLock"
        )
        wakeLock?.acquire(20_000)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dataStoreManager = DataStoreManager(context)
                val isEnabled = dataStoreManager.isEnabled.first()
                if (isEnabled) {
                    val reminderTime = dataStoreManager.reminderTime.first()
                    ReminderScheduler.scheduleReminder(context, reminderTime.first, reminderTime.second)
                }
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

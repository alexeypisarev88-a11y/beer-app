package com.example

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class BeerReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        return runBlocking {
            try {
                val dataStoreManager = DataStoreManager(applicationContext)
                val isEnabled = dataStoreManager.isEnabled.first()
                if (!isEnabled) {
                    return@runBlocking Result.success()
                }

                val reminderTime = dataStoreManager.reminderTime.first()

                // Check if already notified today
                val now = Calendar.getInstance()
                val currentYear = now.get(Calendar.YEAR)
                val currentDay = now.get(Calendar.DAY_OF_YEAR)
                val lastNotified = dataStoreManager.lastNotified.first()
                if (lastNotified.first == currentYear && lastNotified.second == currentDay) {
                    return@runBlocking Result.success()
                }

                // Check if already consumed today
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val startOfDay = calendar.timeInMillis
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                val endOfDay = calendar.timeInMillis

                val db = (applicationContext as BeerApplication).database
                val consumedToday = try {
                    db.beerDao().hasConsumedToday(startOfDay, endOfDay)
                } catch (e: Exception) {
                    false
                }

                if (consumedToday) {
                    return@runBlocking Result.success()
                }

                // Send notification fallback
                ReminderScheduler.sendNotification(applicationContext)

                // Reschedule for next day
                ReminderScheduler.scheduleNextDayReminder(applicationContext, reminderTime.first, reminderTime.second)

                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.retry()
            }
        }
    }
}

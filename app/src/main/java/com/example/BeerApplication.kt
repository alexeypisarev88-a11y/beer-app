package com.example

import android.app.Application
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BeerApplication : Application() {
    val database by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "beer_database"
        ).build()
    }

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dataStoreManager = DataStoreManager(this@BeerApplication)
                val isEnabled = dataStoreManager.isEnabled.first()
                if (isEnabled) {
                    val reminderTime = dataStoreManager.reminderTime.first()
                    ReminderScheduler.scheduleReminder(
                        this@BeerApplication,
                        reminderTime.first,
                        reminderTime.second
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

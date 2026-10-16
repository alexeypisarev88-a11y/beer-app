package com.example

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val db = (context.applicationContext as BeerApplication).database
        val today = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        
        val consumedToday = runBlocking {
            db.beerDao().getAllConsumption().first().any {
                val cal = java.util.Calendar.getInstance()
                cal.timeInMillis = it.date
                cal.get(java.util.Calendar.DAY_OF_YEAR) == today && cal.get(java.util.Calendar.YEAR) == year
            }
        }

        if (consumedToday) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "beer_reminder_channel"
        
        val channel = NotificationChannel(channelId, "Beer Reminder", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
        
        val activityIntent = android.content.Intent(context, MainActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            context, 
            0, 
            activityIntent, 
            android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Beer time!")
            .setContentText("It's time for a beer!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
            
        notificationManager.notify(1, notification)
    }
}

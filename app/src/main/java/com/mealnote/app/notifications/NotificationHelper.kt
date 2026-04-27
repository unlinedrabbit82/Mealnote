package com.mealnote.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mealnote.app.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val WATER_REMINDER_CHANNEL_ID = "water_reminder_channel"
        const val WATER_REMINDER_CHANNEL_NAME = "Water Reminders"
        const val MEAL_REMINDER_CHANNEL_ID = "meal_reminder_channel"
        const val MEAL_REMINDER_CHANNEL_NAME = "Meal Reminders"
    }

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val waterChannel = NotificationChannel(
                WATER_REMINDER_CHANNEL_ID,
                WATER_REMINDER_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to drink water"
                enableVibration(true)
            }

            val mealChannel = NotificationChannel(
                MEAL_REMINDER_CHANNEL_ID,
                MEAL_REMINDER_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to log meals"
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(waterChannel)
            notificationManager.createNotificationChannel(mealChannel)
        }
    }

    fun showWaterReminder(message: String) {
        val notification = NotificationCompat.Builder(context, WATER_REMINDER_CHANNEL_ID)
            .setContentTitle("💧 Drink Water")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(1, notification)
    }

    fun showMealReminder(message: String) {
        val notification = NotificationCompat.Builder(context, MEAL_REMINDER_CHANNEL_ID)
            .setContentTitle("🍽️ Log Your Meal")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_meal)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(2, notification)
    }
}
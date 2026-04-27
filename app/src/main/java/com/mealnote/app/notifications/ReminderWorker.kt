package com.mealnote.app.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mealnote.app.MealNoteApplication
import com.mealnote.app.R

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val reminderType = inputData.getString("type") ?: return Result.failure()
        val message = inputData.getString("message") ?: return Result.failure()

        sendNotification(reminderType, message)

        return Result.success()
    }

    private fun sendNotification(type: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(
            applicationContext,
            MealNoteApplication.CHANNEL_ID
        )
            .setContentTitle(getTitleForType(type))
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationId = if (type == "water") 1001 else 1002
        notificationManager.notify(notificationId, notification)
    }

    private fun getTitleForType(type: String): String {
        return when (type) {
            "water" -> "💧 Time to Hydrate!"
            "meal" -> "🍽️ Meal Time!"
            else -> "Reminder"
        }
    }
}
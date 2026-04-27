package com.mealnote.app.notifications

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.*
import com.mealnote.app.data.database.entities.UserSettings
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class ReminderScheduler(private val context: Context) {

    fun scheduleWaterReminders(settings: UserSettings) {
        cancelWaterReminders()

        if (!settings.enableWaterReminders) return

        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            settings.waterReminderIntervalHours.toLong(),
            TimeUnit.HOURS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .setInitialDelay(4, TimeUnit.HOURS)
            .setInputData(
                workDataOf(
                    "type" to "water",
                    "message" to "Time to hydrate! Add your water intake."
                )
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "water_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleMealReminders(settings: UserSettings) {
        cancelMealReminders()

        if (!settings.enableMealReminders) return

        settings.mealReminderTimes.forEach { timeString ->
            val time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))

            val now = LocalTime.now()
            val initialDelay = if (time.isAfter(now)) {
                java.time.Duration.between(now, time).toMillis()
            } else {
                java.time.Duration.between(now, time).toMillis() + TimeUnit.DAYS.toMillis(1)
            }

            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        "type" to "meal",
                        "message" to "Don't forget to log your meal!"
                    )
                )
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "meal_reminder_${timeString}",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    private fun cancelWaterReminders() {
        WorkManager.getInstance(context).cancelUniqueWork("water_reminder")
    }

    private fun cancelMealReminders() {
        WorkManager.getInstance(context).cancelAllWorkByTag("meal_reminder")
    }

    fun cancelAllReminders() {
        WorkManager.getInstance(context).cancelAllWork()
    }
}
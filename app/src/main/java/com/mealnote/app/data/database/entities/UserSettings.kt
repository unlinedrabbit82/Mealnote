package com.mealnote.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: String = "default",
    val dailyWaterGoalMl: Int = 2500,     // Default 2.5L
    var enableWaterReminders: Boolean = true,
    var enableMealReminders: Boolean = true,
    var waterReminderIntervalHours: Int = 2,
    var mealReminderTimes: List<String> = listOf("08:00", "12:00", "18:00"),
    val themePreference: String = "light"
)
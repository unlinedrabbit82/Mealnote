package com.mealnote.app.data.database.dao

import androidx.room.*
import com.mealnote.app.data.database.entities.UserSettings

@Dao
interface SettingsDao {

    @Query("SELECT * FROM user_settings WHERE id = 'default'")
    suspend fun getSettings(): UserSettings

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: UserSettings)

    @Query("UPDATE user_settings SET dailyWaterGoalMl = :goal WHERE id = 'default'")
    suspend fun updateWaterGoal(goal: Int)

    @Query("UPDATE user_settings SET enableWaterReminders = :enabled WHERE id = 'default'")
    suspend fun updateWaterReminderEnabled(enabled: Boolean)

    @Query("UPDATE user_settings SET enableMealReminders = :enabled WHERE id = 'default'")
    suspend fun updateMealReminderEnabled(enabled: Boolean)
}
package com.mealnote.app.data.repository

import com.mealnote.app.data.database.dao.SettingsDao
import com.mealnote.app.data.database.entities.UserSettings

class SettingsRepository(
    private val settingsDao: SettingsDao
) {
    suspend fun getSettings(): UserSettings {
        return settingsDao.getSettings()
    }

    suspend fun saveSettings(settings: UserSettings) {
        settingsDao.saveSettings(settings)
    }

    suspend fun updateWaterGoal(goalMl: Int) {
        settingsDao.updateWaterGoal(goalMl)
    }

    suspend fun updateWaterReminderEnabled(enabled: Boolean) {
        settingsDao.updateWaterReminderEnabled(enabled)
    }

    suspend fun updateMealReminderEnabled(enabled: Boolean) {
        settingsDao.updateMealReminderEnabled(enabled)
    }
}
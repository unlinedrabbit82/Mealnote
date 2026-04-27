package com.mealnote.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mealnote.app.data.database.entities.UserSettings
import com.mealnote.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository? = null
) : ViewModel() {

    // Use non-nullable UserSettings with a default value
    private val _settings = MutableStateFlow(
        UserSettings(
            dailyWaterGoalMl = 2500,
            enableWaterReminders = true,
            enableMealReminders = true,
            waterReminderIntervalHours = 2,
            mealReminderTimes = listOf("08:00", "12:00", "18:00"),
            themePreference = "light"
        )
    )
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()  // Non-nullable

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    fun updateWaterGoal(goalMl: Int) {
        android.util.Log.d("SettingsVM", "updateWaterGoal: $goalMl")
        _isSaving.value = true
        _settings.value = _settings.value.copy(dailyWaterGoalMl = goalMl)
        _isSaving.value = false

        repository?.let {
            viewModelScope.launch {
                it.updateWaterGoal(goalMl)
            }
        }
    }

    fun updateWaterReminders(enabled: Boolean) {
        android.util.Log.d("SettingsVM", "updateWaterReminders: $enabled")
        _settings.value = _settings.value.copy(enableWaterReminders = enabled)

        repository?.let {
            viewModelScope.launch {
                it.updateWaterReminderEnabled(enabled)
            }
        }
    }

    fun updateMealReminders(enabled: Boolean) {
        android.util.Log.d("SettingsVM", "updateMealReminders: $enabled")
        _settings.value = _settings.value.copy(enableMealReminders = enabled)

        repository?.let {
            viewModelScope.launch {
                it.updateMealReminderEnabled(enabled)
            }
        }
    }
}
package com.mealnote.app.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mealnote.app.data.database.entities.UserSettings
import com.mealnote.app.data.repository.SettingsRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _settings = MutableLiveData<UserSettings?>()
    val settings: LiveData<UserSettings?> = _settings

    private val _isSaving = MutableLiveData(false)
    val isSaving: LiveData<Boolean> = _isSaving

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    fun loadSettings() {
        viewModelScope.launch {
            try {
                val currentSettings = settingsRepository.getSettings()
                _settings.value = currentSettings
            } catch (e: Exception) {
                _message.value = "Failed to load settings: ${e.message}"
            }
        }
    }

    fun saveSettings(dailyGoalMl: Int, enableWaterReminders: Boolean, enableMealReminders: Boolean) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val currentSettings = settingsRepository.getSettings()
                val updatedSettings = currentSettings.copy(
                    dailyWaterGoalMl = dailyGoalMl,
                    enableWaterReminders = enableWaterReminders,
                    enableMealReminders = enableMealReminders
                )
                settingsRepository.saveSettings(updatedSettings)
                _settings.value = updatedSettings
                _message.value = "Settings saved successfully!"
            } catch (e: Exception) {
                _message.value = "Failed to save settings: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun updateThemePreference(theme: String) {
        viewModelScope.launch {
            try {
                val currentSettings = settingsRepository.getSettings()
                val updatedSettings = currentSettings.copy(themePreference = theme)
                settingsRepository.saveSettings(updatedSettings)
                _settings.value = updatedSettings
                applyTheme(theme)
            } catch (e: Exception) {
                _message.value = "Failed to update theme: ${e.message}"
            }
        }
    }

    private fun applyTheme(theme: String) {
        when (theme) {
            "light" -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
            )
            "dark" -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
            )
            else -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
        }
    }

    fun messageShown() {
        _message.value = null
    }
}
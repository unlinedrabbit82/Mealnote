package com.mealnote.app.ui.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class WaterViewModel(private val context: Context) : ViewModel() {

    private val prefs: SharedPreferences = context.getSharedPreferences("mealnote", Context.MODE_PRIVATE)

    private val _todayTotal = MutableStateFlow(0)
    val todayTotal: StateFlow<Int> = _todayTotal.asStateFlow()

    private val _dailyGoal = MutableStateFlow(2500)
    val dailyGoal: StateFlow<Int> = _dailyGoal.asStateFlow()

    // NEW: Lifetime water tracking
    private val _lifetimeTotal = MutableStateFlow(0)
    val lifetimeTotal: StateFlow<Int> = _lifetimeTotal.asStateFlow()

    private val _lifetimeOz = MutableStateFlow(0.0)
    val lifetimeOz: StateFlow<Double> = _lifetimeOz.asStateFlow()

    private val _lifetimeBottles = MutableStateFlow(0.0)
    val lifetimeBottles: StateFlow<Double> = _lifetimeBottles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadDailyGoal()
        loadTodayTotal()
        loadLifetimeTotal()
        checkAndResetIfNewDay()
    }

    fun loadDailyGoal() {
        val savedGoal = prefs.getInt("water_goal", 2500)
        _dailyGoal.value = savedGoal
        android.util.Log.d("WaterVM", "Daily goal loaded: $savedGoal")
    }

    fun updateDailyGoal(goalMl: Int) {
        _dailyGoal.value = goalMl
        prefs.edit().putInt("water_goal", goalMl).apply()
        android.util.Log.d("WaterVM", "Daily goal updated to: $goalMl")
    }

    fun addWater(amountMl: Int) {
        android.util.Log.d("WaterVM", "Adding water: $amountMl ml")
        _isLoading.value = true

        // Update today's total
        val currentTotal = _todayTotal.value
        val newTotal = currentTotal + amountMl
        _todayTotal.value = newTotal
        saveTodayTotal(newTotal)

        // NEW: Update lifetime total
        val currentLifetime = _lifetimeTotal.value
        val newLifetime = currentLifetime + amountMl
        _lifetimeTotal.value = newLifetime
        _lifetimeOz.value = newLifetime * 0.033814
        _lifetimeBottles.value = newLifetime / 500.0
        saveLifetimeTotal(newLifetime)

        _isLoading.value = false
    }

    fun resetDailyTotal() {
        _todayTotal.value = 0
        saveTodayTotal(0)
        android.util.Log.d("WaterVM", "Daily total reset to 0")
    }

    // NEW: Reset lifetime total (for debugging/admin use)
    fun resetLifetimeTotal() {
        _lifetimeTotal.value = 0
        _lifetimeOz.value = 0.0
        _lifetimeBottles.value = 0.0
        saveLifetimeTotal(0)
        android.util.Log.d("WaterVM", "Lifetime total reset to 0")
    }

    fun syncWithSettings() {
        loadDailyGoal()
        android.util.Log.d("WaterVM", "Synced with settings, goal: ${_dailyGoal.value}")
    }

    private fun loadTodayTotal() {
        val lastDate = prefs.getString("water_last_date", "")
        val todayDate = getTodayDateString()

        if (lastDate != todayDate) {
            // New day - reset water total
            _todayTotal.value = 0
            saveTodayTotal(0)
            prefs.edit().putString("water_last_date", todayDate).apply()
            android.util.Log.d("WaterVM", "New day detected, water reset to 0")
        } else {
            // Same day - load saved total
            val savedTotal = prefs.getInt("water_today_total", 0)
            _todayTotal.value = savedTotal
            android.util.Log.d("WaterVM", "Loaded today's water: $savedTotal ml")
        }
    }

    // NEW: Load lifetime water total
    private fun loadLifetimeTotal() {
        val savedLifetime = prefs.getInt("water_lifetime_total", 0)
        _lifetimeTotal.value = savedLifetime
        _lifetimeOz.value = savedLifetime * 0.033814
        _lifetimeBottles.value = savedLifetime / 500.0
        android.util.Log.d("WaterVM", "Loaded lifetime water: $savedLifetime ml")
    }

    // NEW: Save lifetime water total
    private fun saveLifetimeTotal(total: Int) {
        prefs.edit().putInt("water_lifetime_total", total).apply()
    }

    private fun checkAndResetIfNewDay() {
        val lastDate = prefs.getString("water_last_date", "")
        val todayDate = getTodayDateString()

        if (lastDate != todayDate) {
            _todayTotal.value = 0
            saveTodayTotal(0)
            prefs.edit().putString("water_last_date", todayDate).apply()
        }
    }

    private fun saveTodayTotal(total: Int) {
        prefs.edit().putInt("water_today_total", total).apply()
    }

    private fun getTodayDateString(): String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.DAY_OF_YEAR)}"
    }
}
package com.mealnote.app.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Meal(
    val id: Int = 0,
    val name: String,
    val mealTime: String,
    val calories: Int? = null,
    val photoPath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

class MealViewModel : ViewModel() {

    // Make context nullable and initialize later
    private var _context: Context? = null

    fun init(context: Context) {
        _context = context.applicationContext
        loadSavedMeals()
    }

    private val prefs get() = _context?.getSharedPreferences("mealnote", android.content.Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals.asStateFlow()

    private val _todaysMeals = MutableStateFlow<List<Meal>>(emptyList())
    val todaysMeals: StateFlow<List<Meal>> = _todaysMeals.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun addMeal(meal: Meal) {
        viewModelScope.launch {
            _isLoading.value = true
            val newId = (_meals.value.maxOfOrNull { it.id } ?: 0) + 1
            val newMeal = meal.copy(id = newId)
            val newMeals = _meals.value + newMeal
            _meals.value = newMeals
            updateTodaysMeals()
            saveMeals(newMeals)
            _isLoading.value = false
        }
    }

    fun deleteMeal(mealId: Int) {
        val newMeals = _meals.value.filter { it.id != mealId }
        _meals.value = newMeals
        updateTodaysMeals()
        saveMeals(newMeals)
    }

    private fun updateTodaysMeals() {
        val today = getTodayTimestamp()
        _todaysMeals.value = _meals.value.filter {
            isSameDay(it.timestamp, today)
        }
    }

    private fun saveMeals(meals: List<Meal>) {
        val json = gson.toJson(meals)
        prefs?.edit()?.putString("saved_meals", json)?.apply()
        android.util.Log.d("MealVM", "Saved ${meals.size} meals")
    }

    private fun loadSavedMeals() {
        val json = prefs?.getString("saved_meals", null)
        if (json != null) {
            val type = object : TypeToken<List<Meal>>() {}.type
            val loadedMeals: List<Meal> = gson.fromJson(json, type)
            _meals.value = loadedMeals
            updateTodaysMeals()
            android.util.Log.d("MealVM", "Loaded ${loadedMeals.size} meals")
        }
    }

    private fun loadSampleMeals() {
        _meals.value = listOf(
            Meal(id = 1, name = "Avocado Toast", mealTime = "Breakfast", calories = 350),
            Meal(id = 2, name = "Chicken Salad", mealTime = "Lunch", calories = 450),
            Meal(id = 3, name = "Pasta", mealTime = "Dinner", calories = 600)
        )
        updateTodaysMeals()
        saveMeals(_meals.value)
    }

    private fun getTodayTimestamp(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp2 }
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
    }
}
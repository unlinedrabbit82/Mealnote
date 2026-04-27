package com.mealnote.app.ui.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class Meal(
    val id: Int = 0,
    val name: String,
    val mealTime: String,
    val calories: Int? = null,
    val protein: Int? = null,
    val carbs: Int? = null,
    val fat: Int? = null,
    val sodium: Int? = null,    // ← NEW: Sodium in mg
    val fiber: Int? = null,     // ← NEW: Fiber in grams
    val photoPath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

class MealViewModel : ViewModel() {

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

    private val _recentMeals = MutableStateFlow<List<Meal>>(emptyList())
    val recentMeals: StateFlow<List<Meal>> = _recentMeals.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun addMeal(meal: Meal) {
        viewModelScope.launch {
            _isLoading.value = true
            val newId = (_meals.value.maxOfOrNull { it.id } ?: 0) + 1
            val newMeal = meal.copy(id = newId)
            val newMeals = listOf(newMeal) + _meals.value
            _meals.value = newMeals
            updateTodaysMeals()
            updateRecentMeals()
            saveMeals(newMeals)
            _isLoading.value = false
        }
    }

    fun deleteMeal(mealId: Int) {
        val newMeals = _meals.value.filter { it.id != mealId }
        _meals.value = newMeals
        updateTodaysMeals()
        updateRecentMeals()
        saveMeals(newMeals)
    }

    private fun updateTodaysMeals() {
        val today = getTodayTimestamp()
        _todaysMeals.value = _meals.value.filter {
            isSameDay(it.timestamp, today)
        }
    }

    private fun updateRecentMeals() {
        _recentMeals.value = _meals.value.take(10)
    }

    private fun saveMeals(meals: List<Meal>) {
        val json = gson.toJson(meals)
        prefs?.edit()?.putString("saved_meals", json)?.apply()
    }

    private fun loadSavedMeals() {
        val json = prefs?.getString("saved_meals", null)
        if (json != null) {
            val type = object : TypeToken<List<Meal>>() {}.type
            val loadedMeals: List<Meal> = gson.fromJson(json, type)
            _meals.value = loadedMeals.sortedByDescending { it.timestamp }
            updateTodaysMeals()
            updateRecentMeals()
        } else {
            loadSampleMeals()
        }
    }

    private fun loadSampleMeals() {
        _meals.value = listOf(
            Meal(id = 1, name = "Avocado Toast", mealTime = "Breakfast", calories = 350, protein = 12, carbs = 35, fat = 18, sodium = 300, fiber = 8),
            Meal(id = 2, name = "Chicken Salad", mealTime = "Lunch", calories = 450, protein = 35, carbs = 20, fat = 22, sodium = 450, fiber = 5),
            Meal(id = 3, name = "Pasta", mealTime = "Dinner", calories = 600, protein = 20, carbs = 80, fat = 15, sodium = 600, fiber = 6)
        )
        updateTodaysMeals()
        updateRecentMeals()
        saveMeals(_meals.value)
    }

    private fun getTodayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
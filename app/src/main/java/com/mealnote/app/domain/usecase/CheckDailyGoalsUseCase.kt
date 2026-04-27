package com.mealnote.app.domain.usecase

import com.mealnote.app.data.repository.MealRepository
import com.mealnote.app.data.repository.SettingsRepository
import com.mealnote.app.data.repository.WaterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar
import java.util.Date

class CheckDailyGoalsUseCase(
    private val waterRepository: WaterRepository,
    private val mealRepository: MealRepository,
    private val settingsRepository: SettingsRepository
) {

    /**
     * Check if daily water goal has been met
     */
    suspend fun isWaterGoalMet(): Boolean {
        val todayTotal = waterRepository.getTodayTotal()
        val settings = settingsRepository.getSettings()
        return todayTotal >= settings.dailyWaterGoalMl
    }

    /**
     * Get current water progress percentage
     */
    suspend fun getWaterProgressPercentage(): Int {
        val todayTotal = waterRepository.getTodayTotal()
        val settings = settingsRepository.getSettings()
        return if (settings.dailyWaterGoalMl > 0) {
            ((todayTotal.toFloat() / settings.dailyWaterGoalMl) * 100).toInt()
        } else {
            0
        }
    }

    /**
     * Get remaining water needed to reach goal
     */
    suspend fun getRemainingWaterNeeded(): Int {
        val todayTotal = waterRepository.getTodayTotal()
        val settings = settingsRepository.getSettings()
        val remaining = settings.dailyWaterGoalMl - todayTotal
        return if (remaining > 0) remaining else 0
    }

    /**
     * Get meal count for today
     */
    suspend fun getTodayMealCount(): Int {
        return mealRepository.getTodayMeals().size
    }

    /**
     * Check if all goals are met
     */
    suspend fun areAllGoalsMet(): Boolean {
        return isWaterGoalMet() && getTodayMealCount() >= 3
    }

    /**
     * Get motivational message
     */
    suspend fun getMotivationalMessage(): String {
        val waterProgress = getWaterProgressPercentage()
        val mealCount = getTodayMealCount()

        return when {
            waterProgress >= 100 && mealCount >= 3 -> "🎉 Perfect day! All goals achieved!"
            waterProgress >= 100 -> "💧 Water goal complete! Time to eat!"
            mealCount >= 3 -> "🍽️ Meals tracked! Don't forget water!"
            waterProgress >= 75 -> "💧 Almost there with water!"
            waterProgress >= 50 -> "👍 Halfway to your water goal!"
            else -> "🌟 Log your water and meals to stay on track!"
        }
    }
}
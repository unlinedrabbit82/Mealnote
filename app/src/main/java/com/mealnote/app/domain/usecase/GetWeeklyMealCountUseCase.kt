package com.mealnote.app.domain.usecase

import com.mealnote.app.data.repository.MealRepository

class GetWeeklyMealCountUseCase(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(): Int {
        val meals = mealRepository.getMealsForWeek()
        return meals.size
    }
}
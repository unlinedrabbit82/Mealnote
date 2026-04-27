package com.mealnote.app.data.repository

import android.graphics.Bitmap
import com.mealnote.app.data.database.dao.MealDao
import com.mealnote.app.data.database.entities.MealEntry
import com.mealnote.app.data.model.MealTime
import com.mealnote.app.domain.utils.ImageUtils
import java.util.*

class MealRepository(
    private val mealDao: MealDao,
    private val imageUtils: ImageUtils
) {
    suspend fun addMealEntry(
        name: String,
        mealTime: MealTime,
        timestamp: Date = Date(),
        photoBitmap: Bitmap? = null,
        aiSuggestionUsed: Boolean = false
    ): Long {
        val photoPath = photoBitmap?.let { imageUtils.saveImageToStorage(it) }

        val entry = MealEntry(
            name = name,
            mealTime = mealTime,
            timestamp = timestamp,
            photoPath = photoPath,
            aiSuggestionUsed = aiSuggestionUsed
        )
        return mealDao.insertMealEntry(entry)
    }

    suspend fun getTodayMeals(): List<MealEntry> {
        return mealDao.getMealsByDate(Date())
    }

    suspend fun getMealsForWeek(): List<MealEntry> {
        val endDate = Date()
        val calendar = Calendar.getInstance().apply {
            time = endDate
            add(Calendar.DAY_OF_YEAR, -6)
        }
        val startDate = calendar.time
        return mealDao.getMealsBetweenDates(startDate, endDate)
    }

    suspend fun deleteMeal(mealEntry: MealEntry) {
        mealEntry.photoPath?.let { imageUtils.deleteImage(it) }
        mealDao.deleteMealEntry(mealEntry)
    }

    suspend fun updateMeal(mealEntry: MealEntry) {
        mealDao.updateMealEntry(mealEntry)
    }

    suspend fun getMealById(id: Long): MealEntry? {
        return mealDao.getMealById(id)
    }
}
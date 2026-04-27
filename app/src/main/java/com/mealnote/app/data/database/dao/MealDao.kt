package com.mealnote.app.data.database.dao

import androidx.room.*
import com.mealnote.app.data.database.entities.MealEntry
import com.mealnote.app.data.model.MealTime
import java.util.Date

@Dao
interface MealDao {

    // Insert
    @Insert
    suspend fun insertMealEntry(entry: MealEntry): Long

    // Update
    @Update
    suspend fun updateMealEntry(entry: MealEntry)

    // Delete
    @Delete
    suspend fun deleteMealEntry(entry: MealEntry)

    // Get single meal by ID (YOU WERE MISSING THIS)
    @Query("SELECT * FROM meal_entries WHERE id = :id")
    suspend fun getMealById(id: Long): MealEntry?

    // Get meals by date
    @Query("SELECT * FROM meal_entries WHERE date(timestamp) = date(:date) ORDER BY timestamp DESC")
    suspend fun getMealsByDate(date: Date): List<MealEntry>

    // Get meals between dates
    @Query("SELECT * FROM meal_entries WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    suspend fun getMealsBetweenDates(startDate: Date, endDate: Date): List<MealEntry>

    // Get meal count for a date
    @Query("SELECT COUNT(*) FROM meal_entries WHERE date(timestamp) = date(:date)")
    suspend fun getMealCountForDate(date: Date): Int

    // Get meals by time and date
    @Query("SELECT * FROM meal_entries WHERE mealTime = :mealTime AND date(timestamp) = date(:date) ORDER BY timestamp DESC")
    suspend fun getMealsByTimeAndDate(mealTime: MealTime, date: Date): List<MealEntry>

    // Get all meals (for statistics)
    @Query("SELECT * FROM meal_entries ORDER BY timestamp DESC")
    suspend fun getAllMeals(): List<MealEntry>

    // Get meals within a date range with limit
    @Query("SELECT * FROM meal_entries WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMeals(startDate: Date, endDate: Date, limit: Int): List<MealEntry>

    // Delete all meals (for testing)
    @Query("DELETE FROM meal_entries")
    suspend fun deleteAllMeals()
}
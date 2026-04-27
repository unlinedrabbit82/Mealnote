package com.mealnote.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mealnote.app.data.model.MealTime
import java.util.Date

@Entity(tableName = "meal_entries")
data class MealEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val mealTime: MealTime,          // Breakfast, Lunch, Dinner, Snack
    val timestamp: Date,
    val photoPath: String? = null,
    val aiSuggestionUsed: Boolean = false,
    val calories: Int? = null,
    val notes: String? = null
)
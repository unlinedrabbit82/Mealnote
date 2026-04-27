package com.mealnote.app.data.database.converters

import androidx.room.TypeConverter
import com.mealnote.app.data.model.MealTime
import java.util.Date

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromMealTime(mealTime: MealTime): String {
        return mealTime.name
    }

    @TypeConverter
    fun toMealTime(mealTimeString: String): MealTime {
        return MealTime.valueOf(mealTimeString)
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toStringList(data: String): List<String> {
        return data.split(",").filter { it.isNotEmpty() }
    }
}
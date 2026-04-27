package com.mealnote.app.data.database.dao

import androidx.room.*
import com.mealnote.app.data.database.entities.WaterEntry
import java.util.Date

@Dao
interface WaterDao {

    @Insert
    suspend fun insertWaterEntry(entry: WaterEntry): Long

    @Update
    suspend fun updateWaterEntry(entry: WaterEntry)

    @Delete
    suspend fun deleteWaterEntry(entry: WaterEntry)

    @Query("SELECT * FROM water_entries WHERE date(timestamp) = date(:date)")
    suspend fun getEntriesByDate(date: Date): List<WaterEntry>

    @Query("SELECT SUM(amountMl) FROM water_entries WHERE date(timestamp) = date(:date)")
    suspend fun getTotalWaterForDate(date: Date): Int?

    @Query("SELECT * FROM water_entries WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp ASC")
    suspend fun getEntriesBetweenDates(startDate: Date, endDate: Date): List<WaterEntry>

    @Query("SELECT SUM(amountMl) FROM water_entries WHERE timestamp BETWEEN :startDate AND :endDate GROUP BY date(timestamp)")
    suspend fun getDailyTotalsBetweenDates(startDate: Date, endDate: Date): List<Int>
}
package com.mealnote.app.data.repository

import com.mealnote.app.data.database.dao.WaterDao
import com.mealnote.app.data.database.entities.WaterEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class WaterRepository(
    private val waterDao: WaterDao
) {
    suspend fun addWaterEntry(amountMl: Int, timestamp: Date = Date()): Long {
        val entry = WaterEntry(
            amountMl = amountMl,
            timestamp = timestamp
        )
        return waterDao.insertWaterEntry(entry)
    }

    suspend fun getTodayTotal(): Int {
        return waterDao.getTotalWaterForDate(Date()) ?: 0
    }

    fun getDailyTotalsForWeek(): Flow<List<Int>> = flow {
        val endDate = Date()
        val calendar = Calendar.getInstance().apply {
            time = endDate
            add(Calendar.DAY_OF_YEAR, -6)
        }
        val startDate = calendar.time
        val totals = waterDao.getDailyTotalsBetweenDates(startDate, endDate) ?: emptyList()
        emit(totals)
    }

    suspend fun getEntriesForDate(date: Date): List<WaterEntry> {
        return waterDao.getEntriesByDate(date)
    }

    suspend fun deleteWaterEntry(entry: WaterEntry) {
        waterDao.deleteWaterEntry(entry)
    }

    suspend fun updateWaterEntry(entry: WaterEntry) {
        waterDao.updateWaterEntry(entry)
    }
}
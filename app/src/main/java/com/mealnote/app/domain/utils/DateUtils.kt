package com.mealnote.app.domain.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    @SuppressLint("ConstantLocale")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    @SuppressLint("ConstantLocale")
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    @SuppressLint("ConstantLocale")
    private val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    @SuppressLint("ConstantLocale")
    private val fullDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }

    fun formatTime(date: Date): String {
        return timeFormat.format(date)
    }

    fun formatDayOfWeek(date: Date): String {
        return dayFormat.format(date)
    }

    fun formatFullDate(date: Date): String {
        return fullDateFormat.format(date)
    }

    fun getStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.time
    }

    fun getEndOfDay(date: Date): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.time
    }

    fun getDaysBetween(startDate: Date, endDate: Date): List<Date> {
        val days = mutableListOf<Date>()
        val calendar = Calendar.getInstance().apply {
            time = startDate
        }

        while (calendar.time.before(endDate) || calendar.time == endDate) {
            days.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return days
    }

    fun isToday(date: Date): Boolean {
        return formatDate(date) == formatDate(Date())
    }

    fun getWeekDates(): List<Date> {
        val dates = mutableListOf<Date>()
        val calendar = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }

        for (i in 0..6) {
            dates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return dates
    }
}
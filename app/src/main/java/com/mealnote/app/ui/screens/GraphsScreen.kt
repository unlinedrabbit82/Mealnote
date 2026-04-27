package com.mealnote.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mealnote.app.ui.viewmodels.MealViewModel
import com.mealnote.app.ui.viewmodels.WaterViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphsScreen(
    onNavigateBack: () -> Unit,
    mealViewModel: MealViewModel,
    waterViewModel: WaterViewModel
) {
    val allMeals by mealViewModel.meals.collectAsState()
    val last7Days = remember { getLast7Days() }
    val dailyCalories = remember(allMeals) { getDailyCalories(allMeals, last7Days) }
    val maxCalories = dailyCalories.maxOrNull()?.let { if (it == 0) 1000 else it } ?: 1000

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Charts & Graphs") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("📈 Weekly Calories", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Last 7 days", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Simple bar chart using Column
            dailyCalories.forEachIndexed { index, calories ->
                val percentage = (calories.toFloat() / maxCalories).coerceIn(0f, 1f)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getShortDayName(last7Days[index]),
                        modifier = Modifier.width(45.dp),
                        fontSize = 12.sp
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(percentage)
                                .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.small)
                        )
                        Text(
                            text = "$calories cal",
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 12.sp,
                            color = if (percentage > 0.6f) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("🍽️ Weekly Meals", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Number of meals per day", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            val dailyMeals = getDailyMealCount(allMeals, last7Days)
            val maxMeals = dailyMeals.maxOrNull()?.let { if (it == 0) 1 else it } ?: 1

            dailyMeals.forEachIndexed { index, count ->
                val percentage = count.toFloat() / maxMeals
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getShortDayName(last7Days[index]),
                        modifier = Modifier.width(45.dp),
                        fontSize = 12.sp
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(percentage)
                                .background(MaterialTheme.colorScheme.secondary, shape = MaterialTheme.shapes.small)
                        )
                        Text(
                            text = "$count meals",
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 12.sp,
                            color = if (percentage > 0.6f) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("📊 Meal Distribution", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))

            val breakfastCount = allMeals.count { it.mealTime == "Breakfast" }
            val lunchCount = allMeals.count { it.mealTime == "Lunch" }
            val dinnerCount = allMeals.count { it.mealTime == "Dinner" }
            val snackCount = allMeals.count { it.mealTime == "Snack" }
            val total = breakfastCount + lunchCount + dinnerCount + snackCount

            if (total > 0) {
                DistributionBar("🌅 Breakfast", breakfastCount, total, Color(0xFF4CAF50))
                DistributionBar("☀️ Lunch", lunchCount, total, Color(0xFFFF9800))
                DistributionBar("🌙 Dinner", dinnerCount, total, Color(0xFF9C27B0))
                DistributionBar("🍎 Snack", snackCount, total, Color(0xFF2196F3))
            } else {
                Text("No meals logged yet", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("🥗 Macro Breakdown", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))

            val totalProtein = allMeals.mapNotNull { it.protein }.sum()
            val totalCarbs = allMeals.mapNotNull { it.carbs }.sum()
            val totalFat = allMeals.mapNotNull { it.fat }.sum()
            val totalMacros = totalProtein + totalCarbs + totalFat

            if (totalMacros > 0) {
                DistributionBar("💪 Protein", totalProtein, totalMacros, Color(0xFF4CAF50))
                DistributionBar("🌾 Carbs", totalCarbs, totalMacros, Color(0xFFFF9800))
                DistributionBar("🥑 Fat", totalFat, totalMacros, Color(0xFF9C27B0))
            } else {
                Text("No macro data available", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DistributionBar(
    label: String,
    value: Int,
    total: Int,
    color: Color
) {
    val percentage = if (total > 0) (value.toFloat() / total * 100).toInt() else 0

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 14.sp)
            Text("$value ($percentage%)", fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = (value.toFloat() / total).coerceIn(0f, 1f),
            modifier = Modifier.fillMaxWidth(),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// Data helper functions
private fun getLast7Days(): List<Calendar> {
    val calendar = Calendar.getInstance()
    val days = mutableListOf<Calendar>()
    for (i in 6 downTo 0) {
        val day = calendar.clone() as Calendar
        day.add(Calendar.DAY_OF_YEAR, -i)
        days.add(day)
    }
    return days
}

private fun getDailyCalories(meals: List<com.mealnote.app.ui.viewmodels.Meal>, days: List<Calendar>): List<Int> {
    return days.map { day ->
        meals.filter { meal ->
            val mealCal = Calendar.getInstance().apply { timeInMillis = meal.timestamp }
            mealCal.get(Calendar.YEAR) == day.get(Calendar.YEAR) &&
                    mealCal.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR)
        }.mapNotNull { it.calories }.sum()
    }
}

private fun getDailyMealCount(meals: List<com.mealnote.app.ui.viewmodels.Meal>, days: List<Calendar>): List<Int> {
    return days.map { day ->
        meals.count { meal ->
            val mealCal = Calendar.getInstance().apply { timeInMillis = meal.timestamp }
            mealCal.get(Calendar.YEAR) == day.get(Calendar.YEAR) &&
                    mealCal.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR)
        }
    }
}

private fun getShortDayName(calendar: Calendar): String {
    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> "Sun"
        Calendar.MONDAY -> "Mon"
        Calendar.TUESDAY -> "Tue"
        Calendar.WEDNESDAY -> "Wed"
        Calendar.THURSDAY -> "Thu"
        Calendar.FRIDAY -> "Fri"
        Calendar.SATURDAY -> "Sat"
        else -> "Day"
    }
}
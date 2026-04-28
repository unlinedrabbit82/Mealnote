package com.mealnote.app.ui.screens

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
import com.mealnote.app.ui.viewmodels.MealViewModel
import com.mealnote.app.ui.viewmodels.WaterViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToGraphs: () -> Unit,
    waterViewModel: WaterViewModel,
    mealViewModel: MealViewModel
) {
    val todayTotal by waterViewModel.todayTotal.collectAsState()
    val dailyGoal by waterViewModel.dailyGoal.collectAsState()
    val lifetimeTotal by waterViewModel.lifetimeTotal.collectAsState()
    val lifetimeOz by waterViewModel.lifetimeOz.collectAsState()
    val lifetimeBottles by waterViewModel.lifetimeBottles.collectAsState()
    val allMeals by mealViewModel.meals.collectAsState()

    // Get today's timestamp (start of day)
    val todayStart = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    // Filter meals for today
    val todaysMeals = allMeals.filter { it.timestamp >= todayStart }

    // Today's macros
    val todaysCalories = todaysMeals.mapNotNull { it.calories }.sum()
    val todaysProtein = todaysMeals.mapNotNull { it.protein }.sum()
    val todaysCarbs = todaysMeals.mapNotNull { it.carbs }.sum()
    val todaysFat = todaysMeals.mapNotNull { it.fat }.sum()
    val todaysSodium = todaysMeals.mapNotNull { it.sodium }.sum()
    val todaysFiber = todaysMeals.mapNotNull { it.fiber }.sum()

    // All-time macros
    val totalMeals = allMeals.size
    val totalCalories = allMeals.mapNotNull { it.calories }.sum()
    val totalProtein = allMeals.mapNotNull { it.protein }.sum()
    val totalCarbs = allMeals.mapNotNull { it.carbs }.sum()
    val totalFat = allMeals.mapNotNull { it.fat }.sum()
    val totalSodium = allMeals.mapNotNull { it.sodium }.sum()
    val totalFiber = allMeals.mapNotNull { it.fiber }.sum()

    // Bottle calculations
    val todayBottles = todayTotal / 500.0
    val goalBottles = dailyGoal / 500.0

    val avgCaloriesPerMeal = if (totalMeals > 0) {
        allMeals.mapNotNull { it.calories }.average().toInt()
    } else 0

    // Meal distribution
    val breakfastCount = allMeals.count { it.mealTime == "Breakfast" }
    val lunchCount = allMeals.count { it.mealTime == "Lunch" }
    val dinnerCount = allMeals.count { it.mealTime == "Dinner" }
    val snackCount = allMeals.count { it.mealTime == "Snack" }

    val todaysBreakfast = todaysMeals.count { it.mealTime == "Breakfast" }
    val todaysLunch = todaysMeals.count { it.mealTime == "Lunch" }
    val todaysDinner = todaysMeals.count { it.mealTime == "Dinner" }
    val todaysSnack = todaysMeals.count { it.mealTime == "Snack" }
    val todaysTotalMeals = todaysMeals.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📊 Your Statistics",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ===== WATER STATISTICS CARD =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("💧 Water Intake", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Today's Water
                    Text("Today's Intake:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("📏 Metric:")
                        Text("$todayTotal / $dailyGoal ml (${String.format("%.1f", todayTotal * 0.033814)} / ${String.format("%.1f", dailyGoal * 0.033814)} oz)",
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("🍾 Bottles (500ml):")
                        Text("${String.format("%.1f", todayBottles)} / ${String.format("%.1f", goalBottles)} bottles",
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = Color(0xFF2196F3))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val percentage = (todayTotal.toFloat() / dailyGoal * 100).toInt()
                    LinearProgressIndicator(
                        progress = (todayTotal.toFloat() / dailyGoal).coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "Achieved: $percentage% of daily goal",
                        color = if (percentage >= 100) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    // ===== LIFETIME WATER - NOW FULLY IMPLEMENTED =====
                    Text("🌟 Lifetime Totals", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("💧 Total Water Consumed:")
                        Text("${lifetimeTotal} ml",
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = Color(0xFF2196F3))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("📏 In Ounces:")
                        Text("${String.format("%.1f", lifetimeOz)} oz",
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("🍾 In Bottles (500ml):")
                        Text("${String.format("%.1f", lifetimeBottles)} bottles",
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = Color(0xFF4CAF50))
                    }

                    // Show number of days tracked
                    val daysTracked = (lifetimeTotal / (todayTotal.coerceAtLeast(1))).coerceAtLeast(1)
                    if (todayTotal > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("📅 Average Daily Intake:")
                            Text("${lifetimeTotal / daysTracked} ml/day",
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== TODAY'S MACROS CARD =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("📅 Today's Macros", style = MaterialTheme.typography.titleLarge)
                    Text("(Resets daily)", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("🍽️ Meals Today:")
                        Text("$todaysTotalMeals", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("🔥 Calories:")
                        Text("$todaysCalories cal", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("💪 Protein:")
                        Text("${todaysProtein}g", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFF4CAF50))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("🌾 Carbs:")
                        Text("${todaysCarbs}g", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFFFF9800))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("🥑 Fat:")
                        Text("${todaysFat}g", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFF9C27B0))
                    }

                    if (todaysSodium > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🧂 Sodium:")
                            Text("${todaysSodium}mg", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFF2196F3))
                        }
                    }

                    if (todaysFiber > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🌿 Fiber:")
                            Text("${todaysFiber}g", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFF8BC34A))
                        }
                    }

                    if (todaysTotalMeals > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                        Text("Today's Meal Distribution:", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))

                        if (todaysBreakfast > 0) Text("🌅 Breakfast: $todaysBreakfast")
                        if (todaysLunch > 0) Text("☀️ Lunch: $todaysLunch")
                        if (todaysDinner > 0) Text("🌙 Dinner: $todaysDinner")
                        if (todaysSnack > 0) Text("🍎 Snack: $todaysSnack")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== ALL-TIME MEAL STATISTICS CARD =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("🍽️ All-Time Meal Summary", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Meals Logged:")
                        Text("$totalMeals", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }

                    if (avgCaloriesPerMeal > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Average Calories:")
                            Text("$avgCaloriesPerMeal cal", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text("Meal Distribution:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))

                    MealDistributionRow("🌅 Breakfast", breakfastCount, totalMeals)
                    MealDistributionRow("☀️ Lunch", lunchCount, totalMeals)
                    MealDistributionRow("🌙 Dinner", dinnerCount, totalMeals)
                    MealDistributionRow("🍎 Snack", snackCount, totalMeals)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== ALL-TIME MACRO SUMMARY CARD =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("🥗 All-Time Macro Summary", style = MaterialTheme.typography.titleLarge)
                    Text("(Lifetime totals)", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("🔥 Total Calories:")
                        Text("$totalCalories cal", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("💪 Protein:")
                        Text("${totalProtein}g", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFF4CAF50))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("🌾 Carbs:")
                        Text("${totalCarbs}g", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFFFF9800))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("🥑 Fat:")
                        Text("${totalFat}g", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFF9C27B0))
                    }

                    if (totalSodium > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🧂 Sodium:")
                            Text("${totalSodium}mg", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFF2196F3))
                        }
                    }

                    if (totalFiber > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🌿 Fiber:")
                            Text("${totalFiber}g", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFF8BC34A))
                        }
                    }

                    val totalMacros = totalProtein + totalCarbs + totalFat
                    if (totalMacros > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Lifetime Macro Ratio:", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))

                        val proteinPercent = (totalProtein.toFloat() / totalMacros * 100).toInt()
                        val carbsPercent = (totalCarbs.toFloat() / totalMacros * 100).toInt()
                        val fatPercent = (totalFat.toFloat() / totalMacros * 100).toInt()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text("💪 $proteinPercent%", color = Color(0xFF4CAF50))
                            Text("🌾 $carbsPercent%", color = Color(0xFFFF9800))
                            Text("🥑 $fatPercent%", color = Color(0xFF9C27B0))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== ACHIEVEMENT BADGE =====
            if (todayTotal >= dailyGoal && todaysTotalMeals >= 3) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🏆 Great job! You've met your daily goals! 🎉")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateToGraphs,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("📈 View Charts & Graphs")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateToHome,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🏠 Home")
                }
                Button(
                    onClick = onNavigateToSettings,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("⚙️ Settings")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun MealDistributionRow(
    mealType: String,
    count: Int,
    total: Int
) {
    val percentage = if (total > 0) (count.toFloat() / total * 100).toInt() else 0

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(mealType)
            Text("$count meals ($percentage%)", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = (count.toFloat() / total).coerceIn(0f, 1f),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
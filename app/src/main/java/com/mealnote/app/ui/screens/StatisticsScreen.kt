package com.mealnote.app.ui.screens

import androidx.compose.foundation.layout.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit,
    waterViewModel: WaterViewModel,
    mealViewModel: MealViewModel
) {
    val todayTotal by waterViewModel.todayTotal.collectAsState()
    val dailyGoal by waterViewModel.dailyGoal.collectAsState()
    val allMeals by mealViewModel.meals.collectAsState()

    // Calculate statistics
    val totalMeals = allMeals.size
    val totalCalories = allMeals.mapNotNull { it.calories }.sum()
    val totalProtein = allMeals.mapNotNull { it.protein }.sum()
    val totalCarbs = allMeals.mapNotNull { it.carbs }.sum()
    val totalFat = allMeals.mapNotNull { it.fat }.sum()

    val avgCaloriesPerMeal = if (totalMeals > 0) {
        allMeals.mapNotNull { it.calories }.average().toInt()
    } else 0

    // Group meals by type
    val breakfastCount = allMeals.count { it.mealTime == "Breakfast" }
    val lunchCount = allMeals.count { it.mealTime == "Lunch" }
    val dinnerCount = allMeals.count { it.mealTime == "Dinner" }
    val snackCount = allMeals.count { it.mealTime == "Snack" }

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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Today's Water:")
                        Text("$todayTotal / $dailyGoal ml", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val percentage = (todayTotal.toFloat() / dailyGoal * 100).toInt()
                    Text(
                        "Achieved: $percentage% of daily goal",
                        color = if (percentage >= 100) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== MEAL STATISTICS CARD =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("🍽️ Meal Summary", style = MaterialTheme.typography.titleLarge)
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

            // ===== MACRO SUMMARY CARD (NEW) =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("🥗 Macro Summary", style = MaterialTheme.typography.titleLarge)
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
                        Text(
                            "${totalProtein}g",
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("🌾 Carbs:")
                        Text(
                            "${totalCarbs}g",
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = Color(0xFFFF9800)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("🥑 Fat:")
                        Text(
                            "${totalFat}g",
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = Color(0xFF9C27B0)
                        )
                    }

                    // Optional: Show macro breakdown if any macros exist
                    if (totalProtein > 0 || totalCarbs > 0 || totalFat > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))

                        val totalMacros = totalProtein + totalCarbs + totalFat
                        if (totalMacros > 0) {
                            Text("Macro Ratio:", style = MaterialTheme.typography.bodyMedium)
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== ACHIEVEMENT BADGE =====
            if (todayTotal >= dailyGoal && totalMeals >= 3) {
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

            // ===== NAVIGATION BUTTONS =====
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
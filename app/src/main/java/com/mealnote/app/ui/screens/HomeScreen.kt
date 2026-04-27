package com.mealnote.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mealnote.app.ui.viewmodels.MealViewModel
import com.mealnote.app.ui.viewmodels.WaterViewModel
import java.io.File

// Conversion constant: 1 ml = 0.033814 oz
const val ML_TO_OZ = 0.033814

// Helper function to format oz display
fun formatOz(ml: Int): String {
    val oz = ml * ML_TO_OZ
    return String.format("%.1f", oz)
}

@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToAddMeal: () -> Unit,
    onNavigateToAllMeals: () -> Unit,
    waterViewModel: WaterViewModel = viewModel(),
    mealViewModel: MealViewModel = viewModel()
) {
    val todayTotal by waterViewModel.todayTotal.collectAsState()
    val dailyGoal by waterViewModel.dailyGoal.collectAsState()
    val todaysMeals by mealViewModel.todaysMeals.collectAsState()
    val allMeals by mealViewModel.meals.collectAsState()

    // Debug logging
    LaunchedEffect(todayTotal, dailyGoal, todaysMeals) {
        android.util.Log.d("HomeScreen", "Water: $todayTotal / $dailyGoal ml")
        android.util.Log.d("HomeScreen", "Today's meals: ${todaysMeals.size}")
        android.util.Log.d("HomeScreen", "Total meals: ${allMeals.size}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 60.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ===== WATER CARD =====
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("💧 Today's Water", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))

                // Progress bar
                LinearProgressIndicator(
                    progress = (todayTotal.toFloat() / dailyGoal).coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Display both ml and oz
                Text(
                    text = "$todayTotal ml (${formatOz(todayTotal)} oz) / $dailyGoal ml (${formatOz(dailyGoal)} oz)",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Water buttons - NOW WITH FLUID OUNCES
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 250ml / 8.5 oz button
                    Button(
                        onClick = { waterViewModel.addWater(250) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("+250 ml")
                            Text("(${formatOz(250)} oz)", fontSize = 10.sp)
                        }
                    }

                    // 500ml / 16.9 oz button
                    Button(
                        onClick = { waterViewModel.addWater(500) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("+500 ml")
                            Text("(${formatOz(500)} oz)", fontSize = 10.sp)
                        }
                    }

                    // 750ml / 25.4 oz button
                    Button(
                        onClick = { waterViewModel.addWater(750) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("+750 ml")
                            Text("(${formatOz(750)} oz)", fontSize = 10.sp)
                        }
                    }
                }

                // Reset button (for testing)
                TextButton(
                    onClick = { waterViewModel.resetDailyTotal() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset Today")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== RECENT MEALS CARD =====
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("🍽️ Recent Meals", style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = onNavigateToAddMeal) {
                        Text("+ Add Meal")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (todaysMeals.isEmpty()) {
                    Text(
                        "No meals logged today. Tap + Add Meal",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    todaysMeals.take(3).forEach { meal ->
                        MealListItem(
                            meal = meal,
                            onDelete = {
                                mealViewModel.deleteMeal(meal.id)
                                android.util.Log.d("HomeScreen", "Deleted meal: ${meal.name}")
                            }
                        )
                    }
                }

                // Always show "View All" button
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onNavigateToAllMeals,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text("📋 View All Meals (${allMeals.size} total)")
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
                onClick = onNavigateToStats,
                modifier = Modifier.weight(1f)
            ) {
                Text("📊 Statistics")
            }
            Button(
                onClick = onNavigateToAddMeal,
                modifier = Modifier.weight(1f)
            ) {
                Text("➕ Add Meal")
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

// MealListItem remains the same
@Composable
fun MealListItem(
    meal: com.mealnote.app.ui.viewmodels.Meal,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image display using AsyncImage
            if (meal.photoPath != null) {
                AsyncImage(
                    model = File(meal.photoPath),
                    contentDescription = meal.name,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder when no image
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🍽️", fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))

            // Meal details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = "${meal.mealTime} • ${meal.calories?.toString() ?: "No"} cal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // Show macros if available
                if (meal.protein != null || meal.carbs != null || meal.fat != null) {
                    Text(
                        text = buildMacrosString(meal),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Time
            Text(
                text = java.text.SimpleDateFormat("h:mm a", java.util.Locale.US)
                    .format(java.util.Date(meal.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 8.dp)
            )

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(40.dp)
            ) {
                Text("🗑️", fontSize = 16.sp)
            }
        }
    }
}

// Helper function to format macros string
fun buildMacrosString(meal: com.mealnote.app.ui.viewmodels.Meal): String {
    val parts = mutableListOf<String>()
    meal.protein?.let { parts.add("P:${it}g") }
    meal.carbs?.let { parts.add("C:${it}g") }
    meal.fat?.let { parts.add("F:${it}g") }
    meal.sodium?.let { parts.add("Na:${it}mg") }
    meal.fiber?.let { parts.add("Fib:${it}g") }
    return parts.joinToString(" • ")
}
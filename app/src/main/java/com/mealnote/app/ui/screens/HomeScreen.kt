package com.mealnote.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mealnote.app.ui.viewmodels.MealViewModel
import com.mealnote.app.ui.viewmodels.WaterViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import android.graphics.BitmapFactory
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale

import java.io.File

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

    // Debug logging
    LaunchedEffect(todayTotal, dailyGoal, todaysMeals) {
        android.util.Log.d("HomeScreen", "Water: $todayTotal / $dailyGoal ml")
        android.util.Log.d("HomeScreen", "Today's meals: ${todaysMeals.size}")
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

                Text(
                    text = "$todayTotal / $dailyGoal ml",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Water buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { waterViewModel.addWater(250) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+250ml")
                    }
                    Button(
                        onClick = { waterViewModel.addWater(500) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+500ml")
                    }
                    Button(
                        onClick = { waterViewModel.addWater(750) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+750ml")
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

                // In HomeScreen, update the meals display section:
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

                    if (todaysMeals.size > 3) {
                        TextButton(onClick = onNavigateToAllMeals) {
                            Text("View ${todaysMeals.size - 3} more...")
                        }
                    }
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
            // ✅ Using AsyncImage for efficient loading
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
// Helper function to load image from path
fun loadImageFromPath(path: String): android.graphics.Bitmap? {
    return try {
        val file = java.io.File(path)
        if (file.exists()) {
            android.graphics.BitmapFactory.decodeFile(path)
        } else {
            null
        }
    } catch (e: Exception) {
        android.util.Log.e("MealListItem", "Error loading image: ${e.message}")
        null
    }
}
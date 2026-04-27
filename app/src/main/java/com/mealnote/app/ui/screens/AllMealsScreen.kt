package com.mealnote.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mealnote.app.ui.viewmodels.MealViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllMealsScreen(
    onNavigateBack: () -> Unit,
    mealViewModel: MealViewModel
) {
    val allMeals by mealViewModel.meals.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var mealToDelete by remember { mutableStateOf<com.mealnote.app.ui.viewmodels.Meal?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Meals") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            android.util.Log.d("AllMeals", "Total meals: ${allMeals.size}")
                        }
                    ) {
                        Text("${allMeals.size} total")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Meal History",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (allMeals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No meals logged yet")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allMeals.reversed()) { mealItem ->
                        MealListItem(
                            meal = mealItem,
                            onDelete = {  // ← Added onDelete parameter
                                mealToDelete = mealItem
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && mealToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Meal") },
            text = { Text("Are you sure you want to delete '${mealToDelete?.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mealToDelete?.let { meal ->
                            mealViewModel.deleteMeal(meal.id)
                            android.util.Log.d("AllMeals", "Deleted meal: ${meal.name}")
                        }
                        showDeleteDialog = false
                        mealToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
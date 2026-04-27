// ui/screens/SettingsScreen.kt
package com.mealnote.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mealnote.app.ui.viewmodels.SettingsViewModel
import com.mealnote.app.ui.viewmodels.WaterViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    waterViewModel: WaterViewModel,
    onNavigateBack: () -> Unit = {}  // Add this parameter
) {
    val settings by viewModel.settings.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 60.dp,      // ← Add this to push content down
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Add a back button row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onNavigateBack) {
                Text("← Back")
            }
            Text(
                text = "⚙️ Settings",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.width(64.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Water Goal Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("💧 Daily Water Goal")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${settings.dailyWaterGoalMl} ml",
                    style = MaterialTheme.typography.headlineSmall
                )
                Slider(
                    value = settings.dailyWaterGoalMl.toFloat(),
                    onValueChange = { newValue ->
                        val newGoal = newValue.toInt()
                        viewModel.updateWaterGoal(newGoal)
                        waterViewModel.updateDailyGoal(newGoal)  // ← Sync with WaterViewModel
                    },
                    valueRange = 500f..4000f,
                    steps = 7
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reminders Card
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
                    Text("💧 Water Reminders")
                    Switch(
                        checked = settings.enableWaterReminders,
                        onCheckedChange = { viewModel.updateWaterReminders(it) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("🍽️ Meal Reminders")
                    Switch(
                        checked = settings.enableMealReminders,
                        onCheckedChange = { viewModel.updateMealReminders(it) }
                    )
                }
            }
        }

        if (isSaving) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
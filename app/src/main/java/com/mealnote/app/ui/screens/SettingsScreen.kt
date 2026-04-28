package com.mealnote.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.*
import com.mealnote.app.notifications.ReminderWorker
import com.mealnote.app.ui.viewmodels.SettingsViewModel
import com.mealnote.app.ui.viewmodels.WaterViewModel
import java.util.concurrent.TimeUnit

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    waterViewModel: WaterViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val settings by viewModel.settings.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button row
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
                Text("💧 Daily Water Goal", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${settings?.dailyWaterGoalMl ?: 2500} ml",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = (settings?.dailyWaterGoalMl ?: 2500).toFloat(),
                    onValueChange = { newValue ->
                        val newGoal = newValue.toInt()
                        viewModel.updateWaterGoal(newGoal)
                        waterViewModel.updateDailyGoal(newGoal)
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
                        checked = settings?.enableWaterReminders ?: true,
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
                        checked = settings?.enableMealReminders ?: true,
                        onCheckedChange = { viewModel.updateMealReminders(it) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== NEW: TEST NOTIFICATIONS CARD =====
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("🔔 Test Notifications", style = MaterialTheme.typography.titleLarge)
                Text("(Temporary - for testing only)", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            // Test water notification
                            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                                .setInitialDelay(3, TimeUnit.SECONDS)
                                .setInputData(
                                    workDataOf(
                                        "type" to "water",
                                        "message" to "💧 Test: Time to hydrate!"
                                    )
                                )
                                .build()
                            WorkManager.getInstance(context).enqueue(workRequest)
                            android.util.Log.d("Settings", "Water test notification scheduled")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Test Water")
                    }

                    Button(
                        onClick = {
                            // Test meal notification
                            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                                .setInitialDelay(3, TimeUnit.SECONDS)
                                .setInputData(
                                    workDataOf(
                                        "type" to "meal",
                                        "message" to "🍽️ Test: Don't forget to log your meal!"
                                    )
                                )
                                .build()
                            WorkManager.getInstance(context).enqueue(workRequest)
                            android.util.Log.d("Settings", "Meal test notification scheduled")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Test Meal")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        // Immediate notification test
                        val notificationManager = context.getSystemService(android.app.NotificationManager::class.java)
                        val channelId = "meal_reminder_channel"

                        val notification = androidx.core.app.NotificationCompat.Builder(context, channelId)
                            .setContentTitle("🔔 Test Notification")
                            .setContentText("Your MealNote app is working correctly!")
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true)
                            .build()

                        notificationManager.notify(999, notification)
                        android.util.Log.d("Settings", "Immediate test notification sent")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Send Immediate Test Notification")
                }
            }
        }

        if (isSaving) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
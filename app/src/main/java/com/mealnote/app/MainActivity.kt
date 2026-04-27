package com.mealnote.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mealnote.app.ui.screens.AddMealScreen
import com.mealnote.app.ui.screens.HomeScreen
import com.mealnote.app.ui.screens.SettingsScreen
import com.mealnote.app.ui.screens.StatisticsScreen
import com.mealnote.app.ui.theme.MealNoteTheme
import com.mealnote.app.ui.viewmodels.MealViewModel
import com.mealnote.app.ui.viewmodels.SettingsViewModel
import com.mealnote.app.ui.viewmodels.WaterViewModel
import com.mealnote.app.ui.screens.AllMealsScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsViewModel = SettingsViewModel()
        val waterViewModel = WaterViewModel(applicationContext)
        // Initialize MealViewModel with init() method
        val mealViewModel = MealViewModel().apply { init(applicationContext) }

        setContent {
            MealNoteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MealNoteApp(
                        settingsViewModel = settingsViewModel,
                        waterViewModel = waterViewModel,
                        mealViewModel = mealViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun MealNoteApp(
    settingsViewModel: SettingsViewModel,
    waterViewModel: WaterViewModel,
    mealViewModel: MealViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToAddMeal = { navController.navigate("add_meal") },
                onNavigateToAllMeals = { navController.navigate("all_meals") },  // ← Add this
                waterViewModel = waterViewModel,
                mealViewModel = mealViewModel
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = settingsViewModel,
                waterViewModel = waterViewModel,  // ← Pass this
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("all_meals") {
            AllMealsScreen(
                onNavigateBack = { navController.popBackStack() },
                mealViewModel = mealViewModel  // ← Pass the existing ViewModel
            )
        }
        composable("stats") {
            StatisticsScreen(
                onNavigateToHome = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate("settings") },
                waterViewModel = waterViewModel,  // ← Pass WaterViewModel
                mealViewModel = mealViewModel      // ← Pass MealViewModel
            )
        }
        composable("add_meal") {
            AddMealScreen(
                onMealAdded = { navController.popBackStack() },
                mealViewModel = mealViewModel
            )
        }
    }
}
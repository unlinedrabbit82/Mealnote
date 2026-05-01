# MealNote 💧🍽️

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)

**MealNote** is a hydration and meal-tracking application designed to help users build healthy habits. Track your daily water intake, log meals with photos, and monitor your nutrition - all with complete privacy. All data stays on your device. No accounts. No cloud storage.

## 📱 Screenshots

<div align="center">
  <img src="screenshots/home.png" width="200" alt="Home Screen"/>
  <img src="screenshots/add_meal.png" width="200" alt="Add Meal Screen"/>
  <img src="screenshots/stats.png" width="200" alt="Statistics Screen"/>
  <img src="screenshots/charts.png" width="200" alt="Charts Screen"/>
</div>

## ✨ Features

### 💧 Water Tracking
- Log water in 250ml, 500ml, or 750ml increments
- Visual progress bar showing daily goal
- Configurable daily water goal (500-4000ml)
- Shows water in ml, fluid ounces, and bottles (500ml)
- Lifetime water tracking (never resets)

### 🍽️ Meal Logging
- Add meals with name and time (Breakfast/Lunch/Dinner/Snack)
- Take photos with CameraX or select from gallery
- Track calories and macros:
  - Protein (g)
  - Carbs (g)
  - Fat (g)
  - Sodium (mg)
  - Fiber (g)
- AI-powered meal name suggestions from photos (on-device)

### 📊 Statistics & Charts
- Today's macros vs lifetime totals
- Meal distribution (Breakfast/Lunch/Dinner/Snack)
- Water intake in ml, oz, and bottles
- Macro ratio breakdown
- Weekly calorie chart
- Daily meal count overview

### 🔔 Reminders
- Customizable water drinking reminders
- Meal logging notifications
- Set reminder times that work for you

### 🎨 Design
- Material 3 design with light/dark theme
- Smooth navigation between screens
- Intuitive and user-friendly interface

### 🔒 Privacy First
- All data stored locally on your device
- No accounts or sign-ups required
- No cloud storage or servers
- AI processing runs entirely on-device
- Your health data belongs to you

## 🛠️ Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Kotlin |
| **UI Toolkit** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **Minimum SDK** | API 24 (Android 7.0) |
| **Target SDK** | API 34 (Android 14) |
| **Camera** | CameraX |
| **AI/ML** | Google ML Kit (Image Labeling) |
| **Reminders** | WorkManager |
| **Image Loading** | Coil |
| **Async Operations** | Kotlin Coroutines & Flow |
| **Data Persistence** | SharedPreferences + Gson |

## 📁 Project Structure
app/src/main/java/com/mealnote/app/
├── MainActivity.kt # Entry point with navigation
├── ui/
│ ├── screens/ # Compose screens
│ │ ├── HomeScreen.kt # Water tracking & recent meals
│ │ ├── SettingsScreen.kt # User preferences
│ │ ├── StatisticsScreen.kt # Stats and macros
│ │ ├── AddMealScreen.kt # Meal entry with AI
│ │ ├── AllMealsScreen.kt # Full meal history
│ │ └── GraphsScreen.kt # Weekly charts
│ ├── viewmodels/ # ViewModels (StateFlow)
│ │ ├── WaterViewModel.kt
│ │ ├── MealViewModel.kt
│ │ └── SettingsViewModel.kt
│ └── theme/ # Compose theme
│ └── Theme.kt
├── data/
│ └── repository/ # Data repositories
│ ├── WaterRepository.kt
│ ├── MealRepository.kt
│ └── SettingsRepository.kt
└── notifications/ # Reminder system
├── ReminderWorker.kt
└── ReminderScheduler.kt

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
## 🚀 Getting Started

### Prerequisites

- Android Studio (Latest stable version)
- JDK 17 or higher
- Android SDK API 34

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/mealnote.git
   cd mealnote
Open in Android Studio

File → Open → Select the project folder

Wait for Gradle sync to complete

Build and run

Connect an Android device or start an emulator (API 24+)

Click the Run button (green triangle) or press Shift + F10

Building a Release APK/AAB
Generate signed bundle

text
Build → Generate Signed Bundle / APK
→ Select "Android App Bundle"
→ Create or select keystore
→ Select "release" build variant
→ Finish
Output location

AAB: app/build/outputs/bundle/release/app-release.aab

APK: app/build/outputs/apk/release/app-release.apk

📖 How to Use
Setting Up
Open the app - you'll see the Home screen

Go to Settings to set your daily water goal

Configure reminders if desired

Tracking Water
On the Home screen, tap +250ml, +500ml, or +750ml

Watch the progress bar fill up

Your daily water resets automatically at midnight

Lifetime water tracks everything you've ever logged

Logging a Meal
Tap "➕ Add Meal" or "Log Meal"

Enter meal name (or take a photo for AI suggestion)

Select meal time (Breakfast/Lunch/Dinner/Snack)

Enter calories and macros (optional)

Take a photo or select from gallery

Tap "Save Meal"

Viewing Statistics
Tap "📊 Statistics" from Home screen

View today's macros vs lifetime totals

See meal distribution and water consumption

Tap "📈 View Charts & Graphs" for weekly charts

Managing Meals
Delete meals by tapping the 🗑️ button on any meal entry

View all meals by tapping "📋 View All Meals"

🤖 AI Feature
MealNote uses Google ML Kit for on-device image recognition:

When you take or select a photo, the AI analyzes it

The app suggests a meal name based on what it detects (e.g., "Pizza", "Salad")

You can accept or edit the suggestion before saving

All processing happens on your device - no images ever leave your phone

No internet connection required for the AI to work (after initial model download)

🔧 Configuration
Water Goal
Go to Settings → Drag the slider

Range: 500ml to 4000ml

Saves automatically

Reminders
Go to Settings → Toggle water/meal reminders

For meal reminders, add custom times (e.g., 08:00, 12:00, 18:00)

📋 Permissions
Permission	Purpose	Required?
Camera	Take photos of meals	Optional
Notifications	Send reminders	Optional
Storage (photos)	Save and load meal photos	Optional
All permissions are requested at runtime and can be denied.

🧪 Testing
Running tests
bash
# Unit tests
./gradlew test

# Instrumentation tests
./gradlew connectedAndroidTest
Manual test checklist
Water tracking adds correctly

Water resets at midnight

Add meal with all fields

Take photo with camera

Select photo from gallery

AI suggests meal name from photo

Statistics show correct data

Charts update correctly

Delete meal removes it

Settings save and persist

Reminders trigger (requires device testing)

📝 Known Issues
The touch flicker issue on some emulators (fixed on physical devices)

First-time ML Kit model download may take a few seconds

Water lifetime tracking starts from first use (not retroactive)

🗺️ Roadmap
Room Database migration for better query capabilities

Cloud backup option (optional, user-controlled)

Barcode scanning for packaged foods

Recipe import and meal planning

Export data to CSV/PDF

Wear OS companion app

More detailed charts (line graphs, pie charts)

Food database with nutritional info

🤝 Contributing
This is a course project for Mobile Application Development (Spring 2026). Not currently accepting contributions, but feedback is welcome!

📄 License
This project is created for educational purposes as part of a university course.

👥 Team
Name	Role	Responsibilities
Haven Herring	UI/UX & Feature Implementation Lead	Database, notifications, UI design
Hannah Robertson	Lead Developer & Systems Integration	Architecture, camera, core features, charts
🙏 Acknowledgments
Google for ML Kit and Android documentation

JetBrains for Kotlin

Course instructor for Mobile Application Development

📧 Contact
GitHub Issues: Create an issue

Email: hdherring@ualr.edu and hmrobertson@ualr.edu

📊 Key Features Summary
Feature	Status
Water Tracking	✅ Complete
Meal Logging	✅ Complete
Camera Integration	✅ Complete
AI Photo Recognition	✅ Complete
Statistics	✅ Complete
Charts	✅ Complete
Reminders	✅ Complete
Data Persistence	✅ Complete
Privacy First	✅ Complete
Built with ❤️ for healthier living

Report Bug · Request Feature

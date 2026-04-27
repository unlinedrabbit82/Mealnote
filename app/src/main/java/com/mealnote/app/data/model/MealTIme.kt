package com.mealnote.app.data.model

enum class MealTime {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK;

    companion object {
        fun fromString(value: String): MealTime {
            return when (value.uppercase()) {
                "BREAKFAST" -> BREAKFAST
                "LUNCH" -> LUNCH
                "DINNER" -> DINNER
                "SNACK" -> SNACK
                else -> SNACK
            }
        }

        fun getDisplayName(mealTime: MealTime): String {
            return when (mealTime) {
                BREAKFAST -> "🌅 Breakfast"
                LUNCH -> "☀️ Lunch"
                DINNER -> "🌙 Dinner"
                SNACK -> "🍎 Snack"
            }
        }

        fun getShortName(mealTime: MealTime): String {
            return when (mealTime) {
                BREAKFAST -> "Breakfast"
                LUNCH -> "Lunch"
                DINNER -> "Dinner"
                SNACK -> "Snack"
            }
        }

        fun getIcon(mealTime: MealTime): String {
            return when (mealTime) {
                BREAKFAST -> "🌅"
                LUNCH -> "☀️"
                DINNER -> "🌙"
                SNACK -> "🍎"
            }
        }

        fun getAllValues(): List<MealTime> {
            return listOf(BREAKFAST, LUNCH, DINNER, SNACK)
        }
    }
}
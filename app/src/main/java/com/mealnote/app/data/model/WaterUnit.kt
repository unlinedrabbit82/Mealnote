package com.mealnote.app.data.model

enum class WaterUnit {
    ML,
    OZ,
    CUPS;

    companion object {
        fun fromString(value: String): WaterUnit {
            return when (value.uppercase()) {
                "ML" -> ML
                "OZ" -> OZ
                "CUPS" -> CUPS
                else -> ML
            }
        }

        fun getDisplayName(unit: WaterUnit): String {
            return when (unit) {
                ML -> "Milliliters (ml)"
                OZ -> "Ounces (oz)"
                CUPS -> "Cups"
            }
        }

        fun getShortName(unit: WaterUnit): String {
            return when (unit) {
                ML -> "ml"
                OZ -> "oz"
                CUPS -> "cups"
            }
        }

        // Conversion methods
        fun convertToMl(amount: Int, fromUnit: WaterUnit): Int {
            return when (fromUnit) {
                ML -> amount
                OZ -> (amount * 29.5735).toInt()
                CUPS -> (amount * 236.588).toInt()
            }
        }

        fun convertFromMl(ml: Int, toUnit: WaterUnit): Double {
            return when (toUnit) {
                ML -> ml.toDouble()
                OZ -> ml / 29.5735
                CUPS -> ml / 236.588
            }
        }

        fun getPresetAmounts(unit: WaterUnit): List<Int> {
            return when (unit) {
                ML -> listOf(250, 500, 750, 1000)
                OZ -> listOf(8, 12, 16, 24)
                CUPS -> listOf(1, 2, 3, 4)
            }
        }

        fun formatAmount(amount: Int, unit: WaterUnit): String {
            return when (unit) {
                ML -> "$amount ml"
                OZ -> "${String.format("%.1f", amount / 29.5735)} oz"
                CUPS -> "${String.format("%.1f", amount / 236.588)} cups"
            }
        }
    }
}
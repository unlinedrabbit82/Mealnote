package com.mealnote.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mealnote.app.data.database.converters.Converters
import com.mealnote.app.data.database.dao.MealDao
import com.mealnote.app.data.database.dao.SettingsDao
import com.mealnote.app.data.database.dao.WaterDao
import com.mealnote.app.data.database.entities.MealEntry
import com.mealnote.app.data.database.entities.UserSettings
import com.mealnote.app.data.database.entities.WaterEntry

@Database(
    entities = [WaterEntry::class, MealEntry::class, UserSettings::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MealNoteDatabase : RoomDatabase() {

    abstract fun waterDao(): WaterDao
    abstract fun mealDao(): MealDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: MealNoteDatabase? = null

        fun getDatabase(context: Context): MealNoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealNoteDatabase::class.java,
                    "mealnote_database"
                )
                    .fallbackToDestructiveMigration()  // ← REMOVED THE ARGUMENT - just ()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class HabitCategory(val displayName: String) {
    PRAYER_FARD("Fard Prayer"),
    PRAYER_SUNNAH("Sunnah Prayer"),
    ADHKAR("Adhkar & Remembrance"),
    QURAN("Quran & Study"),
    CHARITY("Charity & Kindness"),
    FASTING("Fasting"),
    REFLECTION("Self-Reflection"),
    CUSTOM("Custom Wird")
}

enum class LogStatus {
    UNLOGGED,
    ON_TIME,
    LATE,
    MISSED,
    DONE,
    NOT_DONE
}

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val name: String,
    val arabicName: String? = null,
    val description: String = "",
    val category: String = HabitCategory.CUSTOM.name,
    val tier: Int = 1, // 1: Fard, 2: Sunnah/Adhkar, 3: Advanced, 0: Custom
    val isCore: Boolean = false,
    val isPrayer3State: Boolean = false,
    val isActive: Boolean = true,
    val orderIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habit_logs")
data class HabitLogEntity(
    @PrimaryKey val id: String, // Format: "${date}_${habitId}"
    val date: String, // YYYY-MM-DD
    val habitId: String,
    val status: String = LogStatus.UNLOGGED.name,
    val completed: Boolean = false,
    val loggedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val selectedTier: Int = 1,
    val hasCompletedOnboarding: Boolean = false,
    val adaptiveReminderEnabled: Boolean = true,
    val manualReminderMinutes: Int = 1290, // 21:30 = 9:30 PM
    val lastCheckInDate: String = ""
)

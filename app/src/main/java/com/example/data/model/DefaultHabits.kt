package com.example.data.model

object DefaultHabits {
    fun getCoreHabitsForTier(tier: Int): List<HabitEntity> {
        val fardHabits = listOf(
            HabitEntity(
                id = "prayer_fajr",
                name = "Fajr Prayer",
                arabicName = "صلاة الفجر",
                description = "Dawn prayer",
                category = HabitCategory.PRAYER_FARD.name,
                tier = 1,
                isCore = true,
                isPrayer3State = true,
                orderIndex = 1
            ),
            HabitEntity(
                id = "prayer_dhuhr",
                name = "Dhuhr Prayer",
                arabicName = "صلاة الظهر",
                description = "Noon prayer",
                category = HabitCategory.PRAYER_FARD.name,
                tier = 1,
                isCore = true,
                isPrayer3State = true,
                orderIndex = 2
            ),
            HabitEntity(
                id = "prayer_asr",
                name = "Asr Prayer",
                arabicName = "صلاة العصر",
                description = "Afternoon prayer",
                category = HabitCategory.PRAYER_FARD.name,
                tier = 1,
                isCore = true,
                isPrayer3State = true,
                orderIndex = 3
            ),
            HabitEntity(
                id = "prayer_maghrib",
                name = "Maghrib Prayer",
                arabicName = "صلاة المغرب",
                description = "Sunset prayer",
                category = HabitCategory.PRAYER_FARD.name,
                tier = 1,
                isCore = true,
                isPrayer3State = true,
                orderIndex = 4
            ),
            HabitEntity(
                id = "prayer_isha",
                name = "Isha Prayer",
                arabicName = "صلاة العشاء",
                description = "Night prayer",
                category = HabitCategory.PRAYER_FARD.name,
                tier = 1,
                isCore = true,
                isPrayer3State = true,
                orderIndex = 5
            )
        )

        val stage2Habits = listOf(
            HabitEntity(
                id = "prayer_rawatib",
                name = "Rawatib Sunnah Prayers",
                arabicName = "السنن الرواتب",
                description = "Sunnah prayers before/after Fard (12 Rak'ahs)",
                category = HabitCategory.PRAYER_SUNNAH.name,
                tier = 2,
                isCore = true,
                isPrayer3State = false,
                orderIndex = 6
            ),
            HabitEntity(
                id = "adhkar_morning",
                name = "Morning Adhkar",
                arabicName = "أذكار الصباح",
                description = "Remembrance after Fajr",
                category = HabitCategory.ADHKAR.name,
                tier = 2,
                isCore = true,
                isPrayer3State = false,
                orderIndex = 7
            ),
            HabitEntity(
                id = "adhkar_evening",
                name = "Evening Adhkar",
                arabicName = "أذكار المساء",
                description = "Remembrance after Asr/Maghrib",
                category = HabitCategory.ADHKAR.name,
                tier = 2,
                isCore = true,
                isPrayer3State = false,
                orderIndex = 8
            )
        )

        val stage3Habits = listOf(
            HabitEntity(
                id = "prayer_tahajjud",
                name = "Tahajjud / Qiyam",
                arabicName = "قيام الليل والتهجد",
                description = "Voluntary night vigil prayer",
                category = HabitCategory.PRAYER_SUNNAH.name,
                tier = 3,
                isCore = true,
                isPrayer3State = false,
                orderIndex = 9
            ),
            HabitEntity(
                id = "quran_daily",
                name = "Daily Quran Recitation",
                arabicName = "ورد القرآن اليومي",
                description = "Fixed daily Quran portion (Hizb/Rub/Pages)",
                category = HabitCategory.QURAN.name,
                tier = 3,
                isCore = true,
                isPrayer3State = false,
                orderIndex = 10
            )
        )

        return when (tier) {
            1 -> fardHabits
            2 -> fardHabits + stage2Habits
            else -> fardHabits + stage2Habits + stage3Habits
        }
    }
}

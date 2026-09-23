package com.example.data.repository

import com.example.data.local.HabitDao
import com.example.data.local.HabitLogDao
import com.example.data.local.UserSettingsDao
import com.example.data.model.DefaultHabits
import com.example.data.model.HabitCategory
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.data.model.LogStatus
import com.example.data.model.UserSettingsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class MuhasabahRepository(
    private val habitDao: HabitDao,
    private val habitLogDao: HabitLogDao,
    private val userSettingsDao: UserSettingsDao
) {
    val activeHabits: Flow<List<HabitEntity>> = habitDao.getActiveHabits()
    val allHabits: Flow<List<HabitEntity>> = habitDao.getAllHabits()
    val allLogs: Flow<List<HabitLogEntity>> = habitLogDao.getAllLogs()
    val settings: Flow<UserSettingsEntity?> = userSettingsDao.getSettings()

    fun getLogsForDate(date: String): Flow<List<HabitLogEntity>> {
        return habitLogDao.getLogsForDate(date)
    }

    suspend fun getSettingsSync(): UserSettingsEntity {
        return withContext(Dispatchers.IO) {
            userSettingsDao.getSettingsSync() ?: UserSettingsEntity().also {
                userSettingsDao.insertSettings(it)
            }
        }
    }

    suspend fun initializeWithTier(tier: Int) {
        withContext(Dispatchers.IO) {
            // Save settings
            val currentSettings = userSettingsDao.getSettingsSync() ?: UserSettingsEntity()
            userSettingsDao.insertSettings(
                currentSettings.copy(
                    selectedTier = tier,
                    hasCompletedOnboarding = true
                )
            )

            // Sync core habits for this tier
            habitDao.clearCoreHabits()
            val coreHabits = DefaultHabits.getCoreHabitsForTier(tier)
            habitDao.insertHabits(coreHabits)
        }
    }

    suspend fun updateTier(newTier: Int) {
        withContext(Dispatchers.IO) {
            val currentSettings = userSettingsDao.getSettingsSync() ?: UserSettingsEntity()
            userSettingsDao.updateSettings(
                currentSettings.copy(selectedTier = newTier)
            )
            habitDao.clearCoreHabits()
            val coreHabits = DefaultHabits.getCoreHabitsForTier(newTier)
            habitDao.insertHabits(coreHabits)
        }
    }

    suspend fun logHabit(
        date: String,
        habitId: String,
        status: LogStatus,
        completed: Boolean,
        timestamp: Long = System.currentTimeMillis()
    ) {
        withContext(Dispatchers.IO) {
            val logId = "${date}_${habitId}"
            val log = HabitLogEntity(
                id = logId,
                date = date,
                habitId = habitId,
                status = status.name,
                completed = completed,
                loggedAtTimestamp = timestamp
            )
            habitLogDao.insertLog(log)

            // Update user last check-in date
            val currentSettings = userSettingsDao.getSettingsSync() ?: UserSettingsEntity()
            userSettingsDao.insertSettings(currentSettings.copy(lastCheckInDate = date))
        }
    }

    suspend fun addCustomHabit(
        name: String,
        arabicName: String?,
        category: HabitCategory,
        description: String = ""
    ): String {
        return withContext(Dispatchers.IO) {
            val id = "custom_" + UUID.randomUUID().toString().substring(0, 8)
            val customHabit = HabitEntity(
                id = id,
                name = name.trim(),
                arabicName = arabicName?.trim()?.takeIf { it.isNotBlank() },
                description = description.trim(),
                category = category.name,
                tier = 0,
                isCore = false,
                isPrayer3State = false,
                isActive = true,
                orderIndex = 100,
                createdAt = System.currentTimeMillis()
            )
            habitDao.insertHabit(customHabit)
            id
        }
    }

    suspend fun toggleHabitActiveState(habitId: String, isActive: Boolean) {
        withContext(Dispatchers.IO) {
            val habit = habitDao.getHabitById(habitId) ?: return@withContext
            habitDao.updateHabit(habit.copy(isActive = isActive))
        }
    }

    suspend fun deleteCustomHabit(habitId: String) {
        withContext(Dispatchers.IO) {
            habitDao.deleteCustomHabit(habitId)
            habitLogDao.deleteLogsForHabit(habitId)
        }
    }

    suspend fun updateReminderSettings(adaptiveEnabled: Boolean, manualMinutes: Int) {
        withContext(Dispatchers.IO) {
            val currentSettings = userSettingsDao.getSettingsSync() ?: UserSettingsEntity()
            userSettingsDao.updateSettings(
                currentSettings.copy(
                    adaptiveReminderEnabled = adaptiveEnabled,
                    manualReminderMinutes = manualMinutes
                )
            )
        }
    }

    suspend fun clearAllData() {
        withContext(Dispatchers.IO) {
            habitDao.clearAll()
            habitLogDao.clearAll()
            userSettingsDao.clearSettings()
        }
    }

    suspend fun populateSampleHistory() {
        withContext(Dispatchers.IO) {
            // First ensure Tier 2 or 3 is configured
            val settings = userSettingsDao.getSettingsSync() ?: UserSettingsEntity(selectedTier = 2, hasCompletedOnboarding = true)
            userSettingsDao.insertSettings(settings.copy(hasCompletedOnboarding = true))

            habitDao.clearCoreHabits()
            val coreHabits = DefaultHabits.getCoreHabitsForTier(settings.selectedTier)
            habitDao.insertHabits(coreHabits)

            // Add sample custom habit to demonstrate burnout prevention & insights
            val sampleCustomHabit = HabitEntity(
                id = "custom_duha_prayer",
                name = "Duha Prayer (Forenoon)",
                arabicName = "صلاة الضحى",
                description = "2-4 voluntary rak'ahs in the forenoon",
                category = HabitCategory.PRAYER_SUNNAH.name,
                tier = 0,
                isCore = false,
                isPrayer3State = false,
                isActive = true,
                orderIndex = 20,
                createdAt = System.currentTimeMillis() - (30L * 86400000L)
            )
            habitDao.insertHabit(sampleCustomHabit)

            val sampleCustomHabit2 = HabitEntity(
                id = "custom_istighfar",
                name = "100x Daily Istighfar",
                arabicName = "الاستغفار اليومي",
                description = "Daily seek of forgiveness",
                category = HabitCategory.ADHKAR.name,
                tier = 0,
                isCore = false,
                isPrayer3State = false,
                isActive = true,
                orderIndex = 21,
                createdAt = System.currentTimeMillis() - (30L * 86400000L)
            )
            habitDao.insertHabit(sampleCustomHabit2)

            // Generate 35 days of realistic data
            val today = LocalDate.now()
            val sampleLogs = mutableListOf<HabitLogEntity>()

            for (i in 34 downTo 0) {
                val dateObj = today.minusDays(i.toLong())
                val dateStr = dateObj.format(DateTimeFormatter.ISO_LOCAL_DATE)

                // Vary check-in timestamp:
                // Days with late check-in (e.g. 23:30 = 11:30 PM) vs early check-in (e.g. 20:30 = 8:30 PM)
                val isLateNightCheckin = (i % 3 == 0)
                val checkinHour = if (isLateNightCheckin) 23 else 21
                val checkinMinute = if (isLateNightCheckin) 25 else 15
                val checkinTimestamp = dateObj.atTime(checkinHour, checkinMinute).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

                // Log Prayers
                val prayerIds = listOf("prayer_fajr", "prayer_dhuhr", "prayer_asr", "prayer_maghrib", "prayer_isha")
                for (pId in prayerIds) {
                    val status = if (i == 0) {
                        LogStatus.ON_TIME // today
                    } else if (i % 7 == 2 && pId == "prayer_fajr") {
                        LogStatus.LATE
                    } else if (i % 11 == 0 && pId == "prayer_dhuhr") {
                        LogStatus.LATE
                    } else {
                        LogStatus.ON_TIME
                    }
                    sampleLogs.add(
                        HabitLogEntity(
                            id = "${dateStr}_$pId",
                            date = dateStr,
                            habitId = pId,
                            status = status.name,
                            completed = status == LogStatus.ON_TIME || status == LogStatus.LATE,
                            loggedAtTimestamp = checkinTimestamp
                        )
                    )
                }

                // Log Adhkar (Notice lower completion on late-night check-in days to trigger Timing Bottleneck!)
                val adhkarIds = listOf("adhkar_morning", "adhkar_evening")
                for (aId in adhkarIds) {
                    val completed = if (isLateNightCheckin) {
                        i % 2 == 0 // Drops to ~30-40% completion when logged late!
                    } else {
                        true // High completion when logged on time!
                    }
                    sampleLogs.add(
                        HabitLogEntity(
                            id = "${dateStr}_$aId",
                            date = dateStr,
                            habitId = aId,
                            status = if (completed) LogStatus.DONE.name else LogStatus.NOT_DONE.name,
                            completed = completed,
                            loggedAtTimestamp = checkinTimestamp
                        )
                    )
                }

                // Log Rawatib
                val rawatibDone = i % 5 != 0
                sampleLogs.add(
                    HabitLogEntity(
                        id = "${dateStr}_prayer_rawatib",
                        date = dateStr,
                        habitId = "prayer_rawatib",
                        status = if (rawatibDone) LogStatus.DONE.name else LogStatus.NOT_DONE.name,
                        completed = rawatibDone,
                        loggedAtTimestamp = checkinTimestamp
                    )
                )

                // Log Custom 1 (Duha - low completion rate ~25% in last 10 days to trigger Burnout Prevention!)
                val duhaDone = if (i < 10) {
                    i == 2 || i == 7 // only 20% in last 10 days!
                } else {
                    i % 2 == 0
                }
                sampleLogs.add(
                    HabitLogEntity(
                        id = "${dateStr}_custom_duha_prayer",
                        date = dateStr,
                        habitId = "custom_duha_prayer",
                        status = if (duhaDone) LogStatus.DONE.name else LogStatus.NOT_DONE.name,
                        completed = duhaDone,
                        loggedAtTimestamp = checkinTimestamp
                    )
                )

                // Log Custom 2 (Istighfar - steady)
                val istighfarDone = i % 4 != 0
                sampleLogs.add(
                    HabitLogEntity(
                        id = "${dateStr}_custom_istighfar",
                        date = dateStr,
                        habitId = "custom_istighfar",
                        status = if (istighfarDone) LogStatus.DONE.name else LogStatus.NOT_DONE.name,
                        completed = istighfarDone,
                        loggedAtTimestamp = checkinTimestamp
                    )
                )
            }

            habitLogDao.insertLogs(sampleLogs)
        }
    }
}

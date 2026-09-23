package com.example

import com.example.data.model.HabitCategory
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.data.model.LogStatus
import com.example.domain.engine.AdaptiveReminderEngine
import com.example.domain.engine.InsightType
import com.example.domain.engine.InsightsEngine
import com.example.domain.engine.StatisticsEngine
import com.example.domain.engine.TimeCalculationUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DomainEngineUnitTest {

    @Test
    fun `circular mean correctly averages times spanning across midnight`() {
        // 11:50 PM (23 * 60 + 50 = 1430) and 12:10 AM (0 * 60 + 10 = 10)
        val minutes = listOf(1430, 10)
        val avg = TimeCalculationUtils.calculateCircularAverageMinutes(minutes)
        // Expected average is 0 (12:00 AM Midnight)
        assertEquals(0, avg)
    }

    @Test
    fun `circular mean handles uniform times correctly`() {
        val minutes = listOf(540, 540, 540) // 9:00 AM
        val avg = TimeCalculationUtils.calculateCircularAverageMinutes(minutes)
        assertEquals(540, avg)
    }

    @Test
    fun `streak calculation skips unlogged today without resetting streak`() {
        val today = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        val habit = HabitEntity(
            id = "fajr",
            name = "Fajr",
            category = HabitCategory.PRAYER_FARD.name,
            tier = 1,
            isActive = true
        )

        // Logs for past 5 days (excluding today)
        val logs = (1..5).map { daysAgo ->
            val dateStr = today.minusDays(daysAgo.toLong()).format(dateFormatter)
            HabitLogEntity(
                id = "${dateStr}_fajr",
                date = dateStr,
                habitId = "fajr",
                completed = true,
                status = LogStatus.ON_TIME.name
            )
        }

        val weeklyStats = StatisticsEngine.computeWeeklyStats(
            habits = listOf(habit),
            allLogs = logs
        )

        // Today is not logged yet, streak should start from yesterday and equal 5
        assertEquals(5, weeklyStats.checkInStreak)
        assertEquals(5, weeklyStats.perfectDayStreak)
    }

    @Test
    fun `level up streak enforces on-time for fard prayers`() {
        val today = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        val habit = HabitEntity(
            id = "fajr",
            name = "Fajr",
            category = HabitCategory.PRAYER_FARD.name,
            tier = 1,
            isActive = true
        )

        // User logged late for past 14 days
        val lateLogs = (1..14).map { daysAgo ->
            val dateStr = today.minusDays(daysAgo.toLong()).format(dateFormatter)
            HabitLogEntity(
                id = "${dateStr}_fajr",
                date = dateStr,
                habitId = "fajr",
                completed = true,
                status = LogStatus.LATE.name
            )
        }

        val insights = InsightsEngine.generateInsights(
            habits = listOf(habit),
            allLogs = lateLogs
        )

        // Should NOT trigger Level Up because Fard prayer was LATE, not ON_TIME
        val levelUpInsight = insights.find { it.type == InsightType.LEVEL_UP }
        assertEquals(null, levelUpInsight)
    }

    @Test
    fun `custom habits are not double-counted across categories in monthly stats`() {
        val today = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        val customQuranHabit = HabitEntity(
            id = "custom_quran",
            name = "Surah Al-Mulk",
            category = HabitCategory.QURAN.name,
            tier = 1,
            isCore = false,
            isActive = true
        )

        val dateStr = today.format(dateFormatter)
        val logs = listOf(
            HabitLogEntity(
                id = "${dateStr}_custom_quran",
                date = dateStr,
                habitId = "custom_quran",
                completed = true,
                status = LogStatus.DONE.name
            )
        )

        val monthlyStats = StatisticsEngine.computeMonthlyStats(
            habits = listOf(customQuranHabit),
            allLogs = logs
        )

        val quranPerf = monthlyStats.categoryPerformances.find { it.category == HabitCategory.QURAN }
        val customPerf = monthlyStats.categoryPerformances.find { it.category == HabitCategory.CUSTOM }

        // Quran performance should include it
        assertNotNull(quranPerf)
        assertEquals(1, quranPerf?.totalLogged)

        // Custom performance should NOT include the Quran-categorized habit
        assertEquals(null, customPerf)
    }

    @Test
    fun `yearly stats compute dynamic spiritual seasons from real logs`() {
        val today = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        val habit = HabitEntity(
            id = "fajr",
            name = "Fajr",
            category = HabitCategory.PRAYER_FARD.name,
            tier = 1,
            isActive = true
        )

        val dateStr = today.format(dateFormatter)
        val logs = listOf(
            HabitLogEntity(
                id = "${dateStr}_fajr",
                date = dateStr,
                habitId = "fajr",
                completed = true,
                status = LogStatus.ON_TIME.name
            )
        )

        val yearlyStats = StatisticsEngine.computeYearlyStats(
            habits = listOf(habit),
            allLogs = logs
        )

        assertTrue(yearlyStats.spiritualSeasons.isNotEmpty())
        val seasonWithLog = yearlyStats.spiritualSeasons.find { it.consistencyRate > 0f }
        assertNotNull(seasonWithLog)
        assertEquals(100f, seasonWithLog?.consistencyRate)
        assertEquals("Season of Spiritual Elevation", seasonWithLog?.title)
    }
}

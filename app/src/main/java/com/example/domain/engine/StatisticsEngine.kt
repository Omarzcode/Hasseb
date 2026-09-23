package com.example.domain.engine

import com.example.data.model.HabitCategory
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.data.model.LogStatus
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

data class DayCompletionSummary(
    val date: String,
    val dayOfWeekLetter: String, // e.g. "M", "T", "W"
    val completedCount: Int,
    val totalCount: Int,
    val percentage: Float
)

data class WeeklyStats(
    val thisWeekConsistency: Float,
    val lastWeekConsistency: Float,
    val percentageChange: Float,
    val checkInStreak: Int, // Unbroken days with at least 1 habit completed
    val perfectDayStreak: Int, // Unbroken days with 100% active habits completed on-time
    val averageCheckInTimeStr: String,
    val mostMissedHabitName: String?,
    val mostMissedCount: Int,
    val dailySummaries: List<DayCompletionSummary>
)

data class CategoryPerformance(
    val category: HabitCategory,
    val currentMonthRate: Float,
    val prevMonthRate: Float,
    val changeRate: Float,
    val totalLogged: Int,
    val totalCompleted: Int
)

data class RadarAxisData(
    val label: String,
    val arabicLabel: String,
    val value: Float // 0f to 1f
)

data class MonthlyStats(
    val monthName: String,
    val overallConsistency: Float,
    val categoryPerformances: List<CategoryPerformance>,
    val radarAxes: List<RadarAxisData>,
    val totalCheckInsThisMonth: Int
)

data class SpiritualSeason(
    val periodName: String,
    val dateRange: String,
    val consistencyRate: Float,
    val title: String,
    val narrative: String,
    val tag: String
)

data class YearlyStats(
    val totalActiveDays: Int,
    val totalPrayersCompleted: Int,
    val totalPrayersOnTime: Int,
    val onTimePercentage: Float,
    val overallSpiritualConsistency: Float,
    val spiritualSeasons: List<SpiritualSeason>
)

object StatisticsEngine {

    /**
     * Computes 7-day week-over-week statistics, streaks, and check-in times.
     */
    fun computeWeeklyStats(
        habits: List<HabitEntity>,
        allLogs: List<HabitLogEntity>
    ): WeeklyStats {
        val today = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        val activeHabits = habits.filter { it.isActive }
        val activeCount = activeHabits.size.coerceAtLeast(1)
        val activeHabitIds = activeHabits.map { it.id }.toSet()
        val logsByDate = allLogs.groupBy { it.date }

        // This week: past 7 days (today down to today-6)
        val thisWeekDays = (6 downTo 0).map { today.minusDays(it.toLong()) }
        val lastWeekDays = (13 downTo 7).map { today.minusDays(it.toLong()) }

        var thisWeekCompleted = 0
        var thisWeekTotal = 0
        val dailySummaries = mutableListOf<DayCompletionSummary>()
        val missedHabitCounts = mutableMapOf<String, Int>()
        val thisWeekTimestamps = mutableListOf<Long>()

        for (date in thisWeekDays) {
            val dateStr = date.format(dateFormatter)
            val dayLogs = logsByDate[dateStr] ?: emptyList()
            val completed = dayLogs.count { it.completed }
            val totalForDay = activeCount
            val dayPct = (completed.toFloat() / totalForDay).coerceIn(0f, 1f)

            dailySummaries.add(
                DayCompletionSummary(
                    date = dateStr,
                    dayOfWeekLetter = date.dayOfWeek.name.take(1),
                    completedCount = completed,
                    totalCount = totalForDay,
                    percentage = dayPct
                )
            )

            thisWeekCompleted += completed
            thisWeekTotal += totalForDay

            // Track missed habits among active habits
            for (habit in activeHabits) {
                val log = dayLogs.find { it.habitId == habit.id }
                if (log == null || !log.completed) {
                    missedHabitCounts[habit.name] = (missedHabitCounts[habit.name] ?: 0) + 1
                }
            }

            val maxTs = dayLogs.maxOfOrNull { it.loggedAtTimestamp }
            if (maxTs != null && maxTs > 0) thisWeekTimestamps.add(maxTs)
        }

        var lastWeekCompleted = 0
        var lastWeekTotal = 0
        for (date in lastWeekDays) {
            val dateStr = date.format(dateFormatter)
            val dayLogs = logsByDate[dateStr] ?: emptyList()
            lastWeekCompleted += dayLogs.count { it.completed }
            lastWeekTotal += activeCount
        }

        val thisWeekConsistency = if (thisWeekTotal > 0) (thisWeekCompleted.toFloat() / thisWeekTotal) * 100f else 0f
        val lastWeekConsistency = if (lastWeekTotal > 0) (lastWeekCompleted.toFloat() / lastWeekTotal) * 100f else 0f
        val change = thisWeekConsistency - lastWeekConsistency

        // 1. Check-In Streak: Unbroken consecutive days where at least one habit was completed.
        // If today is not yet checked in, we test yesterday so a daytime check-in view does not break the streak.
        val todayStr = today.format(dateFormatter)
        val todayLogs = logsByDate[todayStr] ?: emptyList()
        val todayHasCheckIn = todayLogs.any { it.completed }

        var checkInStreak = 0
        var checkDate = if (todayHasCheckIn) today else today.minusDays(1)
        while (true) {
            val dateStr = checkDate.format(dateFormatter)
            val dayLogs = logsByDate[dateStr]
            if (dayLogs != null && dayLogs.any { it.completed }) {
                checkInStreak++
                checkDate = checkDate.minusDays(1)
            } else {
                break
            }
        }

        // 2. Perfect Day Streak: Consecutive days where 100% of active habits were completed on-time.
        var perfectDayStreak = 0
        val isTodayPerfect = isDayFlawless(todayLogs, activeHabits)
        var perfectCheckDate = if (isTodayPerfect) today else today.minusDays(1)
        while (true) {
            val dateStr = perfectCheckDate.format(dateFormatter)
            val dayLogs = logsByDate[dateStr] ?: emptyList()
            if (isDayFlawless(dayLogs, activeHabits)) {
                perfectDayStreak++
                perfectCheckDate = perfectCheckDate.minusDays(1)
            } else {
                break
            }
        }

        // Most missed habit
        val mostMissedEntry = missedHabitCounts.maxByOrNull { it.value }
        val mostMissedName = if (mostMissedEntry != null && mostMissedEntry.value > 0) mostMissedEntry.key else null
        val mostMissedCount = mostMissedEntry?.value ?: 0

        // Average check-in time this week computed via circular mean to handle midnight wraparound
        val avgTimeStr = if (thisWeekTimestamps.isNotEmpty()) {
            val zone = ZoneId.systemDefault()
            val minutesList = thisWeekTimestamps.map {
                val t = Instant.ofEpochMilli(it).atZone(zone).toLocalTime()
                t.hour * 60 + t.minute
            }
            val avgMin = TimeCalculationUtils.calculateCircularAverageMinutes(minutesList)
            val h = avgMin / 60
            val m = avgMin % 60
            val ampm = if (h >= 12) "PM" else "AM"
            val displayH = if (h % 12 == 0) 12 else h % 12
            String.format(Locale.getDefault(), "%d:%02d %s", displayH, m, ampm)
        } else {
            "9:30 PM"
        }

        return WeeklyStats(
            thisWeekConsistency = thisWeekConsistency,
            lastWeekConsistency = lastWeekConsistency,
            percentageChange = change,
            checkInStreak = checkInStreak,
            perfectDayStreak = perfectDayStreak,
            averageCheckInTimeStr = avgTimeStr,
            mostMissedHabitName = mostMissedName,
            mostMissedCount = mostMissedCount,
            dailySummaries = dailySummaries
        )
    }

    /**
     * Helper to verify if a single day achieved 100% on-time completion across all active habits.
     * Note: Obligatory (Fard) prayers must be performed ON_TIME (not LATE) for a flawless day.
     */
    private fun isDayFlawless(
        dayLogs: List<HabitLogEntity>,
        activeHabits: List<HabitEntity>
    ): Boolean {
        if (activeHabits.isEmpty() || dayLogs.isEmpty()) return false
        val logsByHabitId = dayLogs.associateBy { it.habitId }

        for (habit in activeHabits) {
            val log = logsByHabitId[habit.id] ?: return false
            if (habit.category == HabitCategory.PRAYER_FARD.name) {
                if (log.status != LogStatus.ON_TIME.name) return false
            } else {
                if (!log.completed && log.status != LogStatus.DONE.name) return false
            }
        }
        return true
    }

    /**
     * Computes monthly statistics across 5 strict spiritual categories without double counting.
     */
    fun computeMonthlyStats(
        habits: List<HabitEntity>,
        allLogs: List<HabitLogEntity>
    ): MonthlyStats {
        val today = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        val logsByDate = allLogs.groupBy { it.date }

        // Current Month (past 30 days) vs Previous Month (days 31 to 60)
        val currentMonthDates = (0..29).map { today.minusDays(it.toLong()).format(dateFormatter) }
        val prevMonthDates = (30..59).map { today.minusDays(it.toLong()).format(dateFormatter) }

        val activeCategories = listOf(
            HabitCategory.PRAYER_FARD,
            HabitCategory.PRAYER_SUNNAH,
            HabitCategory.ADHKAR,
            HabitCategory.QURAN,
            HabitCategory.CUSTOM
        )

        val catPerformances = mutableListOf<CategoryPerformance>()
        for (cat in activeCategories) {
            // Strict category filtering: Each habit belongs to exactly one category
            val catHabits = habits.filter { it.category == cat.name }
            if (catHabits.isEmpty()) continue
            val catHabitIds = catHabits.map { it.id }.toSet()

            var curTotal = 0
            var curCompleted = 0
            for (d in currentMonthDates) {
                val dayLogs = logsByDate[d] ?: emptyList()
                for (l in dayLogs) {
                    if (l.habitId in catHabitIds) {
                        curTotal++
                        if (l.completed) curCompleted++
                    }
                }
            }

            var prevTotal = 0
            var prevCompleted = 0
            for (d in prevMonthDates) {
                val dayLogs = logsByDate[d] ?: emptyList()
                for (l in dayLogs) {
                    if (l.habitId in catHabitIds) {
                        prevTotal++
                        if (l.completed) prevCompleted++
                    }
                }
            }

            val curRate = if (curTotal > 0) (curCompleted.toFloat() / curTotal) * 100f else 0f
            val prevRate = if (prevTotal > 0) (prevCompleted.toFloat() / prevTotal) * 100f else 0f

            catPerformances.add(
                CategoryPerformance(
                    category = cat,
                    currentMonthRate = curRate,
                    prevMonthRate = prevRate,
                    changeRate = curRate - prevRate,
                    totalLogged = curTotal,
                    totalCompleted = curCompleted
                )
            )
        }

        // Compute Radar Axes (5 core spiritual balance dimensions)
        val radarAxes = listOf(
            RadarAxisData(
                label = "Fard Prayers",
                arabicLabel = "الفرائض",
                value = getCategoryRateNormalized(habits, logsByDate, currentMonthDates, HabitCategory.PRAYER_FARD)
            ),
            RadarAxisData(
                label = "Sunnah & Nawafil",
                arabicLabel = "السنن",
                value = getCategoryRateNormalized(habits, logsByDate, currentMonthDates, HabitCategory.PRAYER_SUNNAH)
            ),
            RadarAxisData(
                label = "Adhkar / Wird",
                arabicLabel = "الأذكار",
                value = getCategoryRateNormalized(habits, logsByDate, currentMonthDates, HabitCategory.ADHKAR)
            ),
            RadarAxisData(
                label = "Quran Recitation",
                arabicLabel = "القرآن",
                value = getCategoryRateNormalized(habits, logsByDate, currentMonthDates, HabitCategory.QURAN)
            ),
            RadarAxisData(
                label = "Custom Wirds",
                arabicLabel = "العادات الخاصة",
                value = getCategoryRateNormalized(habits, logsByDate, currentMonthDates, HabitCategory.CUSTOM)
            )
        )

        val totalActiveDaysThisMonth = currentMonthDates.count { (logsByDate[it]?.size ?: 0) > 0 }
        val overallRate = if (catPerformances.isNotEmpty()) catPerformances.map { it.currentMonthRate }.average().toFloat() else 0f

        return MonthlyStats(
            monthName = today.month.name.lowercase().replaceFirstChar { it.uppercase() },
            overallConsistency = overallRate,
            categoryPerformances = catPerformances,
            radarAxes = radarAxes,
            totalCheckInsThisMonth = totalActiveDaysThisMonth
        )
    }

    /**
     * Strict single-category rate calculation for radar balance wheel.
     */
    private fun getCategoryRateNormalized(
        habits: List<HabitEntity>,
        logsByDate: Map<String, List<HabitLogEntity>>,
        dates: List<String>,
        cat: HabitCategory
    ): Float {
        // Strict category match prevents custom habits from being double counted
        val catHabits = habits.filter { it.category == cat.name }
        if (catHabits.isEmpty()) return 0.5f // Default neutral baseline if no habits in category

        val catIds = catHabits.map { it.id }.toSet()
        var total = 0
        var completed = 0
        for (d in dates) {
            val logs = logsByDate[d] ?: emptyList()
            for (l in logs) {
                if (l.habitId in catIds) {
                    total++
                    if (l.completed) completed++
                }
            }
        }
        return if (total > 0) (completed.toFloat() / total).coerceIn(0.15f, 1f) else 0.5f
    }

    /**
     * Computes Yearly reflection statistics and dynamic, real Spiritual Seasons computed
     * from actual user logs per quarter of the year.
     */
    fun computeYearlyStats(
        habits: List<HabitEntity>,
        allLogs: List<HabitLogEntity>
    ): YearlyStats {
        val today = LocalDate.now()
        val currentYear = today.year
        val logsByDate = allLogs.groupBy { it.date }
        val totalActiveDays = logsByDate.size

        // Prayer counts dynamically resolved via HabitCategory.PRAYER_FARD (not hardcoded IDs)
        val fardHabits = habits.filter { it.category == HabitCategory.PRAYER_FARD.name }
        val fardHabitIds = fardHabits.map { it.id }.toSet()

        var totalPrayersCompleted = 0
        var totalPrayersOnTime = 0
        var totalPrayersLogged = 0

        for (log in allLogs) {
            if (log.habitId in fardHabitIds) {
                totalPrayersLogged++
                if (log.completed || log.status == LogStatus.ON_TIME.name || log.status == LogStatus.LATE.name) {
                    totalPrayersCompleted++
                }
                if (log.status == LogStatus.ON_TIME.name) {
                    totalPrayersOnTime++
                }
            }
        }

        val onTimePercentage = if (totalPrayersCompleted > 0) {
            (totalPrayersOnTime.toFloat() / totalPrayersCompleted) * 100f
        } else {
            100f
        }

        val totalHabitsLogged = allLogs.size
        val totalHabitsCompleted = allLogs.count { it.completed }
        val overallConsistency = if (totalHabitsLogged > 0) {
            (totalHabitsCompleted.toFloat() / totalHabitsLogged) * 100f
        } else {
            0f
        }

        // --- Real Spiritual Seasons computed from actual quarter date ranges ---
        data class QuarterDefinition(
            val name: String,
            val startDate: LocalDate,
            val endDate: LocalDate,
            val dateRangeStr: String
        )

        val quarters = listOf(
            QuarterDefinition(
                name = "Q1: Winter & Early Spring",
                startDate = LocalDate.of(currentYear, Month.JANUARY, 1),
                endDate = LocalDate.of(currentYear, Month.MARCH, 31),
                dateRangeStr = "Jan 1 – Mar 31, $currentYear"
            ),
            QuarterDefinition(
                name = "Q2: Spring & High Devotion",
                startDate = LocalDate.of(currentYear, Month.APRIL, 1),
                endDate = LocalDate.of(currentYear, Month.JUNE, 30),
                dateRangeStr = "Apr 1 – Jun 30, $currentYear"
            ),
            QuarterDefinition(
                name = "Q3: Summer Reflection",
                startDate = LocalDate.of(currentYear, Month.JULY, 1),
                endDate = LocalDate.of(currentYear, Month.SEPTEMBER, 30),
                dateRangeStr = "Jul 1 – Sep 30, $currentYear"
            ),
            QuarterDefinition(
                name = "Q4: Autumn & Year-End Focus",
                startDate = LocalDate.of(currentYear, Month.OCTOBER, 1),
                endDate = LocalDate.of(currentYear, Month.DECEMBER, 31),
                dateRangeStr = "Oct 1 – Dec 31, $currentYear"
            )
        )

        val computedSeasons = mutableListOf<SpiritualSeason>()

        for (q in quarters) {
            // Only include quarters that have commenced up to today
            if (q.startDate.isAfter(today)) continue

            val logsInQuarter = allLogs.filter { log ->
                try {
                    val logDate = LocalDate.parse(log.date)
                    !logDate.isBefore(q.startDate) && !logDate.isAfter(q.endDate) && !logDate.isAfter(today)
                } catch (e: Exception) {
                    false
                }
            }

            val totalInQ = logsInQuarter.size
            val completedInQ = logsInQuarter.count { it.completed || it.status == LogStatus.ON_TIME.name || it.status == LogStatus.DONE.name }

            val consistencyRate = if (totalInQ > 0) {
                (completedInQ.toFloat() / totalInQ) * 100f
            } else {
                0f
            }

            val (title, tag, narrative) = when {
                totalInQ == 0 -> Triple(
                    "Unrecorded Quarter",
                    "No Data",
                    "No wirds or check-ins were recorded during this period."
                )
                consistencyRate >= 90f -> Triple(
                    "Season of Spiritual Elevation",
                    "Peak Consistency",
                    "Exceptional devotion and steadfastness across daily prayers and wirds (${consistencyRate.roundToInt()}% consistency). A deeply anchored season of istiqamah."
                )
                consistencyRate >= 75f -> Triple(
                    "Season of Balanced Foundation",
                    "Steady Core",
                    "Strong consistency anchored in obligatory prayers and daily remembrance (${consistencyRate.roundToInt()}% consistency)."
                )
                consistencyRate >= 50f -> Triple(
                    "Season of Mindful Rhythm",
                    "Growing Steadiness",
                    "Encouraging habit engagement (${consistencyRate.roundToInt()}% consistency). Deepening reflection windows will help reinforce steadfastness."
                )
                else -> Triple(
                    "Season of Renewal",
                    "Emerging Focus",
                    "Early steps of mindful accountability (${consistencyRate.roundToInt()}% consistency). Small, consistent deeds remain the most beloved to Allah."
                )
            }

            computedSeasons.add(
                SpiritualSeason(
                    periodName = q.name,
                    dateRange = q.dateRangeStr,
                    consistencyRate = consistencyRate,
                    title = title,
                    narrative = narrative,
                    tag = tag
                )
            )
        }

        // Fallback in case the user is at the very start of the year or has no active quarters
        if (computedSeasons.isEmpty()) {
            computedSeasons.add(
                SpiritualSeason(
                    periodName = "Current Year",
                    dateRange = "$currentYear",
                    consistencyRate = overallConsistency,
                    title = if (overallConsistency >= 75f) "Season of Steadiness" else "Season of Sincere Effort",
                    narrative = "Reflecting ${totalActiveDays} days of mindful accountability throughout the year.",
                    tag = if (overallConsistency >= 75f) "Steady Core" else "Active Focus"
                )
            )
        }

        return YearlyStats(
            totalActiveDays = totalActiveDays.coerceAtLeast(1),
            totalPrayersCompleted = totalPrayersCompleted,
            totalPrayersOnTime = totalPrayersOnTime,
            onTimePercentage = onTimePercentage,
            overallSpiritualConsistency = overallConsistency,
            spiritualSeasons = computedSeasons
        )
    }
}

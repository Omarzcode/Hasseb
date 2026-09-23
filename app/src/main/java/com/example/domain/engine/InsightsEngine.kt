package com.example.domain.engine

import com.example.data.model.HabitCategory
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.data.model.LogStatus
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

enum class InsightType {
    LEVEL_UP,
    BURNOUT_PREVENTION,
    TIMING_BOTTLENECK,
    CONSISTENCY_CELEBRATION,
    MINDFUL_NUDGE
}

data class InsightCardModel(
    val id: String,
    val type: InsightType,
    val title: String,
    val body: String,
    val highlightMetric: String? = null,
    val targetHabitId: String? = null,
    val targetHabitName: String? = null,
    val actionLabel: String? = null,
    val dateGenerated: String = LocalDate.now().toString()
)

object InsightsEngine {

    fun generateInsights(
        habits: List<HabitEntity>,
        allLogs: List<HabitLogEntity>
    ): List<InsightCardModel> {
        val insights = mutableListOf<InsightCardModel>()
        if (habits.isEmpty() || allLogs.isEmpty()) {
            insights.add(
                InsightCardModel(
                    id = "welcome_nudge",
                    type = InsightType.MINDFUL_NUDGE,
                    title = "The Journey of Muhasabah",
                    body = "“Take account of yourselves before you are taken to account.” As you log your daily wirds, this space will gently surface caring reflections to support your spiritual steadiness.",
                    actionLabel = "Start Daily Check-in"
                )
            )
            return insights
        }

        val activeHabits = habits.filter { it.isActive }
        val activeHabitIds = activeHabits.map { it.id }.toSet()
        val habitMap = habits.associateBy { it.id }

        // Group logs by date
        val logsByDate = allLogs.groupBy { it.date }
        val today = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        // --- 1. LEVEL UP TRIGGER & PERFECT DAY STREAK ---
        // A "perfect day" requires 100% completion of active habits.
        // For Fard prayers (PRAYER_FARD), completion must be ON_TIME (not LATE or MISSED).
        // If today is still in progress (not yet a perfect day), we evaluate backwards from yesterday
        // so that viewing insights earlier in the day does not prematurely reset a 14-day streak.
        val todayStr = today.format(dateFormatter)
        val todayLogs = logsByDate[todayStr] ?: emptyList()
        val isTodayPerfect = isDayFlawless(todayLogs, activeHabits)

        var perfectDayStreak = 0
        var checkDate = if (isTodayPerfect) today else today.minusDays(1)

        while (true) {
            val d = checkDate.format(dateFormatter)
            val dayLogs = logsByDate[d] ?: emptyList()
            if (isDayFlawless(dayLogs, activeHabits)) {
                perfectDayStreak++
                checkDate = checkDate.minusDays(1)
            } else {
                break
            }
        }

        if (perfectDayStreak >= 14) {
            insights.add(
                InsightCardModel(
                    id = "trigger_level_up",
                    type = InsightType.LEVEL_UP,
                    title = "Milestone of Steadiness (Istiqamah)",
                    body = "Masha'Allah. You have maintained 100% on-time completion across all your wirds for $perfectDayStreak consecutive days. Your spiritual foundation is deeply rooted. If your heart feels ready, you might consider taking on a new gentle sunnah or custom wird.",
                    highlightMetric = "$perfectDayStreak Days Flawless",
                    actionLabel = "Add a Custom Wird"
                )
            )
        } else if (perfectDayStreak in 7..13) {
            insights.add(
                InsightCardModel(
                    id = "trigger_streak_7",
                    type = InsightType.CONSISTENCY_CELEBRATION,
                    title = "One Week of Steadiness",
                    body = "You have completed your spiritual check-ins with full consistency for $perfectDayStreak consecutive days. The Prophet ﷺ reminded us that the most beloved deeds to Allah are those done consistently, even if small.",
                    highlightMetric = "$perfectDayStreak-Day Perfect Streak",
                    actionLabel = null
                )
            )
        }

        // --- 2. BURNOUT PREVENTION TRIGGER ---
        // For custom or non-core habits: if completion rate over rolling 10-day window is < 40%
        val last10Dates = (0..9).map { today.minusDays(it.toLong()).format(dateFormatter) }
        val customHabits = habits.filter { (!it.isCore || it.category == HabitCategory.CUSTOM.name) && it.isActive }

        for (customHabit in customHabits) {
            var completedCount = 0
            var loggedDaysCount = 0

            for (dateStr in last10Dates) {
                val dayLogs = logsByDate[dateStr] ?: emptyList()
                val habitLog = dayLogs.find { it.habitId == customHabit.id }
                if (habitLog != null) {
                    loggedDaysCount++
                    if (habitLog.completed || habitLog.status == LogStatus.DONE.name) {
                        completedCount++
                    }
                }
            }

            // Calculate rate if there is enough data
            val rate = if (loggedDaysCount >= 5) (completedCount.toFloat() / loggedDaysCount) * 100f else null
            if (rate != null && rate < 40f) {
                insights.add(
                    InsightCardModel(
                        id = "burnout_${customHabit.id}",
                        type = InsightType.BURNOUT_PREVENTION,
                        title = "Gentle Pause: ${customHabit.name}",
                        body = "Over the past 10 days, \"${customHabit.name}\" was completed ${completedCount} out of ${loggedDaysCount} days (${rate.roundToInt()}%). Spiritual journeys have natural ebbs and flows. Would you like to pause or scale back this habit without any pressure?",
                        highlightMetric = "${rate.roundToInt()}% in 10 Days",
                        targetHabitId = customHabit.id,
                        targetHabitName = customHabit.name,
                        actionLabel = "Pause This Habit"
                    )
                )
            }
        }

        // --- 3. TIMING BOTTLENECK TRIGGER ---
        // Correlate check-in submission time against completion rate across ALL active categories.
        // Split days into Late-Night (>= 23:00 / 11 PM or before 4 AM) vs Earlier (< 23:00).
        val zone = ZoneId.systemDefault()
        val lateNightDays = mutableListOf<String>()
        val regularDays = mutableListOf<String>()

        for ((dateStr, dayLogs) in logsByDate) {
            val maxTs = dayLogs.maxOfOrNull { it.loggedAtTimestamp } ?: 0L
            if (maxTs > 0L) {
                val hour = Instant.ofEpochMilli(maxTs).atZone(zone).hour
                if (hour >= 23 || hour < 4) {
                    lateNightDays.add(dateStr)
                } else {
                    regularDays.add(dateStr)
                }
            }
        }

        if (lateNightDays.size >= 3 && regularDays.size >= 3) {
            // Expand to check ALL active categories including Fard prayers and Custom wirds
            val categoriesToCheck = listOf(
                HabitCategory.PRAYER_FARD,
                HabitCategory.PRAYER_SUNNAH,
                HabitCategory.ADHKAR,
                HabitCategory.QURAN,
                HabitCategory.CUSTOM
            )

            for (cat in categoriesToCheck) {
                val habitsInCat = habits.filter { it.category == cat.name && it.isActive }
                if (habitsInCat.isEmpty()) continue
                val catHabitIds = habitsInCat.map { it.id }.toSet()

                // Calculate regular day completion
                var regularTotal = 0
                var regularCompleted = 0
                for (d in regularDays) {
                    val logs = logsByDate[d] ?: emptyList()
                    for (l in logs) {
                        if (l.habitId in catHabitIds) {
                            regularTotal++
                            if (l.completed || l.status == LogStatus.ON_TIME.name || l.status == LogStatus.DONE.name) {
                                regularCompleted++
                            }
                        }
                    }
                }

                // Calculate late night completion
                var lateTotal = 0
                var lateCompleted = 0
                for (d in lateNightDays) {
                    val logs = logsByDate[d] ?: emptyList()
                    for (l in logs) {
                        if (l.habitId in catHabitIds) {
                            lateTotal++
                            if (l.completed || l.status == LogStatus.ON_TIME.name || l.status == LogStatus.DONE.name) {
                                lateCompleted++
                            }
                        }
                    }
                }

                if (regularTotal >= 5 && lateTotal >= 5) {
                    val regularRate = (regularCompleted.toFloat() / regularTotal) * 100f
                    val lateRate = (lateCompleted.toFloat() / lateTotal) * 100f
                    val diff = regularRate - lateRate

                    if (diff >= 20f) {
                        val dropPercent = diff.roundToInt()
                        val catTitle = cat.displayName
                        insights.add(
                            InsightCardModel(
                                id = "timing_bottleneck_${cat.name}",
                                type = InsightType.TIMING_BOTTLENECK,
                                title = "Timing Reflection for $catTitle",
                                body = "When you log past 11:00 PM, your $catTitle completion drops by ${dropPercent}% compared to earlier in the evening. You might find greater tranquility checking in during a designated quiet reflection window after Asr or Maghrib.",
                                highlightMetric = "-${dropPercent}% Late at Night",
                                actionLabel = "Adjust Reflection Window"
                            )
                        )
                        break // Surface the most prominent timing bottleneck insight
                    }
                }
            }
        }

        // --- 4. GENERAL MINDFUL REFLECTION / ENCOURAGEMENT ---
        if (insights.isEmpty()) {
            val totalLoggedDays = logsByDate.size
            insights.add(
                InsightCardModel(
                    id = "general_steadiness",
                    type = InsightType.CONSISTENCY_CELEBRATION,
                    title = "Building Spiritual Rhythm",
                    body = "You have recorded $totalLoggedDays days of mindful reflection. Each check-in is an act of quiet accountability between you and your Creator.",
                    highlightMetric = "$totalLoggedDays Days Logged",
                    actionLabel = null
                )
            )
        }

        return insights
    }

    /**
     * Checks if a specific day had 100% on-time completion across all active habits.
     * Note: Obligatory (Fard) prayers must be ON_TIME (not LATE or MISSED).
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
                // Fard prayers must be on-time for a flawless istiqamah day
                if (log.status != LogStatus.ON_TIME.name) return false
            } else {
                if (!log.completed && log.status != LogStatus.DONE.name) return false
            }
        }
        return true
    }
}

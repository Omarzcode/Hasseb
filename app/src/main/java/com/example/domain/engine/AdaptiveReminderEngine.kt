package com.example.domain.engine

import com.example.data.model.HabitLogEntity
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class AdaptiveReminderInfo(
    val formattedTime: String,
    val averageMinutesOfDay: Int,
    val totalCheckInsAnalyzed: Int,
    val isCalculatedFromHabits: Boolean,
    val explanation: String
)

object AdaptiveReminderEngine {

    fun computeReminderTime(
        logs: List<HabitLogEntity>,
        isAdaptiveEnabled: Boolean,
        manualMinutes: Int
    ): AdaptiveReminderInfo {
        if (!isAdaptiveEnabled) {
            val hour = manualMinutes / 60
            val min = manualMinutes % 60
            val time = LocalTime.of(hour, min)
            val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
            return AdaptiveReminderInfo(
                formattedTime = time.format(formatter),
                averageMinutesOfDay = manualMinutes,
                totalCheckInsAnalyzed = 0,
                isCalculatedFromHabits = false,
                explanation = "Set manually to your designated reflection time"
            )
        }

        // Group logs by day and take the max loggedAtTimestamp as that day's check-in time
        val dailyCheckinTimestamps = logs
            .groupBy { it.date }
            .mapNotNull { (_, dayLogs) ->
                val maxTs = dayLogs.maxOfOrNull { it.loggedAtTimestamp }
                if (maxTs != null && maxTs > 0) maxTs else null
            }

        if (dailyCheckinTimestamps.isEmpty()) {
            val time = LocalTime.of(21, 30)
            val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
            return AdaptiveReminderInfo(
                formattedTime = time.format(formatter),
                averageMinutesOfDay = 1290,
                totalCheckInsAnalyzed = 0,
                isCalculatedFromHabits = false,
                explanation = "Default quiet evening window (adapts after you log check-ins)"
            )
        }

        // Look at the last 14 logged days for a responsive rolling average
        val recentTimestamps = dailyCheckinTimestamps.takeLast(14)
        val zone = ZoneId.systemDefault()

        val minutesList = recentTimestamps.map { ts ->
            val localTime = Instant.ofEpochMilli(ts).atZone(zone).toLocalTime()
            localTime.hour * 60 + localTime.minute
        }

        // Circular mean correctly handles check-in times spanning midnight (e.g. 11:50 PM and 12:10 AM)
        val avgMinutes = TimeCalculationUtils.calculateCircularAverageMinutes(minutesList)
        val hour = avgMinutes / 60
        val min = avgMinutes % 60
        val time = LocalTime.of(hour, min)
        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())

        return AdaptiveReminderInfo(
            formattedTime = time.format(formatter),
            averageMinutesOfDay = avgMinutes,
            totalCheckInsAnalyzed = recentTimestamps.size,
            isCalculatedFromHabits = true,
            explanation = "Calculated from your rolling average reflection time across ${recentTimestamps.size} active days"
        )
    }
}

package com.example.domain.engine

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

object TimeCalculationUtils {

    /**
     * Calculates the circular mean of a collection of minute-of-day values (0..1439).
     *
     * Standard arithmetic averaging fails when times span across midnight.
     * For example, check-ins at 11:50 PM (1430 min) and 12:10 AM (10 min) would produce
     * an arithmetic average of (1430 + 10) / 2 = 720 min (12:00 PM noon), which is 12 hours off.
     *
     * The circular mean treats the 24-hour day (1440 minutes) as a circle (0 to 2π radians).
     * 1. Each minute value is converted into an angle θ = (minute / 1440.0) * 2π.
     * 2. The sin(θ) and cos(θ) components are summed across all sample points.
     * 3. atan2(sumSin, sumCos) determines the resultant mean angle θ_mean.
     * 4. If θ_mean is negative, 2π is added to normalize it to the range [0, 2π).
     * 5. The normalized angle is converted back to minutes-of-day in the range 0..1439.
     */
    fun calculateCircularAverageMinutes(minutesList: List<Int>): Int {
        if (minutesList.isEmpty()) return 1290 // Default fallback: 9:30 PM (21 * 60 + 30)

        var sumSin = 0.0
        var sumCos = 0.0

        for (m in minutesList) {
            val angle = (m % 1440).toDouble() / 1440.0 * 2.0 * Math.PI
            sumSin += sin(angle)
            sumCos += cos(angle)
        }

        val avgAngle = atan2(sumSin, sumCos)
        val normalizedAngle = if (avgAngle < 0.0) avgAngle + (2.0 * Math.PI) else avgAngle
        val avgMinutes = ((normalizedAngle / (2.0 * Math.PI)) * 1440.0).roundToInt() % 1440
        return (avgMinutes + 1440) % 1440
    }
}

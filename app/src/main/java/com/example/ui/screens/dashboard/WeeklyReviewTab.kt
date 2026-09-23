package com.example.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.engine.DayCompletionSummary
import com.example.domain.engine.WeeklyStats
import com.example.ui.theme.Dimens
import com.example.ui.theme.OrganicCardShape
import com.example.ui.theme.extendedColors
import kotlin.math.roundToInt

@Composable
fun WeeklyReviewTab(
    weeklyStats: WeeklyStats?,
    modifier: Modifier = Modifier
) {
    if (weeklyStats == null) return
    val extendedColors = MaterialTheme.extendedColors

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
    ) {
        // Consistency Comparison Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("weekly_consistency_card"),
            shape = OrganicCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(Dimens.BorderWidthThin, extendedColors.sandBorder)
        ) {
            Column(modifier = Modifier.padding(Dimens.CardInnerPadding)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Weekly Consistency",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Week-over-week habit tracking rate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Delta badge
                    val isPositive = weeklyStats.percentageChange >= 0
                    Surface(
                        shape = RoundedCornerShape(Dimens.CardCornerSmall),
                        color = if (isPositive) extendedColors.sageContainer else extendedColors.sandCard,
                        border = BorderStroke(Dimens.BorderWidthThin, if (isPositive) extendedColors.sageContainerBorder else extendedColors.sandBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = Dimens.SpacingSmall, vertical = Dimens.SpacingExtraSmall),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = if (isPositive) MaterialTheme.colorScheme.primary else extendedColors.statusLateText,
                                modifier = Modifier.size(Dimens.IconSizeExtraSmall)
                            )
                            Spacer(modifier = Modifier.width(Dimens.SpacingExtraSmall))
                            Text(
                                text = "${if (isPositive) "+" else ""}${weeklyStats.percentageChange.roundToInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isPositive) MaterialTheme.colorScheme.primary else extendedColors.statusLateText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingLarge))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${weeklyStats.thisWeekConsistency.roundToInt()}%",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "This Week",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(extendedColors.sandBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${weeklyStats.lastWeekConsistency.roundToInt()}%",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Last Week",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Daily Breakdown Chart
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("weekly_daily_breakdown_card"),
            shape = OrganicCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(Dimens.BorderWidthThin, extendedColors.sandBorder)
        ) {
            Column(modifier = Modifier.padding(Dimens.CardInnerPadding)) {
                Text(
                    text = "Daily Completion Rate",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Percentage of active habits completed per day",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Dimens.SpacingLarge))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyStats.dailySummaries.forEach { daySummary ->
                        DailyBarItem(
                            daySummary = daySummary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 2-Column Summary Cards (Streak & Average Time)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
        ) {
            // Streak Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("weekly_streak_card"),
                shape = RoundedCornerShape(Dimens.CardCornerMedium),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(Dimens.BorderWidthThin, extendedColors.sandBorder)
            ) {
                Column(modifier = Modifier.padding(Dimens.CardInnerPaddingCompact)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = extendedColors.streakFlame,
                            modifier = Modifier.size(Dimens.IconSizeSmall)
                        )
                        Spacer(modifier = Modifier.width(Dimens.SpacingExtraSmall))
                        Text(
                            text = "Check-In Streak",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(Dimens.SpacingSmall))
                    Text(
                        text = "${weeklyStats.checkInStreak} Days",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (weeklyStats.perfectDayStreak > 0) "${weeklyStats.perfectDayStreak} perfect days" else "Active daily logs",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            // Average Time Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("weekly_avg_time_card"),
                shape = RoundedCornerShape(Dimens.CardCornerMedium),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(Dimens.BorderWidthThin, extendedColors.sandBorder)
            ) {
                Column(modifier = Modifier.padding(Dimens.CardInnerPaddingCompact)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Avg Time",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Dimens.IconSizeSmall)
                        )
                        Spacer(modifier = Modifier.width(Dimens.SpacingExtraSmall))
                        Text(
                            text = "Avg Check-In",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(Dimens.SpacingSmall))
                    Text(
                        text = weeklyStats.averageCheckInTimeStr,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Evening reflection",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // "Most Missed" Habit Card
        weeklyStats.mostMissedHabitName?.let { habitName ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.CardCornerMedium),
                colors = CardDefaults.cardColors(containerColor = extendedColors.sandCard),
                border = BorderStroke(Dimens.BorderWidthThin, extendedColors.sandBorder)
            ) {
                Row(
                    modifier = Modifier.padding(Dimens.CardInnerPaddingCompact),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(Dimens.SmallActionTarget)
                            .clip(CircleShape)
                            .background(extendedColors.terracottaGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = extendedColors.terracottaGold,
                            modifier = Modifier.size(Dimens.IconSizeMedium)
                        )
                    }
                    Spacer(modifier = Modifier.width(Dimens.SpacingMedium))
                    Column {
                        Text(
                            text = "Focus Area: $habitName",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Missed ${weeklyStats.mostMissedCount} times this week. Remember: small consistent steps lead to enduring spiritual habits.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyBarItem(
    daySummary: DayCompletionSummary,
    modifier: Modifier = Modifier
) {
    val extendedColors = MaterialTheme.extendedColors

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${(daySummary.percentage * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 9.sp
        )

        Spacer(modifier = Modifier.height(Dimens.SpacingExtraSmall))

        Box(
            modifier = Modifier
                .width(16.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(extendedColors.chartTrack),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(daySummary.percentage.coerceIn(0.05f, 1f))
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (daySummary.percentage >= 0.8f) MaterialTheme.colorScheme.primary else extendedColors.oliveLight)
            )
        }

        Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

        Text(
            text = daySummary.dayOfWeekLetter,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

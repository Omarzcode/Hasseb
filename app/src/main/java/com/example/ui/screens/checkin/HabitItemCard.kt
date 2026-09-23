package com.example.ui.screens.checkin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.data.model.LogStatus
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.Dimens
import com.example.ui.theme.OrganicCardShape
import com.example.ui.theme.extendedColors

@Composable
fun HabitItemCard(
    habit: HabitEntity,
    log: HabitLogEntity?,
    onSetPrayerStatus: (LogStatus) -> Unit,
    onToggleDone: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (habit.isPrayer3State) {
        Prayer3StateCard(
            habit = habit,
            currentStatus = log?.let {
                try { LogStatus.valueOf(it.status) } catch (e: Exception) { LogStatus.UNLOGGED }
            } ?: LogStatus.UNLOGGED,
            onStatusSelected = onSetPrayerStatus,
            modifier = modifier
        )
    } else {
        StandardHabitCard(
            habit = habit,
            isDone = log?.completed == true,
            onToggleDone = onToggleDone,
            modifier = modifier
        )
    }
}

@Composable
fun Prayer3StateCard(
    habit: HabitEntity,
    currentStatus: LogStatus,
    onStatusSelected: (LogStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = MaterialTheme.extendedColors

    val cardBorder = when (currentStatus) {
        LogStatus.ON_TIME, LogStatus.DONE -> extendedColors.statusOnTimeBorder.copy(alpha = 0.5f)
        LogStatus.LATE -> extendedColors.statusLateBorder
        LogStatus.MISSED, LogStatus.NOT_DONE -> extendedColors.statusMissedBorder
        LogStatus.UNLOGGED -> MaterialTheme.colorScheme.outlineVariant
    }

    val cardBg = when (currentStatus) {
        LogStatus.ON_TIME, LogStatus.DONE -> extendedColors.sageContainer.copy(alpha = 0.35f)
        LogStatus.LATE -> extendedColors.statusLateBg.copy(alpha = 0.45f)
        LogStatus.MISSED, LogStatus.NOT_DONE -> extendedColors.statusMissedBg.copy(alpha = 0.45f)
        LogStatus.UNLOGGED -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("prayer_card_${habit.id}"),
        shape = OrganicCardShape,
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(Dimens.BorderWidthThin, cardBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = Dimens.SpacingLarge, vertical = Dimens.SpacingMedium)
        ) {
            // Prayer Name & Subtitle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(extendedColors.terracottaGold)
                    )
                    Column {
                        Text(
                            text = habit.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp
                        )
                        habit.arabicName?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = ArabicFontFamily,
                                color = extendedColors.terracottaGold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Cultural pill badge
                Text(
                    text = "STAGE ${habit.tier}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 9.sp,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(20.dp))
                        .border(Dimens.BorderWidthThin, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingMedium))

            // 3-State Toggle Segment
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
            ) {
                // On-Time Button
                PrayerStateButton(
                    label = "On-time",
                    arabicLabel = "في وقتها",
                    isSelected = currentStatus == LogStatus.ON_TIME,
                    activeBg = extendedColors.statusOnTimeBg,
                    activeText = extendedColors.statusOnTimeText,
                    activeBorder = extendedColors.statusOnTimeBorder,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_ontime_${habit.id}"),
                    onClick = {
                        if (currentStatus == LogStatus.ON_TIME) onStatusSelected(LogStatus.UNLOGGED)
                        else onStatusSelected(LogStatus.ON_TIME)
                    }
                )

                // Late Button
                PrayerStateButton(
                    label = "Late",
                    arabicLabel = "متأخر",
                    isSelected = currentStatus == LogStatus.LATE,
                    activeBg = extendedColors.statusLateBg,
                    activeText = extendedColors.statusLateText,
                    activeBorder = extendedColors.statusLateBorder,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_late_${habit.id}"),
                    onClick = {
                        if (currentStatus == LogStatus.LATE) onStatusSelected(LogStatus.UNLOGGED)
                        else onStatusSelected(LogStatus.LATE)
                    }
                )

                // Missed Button
                PrayerStateButton(
                    label = "Missed",
                    arabicLabel = "فاتتني",
                    isSelected = currentStatus == LogStatus.MISSED,
                    activeBg = extendedColors.statusMissedBg,
                    activeText = extendedColors.statusMissedText,
                    activeBorder = extendedColors.statusMissedBorder,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_missed_${habit.id}"),
                    onClick = {
                        if (currentStatus == LogStatus.MISSED) onStatusSelected(LogStatus.UNLOGGED)
                        else onStatusSelected(LogStatus.MISSED)
                    }
                )
            }
        }
    }
}

@Composable
fun PrayerStateButton(
    label: String,
    arabicLabel: String,
    isSelected: Boolean,
    activeBg: Color,
    activeText: Color,
    activeBorder: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val extendedColors = MaterialTheme.extendedColors
    val bg = if (isSelected) activeBg else extendedColors.toggleIdleBg
    val textC = if (isSelected) activeText else MaterialTheme.colorScheme.primary
    val borderC = if (isSelected) activeBorder else extendedColors.toggleIdleBorder

    Surface(
        modifier = modifier
            .height(Dimens.TouchTargetMin)
            .clickable { onClick() },
        shape = RoundedCornerShape(Dimens.CardCornerMedium),
        color = bg,
        border = BorderStroke(Dimens.BorderWidthThin, borderC)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = textC,
                fontSize = 11.5.sp
            )
            Text(
                text = arabicLabel,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = ArabicFontFamily,
                color = textC.copy(alpha = 0.85f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun StandardHabitCard(
    habit: HabitEntity,
    isDone: Boolean,
    onToggleDone: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = MaterialTheme.extendedColors
    val borderColor = if (isDone) extendedColors.statusOnTimeBorder.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
    val bgColor = if (isDone) extendedColors.sageContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggleDone(!isDone) }
            .testTag("habit_card_${habit.id}"),
        shape = OrganicCardShape,
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(Dimens.BorderWidthThin, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.SpacingLarge, vertical = Dimens.SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isDone) MaterialTheme.colorScheme.primary else extendedColors.terracottaGold)
                    )

                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp
                    )

                    if (!habit.isCore) {
                        Text(
                            text = "CUSTOM",
                            style = MaterialTheme.typography.labelSmall,
                            color = extendedColors.terracottaGold,
                            fontSize = 8.5.sp,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(6.dp))
                                .border(Dimens.BorderWidthThin, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }

                if (!habit.arabicName.isNullOrBlank() || habit.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        habit.arabicName?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = ArabicFontFamily,
                                color = extendedColors.terracottaGold,
                                fontSize = 12.sp
                            )
                        }
                        if (!habit.arabicName.isNullOrBlank() && habit.description.isNotBlank()) {
                            Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                        }
                        if (habit.description.isNotBlank()) {
                            Text(
                                text = habit.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontStyle = FontStyle.Italic,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(Dimens.SpacingMedium))

            // Organic Circular Checkbox Button
            Box(
                modifier = Modifier
                    .size(Dimens.TouchTargetMin)
                    .clip(CircleShape)
                    .background(if (isDone) MaterialTheme.colorScheme.primary else extendedColors.toggleIdleBg)
                    .border(1.5.dp, if (isDone) MaterialTheme.colorScheme.primary else extendedColors.toggleIdleBorder, CircleShape)
                    .testTag("checkbox_${habit.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(Dimens.IconSizeMedium)
                    )
                }
            }
        }
    }
}

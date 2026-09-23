package com.example.ui.screens.insights

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.engine.InsightCardModel
import com.example.domain.engine.InsightType
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.Dimens
import com.example.ui.theme.OrganicCardShape
import com.example.ui.theme.extendedColors

@Composable
fun InsightsScreen(
    insights: List<InsightCardModel>,
    onPauseHabit: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onAddWird: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = MaterialTheme.extendedColors

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.ScreenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
    ) {
        item {
            Spacer(modifier = Modifier.height(Dimens.SpacingExtraSmall))

            // Header Banner
            Column {
                Text(
                    text = "Empathetic Insights",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "نصائح المحاسبة والتزكية",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = ArabicFontFamily,
                    color = extendedColors.terracottaGold
                )
                Spacer(modifier = Modifier.height(Dimens.SpacingExtraSmall))
                Text(
                    text = "A caring, non-judgmental mirror of your spiritual habits. All rules are computed 100% on your device with zero data leaving your phone.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Privacy assurance card
        item {
            PrivacyBadgeCard()
        }

        // List of Insight Cards
        items(insights, key = { it.id }) { insight ->
            InsightCard(
                insight = insight,
                onPauseHabit = { habitId -> onPauseHabit(habitId) },
                onNavigateToSettings = onNavigateToSettings,
                onAddWird = onAddWird
            )
        }

        item {
            SpiritualReflectionFooter()
        }

        item {
            Spacer(modifier = Modifier.height(Dimens.SpacingTriple))
        }
    }
}

@Composable
fun PrivacyBadgeCard() {
    val extendedColors = MaterialTheme.extendedColors

    Surface(
        shape = RoundedCornerShape(Dimens.CardCornerSmall),
        color = extendedColors.sageContainer.copy(alpha = 0.55f),
        border = BorderStroke(Dimens.BorderWidthThin, extendedColors.sageContainerBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Dimens.SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Private",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.IconSizeSmall)
            )
            Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
            Text(
                text = "Completely Private: Insights and correlations are processed locally in Room SQLite without any analytics or cloud dependencies.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

private data class InsightStyle(
    val icon: ImageVector,
    val accentColor: Color,
    val containerBg: Color,
    val borderC: Color,
    val titleColor: Color
)

@Composable
fun InsightCard(
    insight: InsightCardModel,
    onPauseHabit: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onAddWird: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = MaterialTheme.extendedColors

    val style = when (insight.type) {
        InsightType.LEVEL_UP -> InsightStyle(
            icon = Icons.Default.EmojiEvents,
            accentColor = extendedColors.celebrationGold,
            containerBg = extendedColors.insightEncouragingBg,
            borderC = extendedColors.insightEncouragingBorder,
            titleColor = extendedColors.insightEncouragingText
        )
        InsightType.BURNOUT_PREVENTION -> InsightStyle(
            icon = Icons.Default.Spa,
            accentColor = extendedColors.insightProtectiveAccent,
            containerBg = extendedColors.insightProtectiveBg,
            borderC = extendedColors.insightProtectiveBorder,
            titleColor = extendedColors.insightProtectiveText
        )
        InsightType.TIMING_BOTTLENECK -> InsightStyle(
            icon = Icons.Default.Bedtime,
            accentColor = extendedColors.insightInformativeAccent,
            containerBg = extendedColors.insightInformativeBg,
            borderC = extendedColors.insightInformativeBorder,
            titleColor = extendedColors.insightInformativeText
        )
        InsightType.CONSISTENCY_CELEBRATION -> InsightStyle(
            icon = Icons.Default.AutoAwesome,
            accentColor = extendedColors.celebrationGold,
            containerBg = extendedColors.insightEncouragingBg,
            borderC = extendedColors.insightEncouragingBorder,
            titleColor = extendedColors.insightEncouragingText
        )
        InsightType.MINDFUL_NUDGE -> InsightStyle(
            icon = Icons.Default.Psychology,
            accentColor = extendedColors.insightNudgeAccent,
            containerBg = extendedColors.insightNudgeBg,
            borderC = extendedColors.insightNudgeBorder,
            titleColor = extendedColors.insightNudgeText
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("insight_card_${insight.id}"),
        shape = OrganicCardShape,
        colors = CardDefaults.cardColors(containerColor = style.containerBg),
        border = BorderStroke(Dimens.BorderWidthThin, style.borderC)
    ) {
        Column(modifier = Modifier.padding(Dimens.CardInnerPadding)) {
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
                            .size(Dimens.SmallActionTarget)
                            .clip(CircleShape)
                            .background(style.accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = style.icon,
                            contentDescription = insight.type.name,
                            tint = style.accentColor,
                            modifier = Modifier.size(Dimens.IconSizeMedium)
                        )
                    }

                    Column {
                        Text(
                            text = insight.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = style.titleColor
                        )
                    }
                }

                insight.highlightMetric?.let { metric ->
                    Text(
                        text = metric,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = style.accentColor,
                        modifier = Modifier
                            .background(style.accentColor.copy(alpha = 0.14f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingMedium))

            Text(
                text = insight.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            // Action Button if applicable
            insight.actionLabel?.let { label ->
                Spacer(modifier = Modifier.height(Dimens.SpacingLarge))

                when (insight.type) {
                    InsightType.BURNOUT_PREVENTION -> {
                        Button(
                            onClick = {
                                insight.targetHabitId?.let { onPauseHabit(it) }
                            },
                            shape = RoundedCornerShape(Dimens.CardCornerSmall),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("action_pause_habit")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PauseCircle,
                                contentDescription = null,
                                modifier = Modifier.size(Dimens.IconSizeSmall)
                            )
                            Spacer(modifier = Modifier.width(Dimens.SpacingExtraSmall))
                            Text(label, style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    InsightType.LEVEL_UP -> {
                        Button(
                            onClick = onAddWird,
                            shape = RoundedCornerShape(Dimens.CardCornerSmall),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = extendedColors.celebrationGold,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("action_add_wird")
                        ) {
                            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }

                    InsightType.TIMING_BOTTLENECK -> {
                        OutlinedButton(
                            onClick = onNavigateToSettings,
                            shape = RoundedCornerShape(Dimens.CardCornerSmall),
                            border = BorderStroke(Dimens.BorderWidthThin, MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("action_adjust_timing")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(Dimens.IconSizeSmall)
                            )
                            Spacer(modifier = Modifier.width(Dimens.SpacingExtraSmall))
                            Text(
                                label,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    else -> {
                        // Default general action
                    }
                }
            }
        }
    }
}

@Composable
fun SpiritualReflectionFooter() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = OrganicCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(Dimens.BorderWidthThin, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(Dimens.CardInnerPadding)) {
            Text(
                text = "Guiding Principle of Muhasabah",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(Dimens.SpacingExtraSmall))
            Text(
                text = "The goal of spiritual tracking is never perfectionism or guilt, but continuous gentle awareness (Tazkiyah) and turning back with hope.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

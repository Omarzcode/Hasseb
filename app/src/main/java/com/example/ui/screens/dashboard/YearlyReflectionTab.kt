package com.example.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Spa
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.engine.SpiritualSeason
import com.example.domain.engine.YearlyStats
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.Dimens
import com.example.ui.theme.OrganicCardShape
import com.example.ui.theme.OrganicHeroShape
import com.example.ui.theme.extendedColors
import kotlin.math.roundToInt

@Composable
fun YearlyReflectionTab(
    yearlyStats: YearlyStats?,
    modifier: Modifier = Modifier
) {
    if (yearlyStats == null) return
    val extendedColors = MaterialTheme.extendedColors

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
    ) {
        // Year in Review Hero Banner with organic cultural styling
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("yearly_hero_card"),
            shape = OrganicHeroShape,
            colors = CardDefaults.cardColors(containerColor = extendedColors.heroBannerBg),
            border = BorderStroke(Dimens.BorderWidthThin, extendedColors.heroBannerBorder)
        ) {
            Column(
                modifier = Modifier.padding(Dimens.SpacingLarge)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Year in Review",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "الحصاد السنوي والتزكية",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = ArabicFontFamily,
                            color = extendedColors.heroBannerGold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(Dimens.TouchTargetMin)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(Dimens.IconSizeMedium)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingLarge))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "${yearlyStats.totalActiveDays}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Active Days",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }

                    Column {
                        Text(
                            text = "${yearlyStats.totalPrayersCompleted}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Prayers Logged",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }

                    Column {
                        Text(
                            text = "${yearlyStats.onTimePercentage.roundToInt()}%",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = extendedColors.celebrationGold
                        )
                        Text(
                            text = "On-Time Rate",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }
            }
        }

        // Section Title: Spiritual Seasons
        Text(
            text = "Your Spiritual Seasons",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = Dimens.SpacingExtraSmall)
        )

        Text(
            text = "A narrative synthesis of your spiritual consistency through the seasons of the year",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Narrative Seasons Cards
        yearlyStats.spiritualSeasons.forEach { season ->
            SpiritualSeasonCard(season = season)
        }
    }
}

@Composable
fun SpiritualSeasonCard(
    season: SpiritualSeason
) {
    val extendedColors = MaterialTheme.extendedColors

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                        text = season.periodName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = season.dateRange,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(Dimens.CardCornerSmall),
                    color = extendedColors.sageContainer
                ) {
                    Text(
                        text = "${season.consistencyRate.roundToInt()}% Consistency",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = Dimens.SpacingSmall, vertical = Dimens.SpacingExtraSmall)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

            Text(
                text = season.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = extendedColors.terracottaGold
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingExtraSmall))

            Text(
                text = season.narrative,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 19.sp
            )
        }
    }
}

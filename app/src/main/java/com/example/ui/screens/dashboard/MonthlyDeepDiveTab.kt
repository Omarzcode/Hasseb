package com.example.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.engine.CategoryPerformance
import com.example.domain.engine.MonthlyStats
import com.example.ui.theme.Dimens
import com.example.ui.theme.OrganicCardShape
import com.example.ui.theme.extendedColors
import kotlin.math.roundToInt

@Composable
fun MonthlyDeepDiveTab(
    monthlyStats: MonthlyStats?,
    modifier: Modifier = Modifier
) {
    if (monthlyStats == null) return
    val extendedColors = MaterialTheme.extendedColors

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
    ) {
        // Month Overall Score Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("monthly_overview_card"),
            shape = OrganicCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(Dimens.BorderWidthThin, extendedColors.sandBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.CardInnerPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${monthlyStats.monthName} Reflection",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${monthlyStats.totalCheckInsThisMonth} active reflection days",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${monthlyStats.overallConsistency.roundToInt()}%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Category Balance Wheel (Radar / Spider Chart)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("category_balance_wheel_card"),
            shape = OrganicCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(Dimens.BorderWidthThin, extendedColors.sandBorder)
        ) {
            Column(modifier = Modifier.padding(Dimens.CardInnerPadding)) {
                Text(
                    text = "Spiritual Balance Wheel",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Equilibrium across prayer, Quran, remembrance, and voluntary wirds",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

                RadarBalanceWheel(
                    axes = monthlyStats.radarAxes,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Category Performance Breakdown (% Change vs Previous Month)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = OrganicCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(Dimens.BorderWidthThin, extendedColors.sandBorder)
        ) {
            Column(modifier = Modifier.padding(Dimens.CardInnerPadding)) {
                Text(
                    text = "Category Performance vs. Prior Month",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Detailed shift in consistency per category",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Dimens.SpacingMedium))

                monthlyStats.categoryPerformances.forEach { catPerf ->
                    CategoryPerformanceRow(catPerf = catPerf)
                    Spacer(modifier = Modifier.height(Dimens.SpacingSmall))
                }
            }
        }
    }
}

@Composable
fun CategoryPerformanceRow(
    catPerf: CategoryPerformance
) {
    val extendedColors = MaterialTheme.extendedColors
    val isPositive = catPerf.changeRate >= 0
    val deltaText = "${if (isPositive) "+" else ""}${catPerf.changeRate.roundToInt()}%"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CardCornerSmall),
        color = extendedColors.sandCard,
        border = BorderStroke(Dimens.BorderWidthThin, extendedColors.sandBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.SpacingMedium, vertical = Dimens.SpacingSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = catPerf.category.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Current: ${catPerf.currentMonthRate.roundToInt()}% (Prev: ${catPerf.prevMonthRate.roundToInt()}%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isPositive) extendedColors.sageContainer else extendedColors.statusLateText.copy(alpha = 0.12f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = if (isPositive) MaterialTheme.colorScheme.primary else extendedColors.statusLateText,
                        modifier = Modifier.size(Dimens.IconSizeExtraSmall)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = deltaText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isPositive) MaterialTheme.colorScheme.primary else extendedColors.statusLateText
                    )
                }
            }
        }
    }
}

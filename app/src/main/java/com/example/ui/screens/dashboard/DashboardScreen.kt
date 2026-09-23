package com.example.ui.screens.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.engine.MonthlyStats
import com.example.domain.engine.WeeklyStats
import com.example.domain.engine.YearlyStats
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.Dimens
import com.example.ui.theme.extendedColors

enum class DashboardHorizon(val title: String) {
    WEEKLY("Weekly Review"),
    MONTHLY("Monthly Deep-Dive"),
    YEARLY("Yearly Reflection")
}

@Composable
fun DashboardScreen(
    weeklyStats: WeeklyStats?,
    monthlyStats: MonthlyStats?,
    yearlyStats: YearlyStats?,
    modifier: Modifier = Modifier
) {
    val extendedColors = MaterialTheme.extendedColors
    var selectedHorizonIndex by remember { mutableIntStateOf(0) }
    val horizons = DashboardHorizon.values()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.ScreenHorizontalPadding)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(Dimens.SpacingExtraSmall))

        // Title Header
        Text(
            text = "Reporting Dashboard",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "تقرير المحاسبة والمؤشرات الروحية",
            style = MaterialTheme.typography.titleMedium,
            fontFamily = ArabicFontFamily,
            color = extendedColors.terracottaGold
        )

        Spacer(modifier = Modifier.height(Dimens.SpacingMedium))

        // Tri-Horizon Tab Row
        TabRow(
            selectedTabIndex = selectedHorizonIndex,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedHorizonIndex]),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimens.CardCornerSmall))
                .testTag("dashboard_horizon_tabs")
        ) {
            horizons.forEachIndexed { index, horizon ->
                Tab(
                    selected = selectedHorizonIndex == index,
                    onClick = { selectedHorizonIndex = index },
                    text = {
                        Text(
                            text = horizon.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (selectedHorizonIndex == index) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpacingLarge))

        when (horizons[selectedHorizonIndex]) {
            DashboardHorizon.WEEKLY -> WeeklyReviewTab(weeklyStats = weeklyStats)
            DashboardHorizon.MONTHLY -> MonthlyDeepDiveTab(monthlyStats = monthlyStats)
            DashboardHorizon.YEARLY -> YearlyReflectionTab(yearlyStats = yearlyStats)
        }

        Spacer(modifier = Modifier.height(Dimens.SpacingTriple))
    }
}

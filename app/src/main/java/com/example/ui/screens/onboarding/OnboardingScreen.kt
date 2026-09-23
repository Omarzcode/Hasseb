package com.example.ui.screens.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.Dimens
import com.example.ui.theme.OrganicCardShape
import com.example.ui.theme.extendedColors

@Composable
fun OnboardingScreen(
    onSelectTier: (Int) -> Unit
) {
    val extendedColors = MaterialTheme.extendedColors
    var selectedTier by remember { mutableIntStateOf(1) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.SpacingLarge, vertical = Dimens.SpacingMedium)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Dimens.SpacingMedium))

            // Calm spiritual emblem
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(extendedColors.sageContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = "Spiritual Mindfulness",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.IconSizeLarge)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingMedium))

            Text(
                text = "Digital Muhasabah",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "محاسبة النفس",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = ArabicFontFamily,
                color = extendedColors.terracottaGold,
                modifier = Modifier.padding(top = Dimens.SpacingExtraSmall)
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

            Text(
                text = "A quiet, private space to reflect on your daily spiritual habits (wirds). Zero data ever leaves your device.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = Dimens.SpacingSmall)
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingLarge))

            Text(
                text = "Choose Your Starting Stage",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = Dimens.SpacingSmall)
            )

            Text(
                text = "Core habits for your tier are non-deletable to anchor your routine. You can change stages or add custom habits at any time.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = Dimens.SpacingMedium)
            )

            // Stage 1 Card
            TierSelectionCard(
                tierNumber = 1,
                title = "Stage 1 – Fard Focus",
                arabicSubtitle = "الفرائض الخمس",
                description = "Anchor your foundation with the 5 daily obligatory prayers.",
                habits = listOf(
                    "5 Daily Prayers (Fajr, Dhuhr, Asr, Maghrib, Isha)",
                    "3-State Logging: On-time / Late / Missed",
                    "Unlimited custom wirds can be added"
                ),
                isSelected = selectedTier == 1,
                onClick = { selectedTier = 1 }
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

            // Stage 2 Card
            TierSelectionCard(
                tierNumber = 2,
                title = "Stage 2 – Sunnah & Adhkar",
                arabicSubtitle = "السنن الرواتب والأذكار",
                description = "Deepen consistency with confirmed sunnahs and remembrance.",
                habits = listOf(
                    "All Stage 1 Fard Prayers",
                    "Rawatib Sunnah Prayers (12 Rak'ahs)",
                    "Morning & Evening Adhkar",
                    "Unlimited custom wirds"
                ),
                isSelected = selectedTier == 2,
                onClick = { selectedTier = 2 }
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

            // Stage 3 Card
            TierSelectionCard(
                tierNumber = 3,
                title = "Stage 3 – Advanced Devotion",
                arabicSubtitle = "قيام الليل والقرآن",
                description = "Comprehensive spiritual rhythm for steady dedicated seekers.",
                habits = listOf(
                    "All Stage 1 & 2 Habits",
                    "Tahajjud / Qiyam (Night Vigil)",
                    "Daily Fixed Quran Recitation",
                    "Unlimited custom wirds"
                ),
                isSelected = selectedTier == 3,
                onClick = { selectedTier = 3 }
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingLarge))

            Button(
                onClick = { onSelectTier(selectedTier) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.ButtonHeightPrimary)
                    .testTag("begin_journey_button"),
                shape = RoundedCornerShape(Dimens.CardCornerMedium),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Begin Daily Muhasabah",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingLarge))
        }
    }
}

@Composable
fun TierSelectionCard(
    tierNumber: Int,
    title: String,
    arabicSubtitle: String,
    description: String,
    habits: List<String>,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val extendedColors = MaterialTheme.extendedColors
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else extendedColors.sandBorder
    val bgColor = if (isSelected) extendedColors.sageContainer.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("tier_card_$tierNumber"),
        shape = OrganicCardShape,
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(if (isSelected) 2.dp else Dimens.BorderWidthThin, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.CardInnerPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = arabicSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = ArabicFontFamily,
                        color = extendedColors.terracottaGold,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.IconSizeMedium)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingExtraSmall))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

            habits.forEach { habit ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
                    Text(
                        text = habit,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

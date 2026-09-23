package com.example.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Digital Muhasabah Color System: Warm Organic / Cultural Palette.
 * Designed with high contrast (WCAG AA compliant), serene natural tones,
 * and a full Material 3 tonal elevation ramp.
 */

// --- 1. Core Herbal Olive Palette ---
val OlivePrimary = Color(0xFF4A5D23)
val OliveDeep = Color(0xFF2B3813)
val OliveLight = Color(0xFF6B8733)
val OliveSubtle = Color(0xFF7A993C)
val SageContainer = Color(0xFFE8EDD1)
val SageContainerBorder = Color(0xFFDAE2C0)

// --- 2. Terracotta, Cultural Amber & Celebration ---
// TerracottaPrimary & TerracottaGold are tuned to #986127 for WCAG AA (5.2:1 contrast with white text)
val TerracottaGold = Color(0xFF986127)
val TerracottaGoldDark = Color(0xFF7C4B19)
val TerracottaGoldLight = Color(0xFFC59B6D)
val GoldSubtle = Color(0xFFFAF3EB)

// Dedicated Level Up milestone & streak celebration accents
val CelebrationGold = Color(0xFFD48B28)
val CelebrationGlow = Color(0xFFFDF1DC)
val StreakFlame = Color(0xFFD96B27)
val StreakBg = Color(0xFFFDF2E7)
val StreakBorder = Color(0xFFF4D3B4)

// --- 3. Light Mode Surfaces & Material 3 Elevation Ramp ---
val WarmAlabaster = Color(0xFFFAF9F6)
val SandBackground = Color(0xFFFAF9F6)
val SandSurface = Color(0xFFFFFFFF)
val SandCard = Color(0xFFF7F5EE)
val SandCardAlt = Color(0xFFF1EFE8)
val SandBorder = Color(0xFFEAE4DB)
val SandBorderSubtle = Color(0xFFF2ECE4)

val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFFAF9F6)
val LightSurfaceContainer = Color(0xFFF4F2EB)
val LightSurfaceContainerHigh = Color(0xFFEEEBE2)
val LightSurfaceContainerHighest = Color(0xFFE7E3D8)

// --- 4. Dark Mode Surfaces & Material 3 Elevation Ramp ---
val DarkBackground = Color(0xFF141310)
val DarkSurface = Color(0xFF1C1B17)
val DarkCard = Color(0xFF24231E)
val DarkBorder = Color(0xFF3D3A32)
val DarkBorderSubtle = Color(0xFF2B2923)

val DarkSurfaceContainerLowest = Color(0xFF0F0E0C)
val DarkSurfaceContainerLow = Color(0xFF1E1D19)
val DarkSurfaceContainer = Color(0xFF24231E)
val DarkSurfaceContainerHigh = Color(0xFF2C2A24)
val DarkSurfaceContainerHighest = Color(0xFF35332C)

// --- 5. Typography & Text Hierarchy ---
val TextPrimary = Color(0xFF1C1B17)
val TextSecondary = Color(0xFF5C584E)
val TextMuted = Color(0xFF8C8A82)

val DarkTextPrimary = Color(0xFFF5F3EC)
val DarkTextSecondary = Color(0xFFB8B2A2)
val DarkTextMuted = Color(0xFF837D70)

// --- 6. Harmonious Chart & Balance Wheel Palette ---
val ChartPrayer = Color(0xFF4A5D23)      // Herbal Olive
val ChartSunnah = Color(0xFF788F3B)      // Gentle Sage
val ChartQuran = Color(0xFF986127)       // Terracotta Ochre
val ChartAdhkar = Color(0xFF3D6E77)      // Muted Celadon / Teal
val ChartCustom = Color(0xFF8C5A76)      // Dusty Plum Rose
val ChartBalancePalette = listOf(ChartPrayer, ChartSunnah, ChartQuran, ChartAdhkar, ChartCustom)

// --- 7. Status Colors (Three-State Prayer & Check-in) ---
val StatusOnTimeBg = OlivePrimary
val StatusOnTimeText = Color.White
val StatusOnTimeBorder = OlivePrimary

val StatusLateBg = Color(0xFFFDF4E7)
val StatusLateText = Color(0xFF986127)
val StatusLateBorder = Color(0xFFE0BA8C)

val StatusMissedBg = Color(0xFFF2EFE9)
val StatusMissedText = Color(0xFF7A766E)
val StatusMissedBorder = Color(0xFFD6D0C5)

// --- 8. Component-Specific Colors: Insight Engine Tones ---
// Tone A: Encouraging / Milestone / Level Up
val InsightEncouragingBg = Color(0xFFFBF4E7)
val InsightEncouragingBorder = Color(0xFFE8CB9D)
val InsightEncouragingAccent = Color(0xFF986127)
val InsightEncouragingText = Color(0xFF53330D)
val DarkInsightEncouragingBg = Color(0xFF292215)
val DarkInsightEncouragingBorder = Color(0xFF564324)

// Tone B: Gentle / Protective / Burnout Prevention
val InsightProtectiveBg = Color(0xFFF1F6EA)
val InsightProtectiveBorder = Color(0xFFCFDCB8)
val InsightProtectiveAccent = Color(0xFF4A5D23)
val InsightProtectiveText = Color(0xFF283611)
val DarkInsightProtectiveBg = Color(0xFF1D2617)
val DarkInsightProtectiveBorder = Color(0xFF394A27)

// Tone C: Informative / Analytical / Timing Bottleneck
val InsightInformativeBg = Color(0xFFEFF5F6)
val InsightInformativeBorder = Color(0xFFC3D8DC)
val InsightInformativeAccent = Color(0xFF366068)
val InsightInformativeText = Color(0xFF14343B)
val DarkInsightInformativeBg = Color(0xFF172427)
val DarkInsightInformativeBorder = Color(0xFF274349)

// --- 9. Toggle & Progress Component States ---
val ToggleIdleBg = WarmAlabaster
val ToggleIdleBorder = SandBorder
val ToggleIdleText = OlivePrimary

val ProgressRingTrackLight = SageContainer
val ProgressRingTrackDark = DarkSurfaceContainerHighest
val ProgressRingActive = OlivePrimary
val ProgressRingMilestone = CelebrationGold

/**
 * Centralized semantic color container for dynamic Light/Dark theme switching.
 */
data class MuhasabahExtendedColors(
    val statusOnTimeBg: Color,
    val statusOnTimeText: Color,
    val statusOnTimeBorder: Color,

    val statusLateBg: Color,
    val statusLateText: Color,
    val statusLateBorder: Color,

    val statusMissedBg: Color,
    val statusMissedText: Color,
    val statusMissedBorder: Color,

    val insightEncouragingBg: Color,
    val insightEncouragingBorder: Color,
    val insightEncouragingAccent: Color,
    val insightEncouragingText: Color,

    val insightProtectiveBg: Color,
    val insightProtectiveBorder: Color,
    val insightProtectiveAccent: Color,
    val insightProtectiveText: Color,

    val insightInformativeBg: Color,
    val insightInformativeBorder: Color,
    val insightInformativeAccent: Color,
    val insightInformativeText: Color,

    val insightNudgeBg: Color,
    val insightNudgeBorder: Color,
    val insightNudgeAccent: Color,
    val insightNudgeText: Color,

    val celebrationGold: Color,
    val celebrationGlow: Color,
    val streakFlame: Color,
    val streakBg: Color,
    val streakBorder: Color,

    val terracottaGold: Color,
    val goldSubtle: Color,

    val chartPrayer: Color,
    val chartSunnah: Color,
    val chartQuran: Color,
    val chartAdhkar: Color,
    val chartCustom: Color,
    val chartBalancePalette: List<Color>,

    val toggleIdleBg: Color,
    val toggleIdleBorder: Color,
    val toggleIdleText: Color,

    val progressRingTrack: Color,
    val progressRingActive: Color,
    val progressRingMilestone: Color,

    val sageContainer: Color,
    val sageContainerBorder: Color,
    val sandCard: Color,
    val sandBorder: Color,
    val sandSurface: Color,
    val chartTrack: Color,
    val oliveLight: Color,
    val heroBannerBg: Color,
    val heroBannerBorder: Color,
    val heroBannerGold: Color
)

val MuhasabahLightExtendedColors = MuhasabahExtendedColors(
    statusOnTimeBg = OlivePrimary,
    statusOnTimeText = Color.White,
    statusOnTimeBorder = OlivePrimary,

    statusLateBg = Color(0xFFFDF4E7),
    statusLateText = Color(0xFF986127),
    statusLateBorder = Color(0xFFE0BA8C),

    statusMissedBg = Color(0xFFF2EFE9),
    statusMissedText = Color(0xFF7A766E),
    statusMissedBorder = Color(0xFFD6D0C5),

    insightEncouragingBg = InsightEncouragingBg,
    insightEncouragingBorder = InsightEncouragingBorder,
    insightEncouragingAccent = InsightEncouragingAccent,
    insightEncouragingText = InsightEncouragingText,

    insightProtectiveBg = InsightProtectiveBg,
    insightProtectiveBorder = InsightProtectiveBorder,
    insightProtectiveAccent = InsightProtectiveAccent,
    insightProtectiveText = InsightProtectiveText,

    insightInformativeBg = InsightInformativeBg,
    insightInformativeBorder = InsightInformativeBorder,
    insightInformativeAccent = InsightInformativeAccent,
    insightInformativeText = InsightInformativeText,

    insightNudgeBg = Color(0xFFF4F6EC),
    insightNudgeBorder = SageContainerBorder,
    insightNudgeAccent = OlivePrimary,
    insightNudgeText = TextPrimary,

    celebrationGold = CelebrationGold,
    celebrationGlow = CelebrationGlow,
    streakFlame = StreakFlame,
    streakBg = StreakBg,
    streakBorder = StreakBorder,

    terracottaGold = TerracottaGold,
    goldSubtle = GoldSubtle,

    chartPrayer = ChartPrayer,
    chartSunnah = ChartSunnah,
    chartQuran = ChartQuran,
    chartAdhkar = ChartAdhkar,
    chartCustom = ChartCustom,
    chartBalancePalette = ChartBalancePalette,

    toggleIdleBg = WarmAlabaster,
    toggleIdleBorder = SandBorder,
    toggleIdleText = OlivePrimary,

    progressRingTrack = SageContainer,
    progressRingActive = OlivePrimary,
    progressRingMilestone = CelebrationGold,

    sageContainer = SageContainer,
    sageContainerBorder = SageContainerBorder,
    sandCard = SandCard,
    sandBorder = SandBorder,
    sandSurface = SandSurface,
    chartTrack = SageContainer.copy(alpha = 0.5f),
    oliveLight = OliveLight,
    heroBannerBg = OliveDeep,
    heroBannerBorder = TerracottaGold.copy(alpha = 0.4f),
    heroBannerGold = CelebrationGold
)

val MuhasabahDarkExtendedColors = MuhasabahExtendedColors(
    statusOnTimeBg = OliveLight,
    statusOnTimeText = DarkBackground,
    statusOnTimeBorder = OliveLight,

    statusLateBg = Color(0xFF332415),
    statusLateText = Color(0xFFE4AA6B),
    statusLateBorder = Color(0xFF6E4D25),

    statusMissedBg = Color(0xFF262420),
    statusMissedText = Color(0xFF9E9A90),
    statusMissedBorder = Color(0xFF423F38),

    insightEncouragingBg = DarkInsightEncouragingBg,
    insightEncouragingBorder = DarkInsightEncouragingBorder,
    insightEncouragingAccent = CelebrationGold,
    insightEncouragingText = DarkTextPrimary,

    insightProtectiveBg = DarkInsightProtectiveBg,
    insightProtectiveBorder = DarkInsightProtectiveBorder,
    insightProtectiveAccent = OliveLight,
    insightProtectiveText = DarkTextPrimary,

    insightInformativeBg = DarkInsightInformativeBg,
    insightInformativeBorder = DarkInsightInformativeBorder,
    insightInformativeAccent = Color(0xFF5B929D),
    insightInformativeText = DarkTextPrimary,

    insightNudgeBg = DarkSurfaceContainerHigh,
    insightNudgeBorder = DarkBorder,
    insightNudgeAccent = OliveLight,
    insightNudgeText = DarkTextPrimary,

    celebrationGold = CelebrationGold,
    celebrationGlow = Color(0xFF3B2E17),
    streakFlame = Color(0xFFE57B36),
    streakBg = Color(0xFF332014),
    streakBorder = Color(0xFF6B4323),

    terracottaGold = Color(0xFFD9964A),
    goldSubtle = Color(0xFF2E2419),

    chartPrayer = Color(0xFF8BAA4E),
    chartSunnah = Color(0xFFA5BF5C),
    chartQuran = Color(0xFFD9964A),
    chartAdhkar = Color(0xFF5BA4B0),
    chartCustom = Color(0xFFC07F9E),
    chartBalancePalette = listOf(
        Color(0xFF8BAA4E),
        Color(0xFFA5BF5C),
        Color(0xFFD9964A),
        Color(0xFF5BA4B0),
        Color(0xFFC07F9E)
    ),

    toggleIdleBg = DarkSurfaceContainer,
    toggleIdleBorder = DarkBorder,
    toggleIdleText = DarkTextSecondary,

    progressRingTrack = DarkSurfaceContainerHighest,
    progressRingActive = OliveLight,
    progressRingMilestone = CelebrationGold,

    sageContainer = OliveDeep,
    sageContainerBorder = Color(0xFF3E4F1E),
    sandCard = DarkCard,
    sandBorder = DarkBorder,
    sandSurface = DarkSurface,
    chartTrack = DarkSurfaceContainerHighest,
    oliveLight = OliveLight,
    heroBannerBg = DarkCard,
    heroBannerBorder = DarkBorder,
    heroBannerGold = CelebrationGold
)

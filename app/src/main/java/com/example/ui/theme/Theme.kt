package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LocalMuhasabahColors = staticCompositionLocalOf<MuhasabahExtendedColors> {
    error("No MuhasabahExtendedColors provided")
}

/**
 * Access the semantic extended colors via MaterialTheme.extendedColors or MuhasabahTheme.extendedColors.
 */
val MaterialTheme.extendedColors: MuhasabahExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalMuhasabahColors.current

object MuhasabahTheme {
    val extendedColors: MuhasabahExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalMuhasabahColors.current
}

private val MuhasabahDarkColorScheme = darkColorScheme(
    primary = OliveLight,
    onPrimary = DarkBackground,
    primaryContainer = OliveDeep,
    onPrimaryContainer = SageContainer,
    secondary = TerracottaGold,
    onSecondary = Color.White,
    secondaryContainer = DarkSurfaceContainerHigh,
    onSecondaryContainer = GoldSubtle,
    tertiary = CelebrationGold,
    onTertiary = DarkBackground,
    tertiaryContainer = DarkInsightEncouragingBg,
    onTertiaryContainer = CelebrationGlow,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceContainer,
    onSurfaceVariant = DarkTextSecondary,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    surfaceTint = OliveLight,
    outline = DarkBorder,
    outlineVariant = DarkBorderSubtle
)

private val MuhasabahLightColorScheme = lightColorScheme(
    primary = OlivePrimary,
    onPrimary = Color.White,
    primaryContainer = SageContainer,
    onPrimaryContainer = OliveDeep,
    secondary = TerracottaGold,
    onSecondary = Color.White,
    secondaryContainer = GoldSubtle,
    onSecondaryContainer = TextPrimary,
    tertiary = CelebrationGold,
    onTertiary = Color.White,
    tertiaryContainer = CelebrationGlow,
    onTertiaryContainer = TerracottaGoldDark,
    background = WarmAlabaster,
    onBackground = TextPrimary,
    surface = SandSurface,
    onSurface = TextPrimary,
    surfaceVariant = SandCard,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLowest = LightSurfaceContainerLowest,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest,
    surfaceTint = OlivePrimary,
    outline = SandBorder,
    outlineVariant = SandBorderSubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep intentional serene spiritual theme by default
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> MuhasabahDarkColorScheme
        else -> MuhasabahLightColorScheme
    }

    val extendedColors = if (darkTheme) MuhasabahDarkExtendedColors else MuhasabahLightExtendedColors

    CompositionLocalProvider(
        LocalMuhasabahColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

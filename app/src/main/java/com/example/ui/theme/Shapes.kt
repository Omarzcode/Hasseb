package com.example.ui.theme

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Organic Material 3 Shapes reflecting a warm, handcrafted, and contemplative visual aesthetic.
 * Soft curves, generous corner radii, and subtle organic asymmetry reinforce serenity.
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// Organic Component-Specific Shape Tokens
val OrganicPillShape = RoundedCornerShape(percent = 50)
val OrganicCardShape = RoundedCornerShape(24.dp)
val OrganicCardCompactShape = RoundedCornerShape(16.dp)
val OrganicContainerShape = RoundedCornerShape(28.dp)
val OrganicHeroShape = RoundedCornerShape(32.dp)

// Gentle Asymmetrical Shapes for organic cultural parchment feeling
val OrganicAsymmetricCardShape = RoundedCornerShape(
    topStart = CornerSize(26.dp),
    topEnd = CornerSize(18.dp),
    bottomEnd = CornerSize(26.dp),
    bottomStart = CornerSize(18.dp)
)

val OrganicDialogShape = RoundedCornerShape(28.dp)
val OrganicBadgeShape = RoundedCornerShape(8.dp)
val OrganicToggleShape = RoundedCornerShape(12.dp)

package com.example.ui.screens.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.domain.engine.RadarAxisData
import com.example.ui.theme.Dimens
import com.example.ui.theme.extendedColors
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarBalanceWheel(
    axes: List<RadarAxisData>,
    modifier: Modifier = Modifier
) {
    val extendedColors = MaterialTheme.extendedColors
    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurface
    val arabicTextColor = extendedColors.terracottaGold
    val sandBorderColor = extendedColors.sandBorder
    val sageContainerColor = extendedColors.sageContainer
    val palette = extendedColors.chartBalancePalette

    Box(
        modifier = modifier
            .height(Dimens.RadarChartHeight)
            .padding(Dimens.SpacingMedium)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val count = axes.size.coerceAtLeast(3)
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (size.minDimension / 2f) - 38.dp.toPx()

            val angleStep = (2 * Math.PI / count).toFloat()
            val startAngle = -Math.PI / 2f // Top center

            // 1. Draw Concentric Grid Polygons (25%, 50%, 75%, 100%)
            val gridLevels = listOf(0.25f, 0.5f, 0.75f, 1.0f)
            for (level in gridLevels) {
                val gridPath = Path()
                for (i in 0 until count) {
                    val angle = startAngle + i * angleStep
                    val r = radius * level
                    val x = center.x + r * cos(angle).toFloat()
                    val y = center.y + r * sin(angle).toFloat()
                    if (i == 0) gridPath.moveTo(x, y) else gridPath.lineTo(x, y)
                }
                gridPath.close()
                drawPath(
                    path = gridPath,
                    color = sandBorderColor.copy(alpha = 0.8f),
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // 2. Draw Spokes from Center
            for (i in 0 until count) {
                val angle = startAngle + i * angleStep
                val endX = center.x + radius * cos(angle).toFloat()
                val endY = center.y + radius * sin(angle).toFloat()
                val spokeColor = palette.getOrElse(i % palette.size) { primaryColor }.copy(alpha = 0.35f)
                drawLine(
                    color = spokeColor,
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // 3. Draw Data Polygon
            val dataPath = Path()
            val pointList = mutableListOf<Offset>()

            for (i in 0 until count) {
                val angle = startAngle + i * angleStep
                val axisValue = axes.getOrNull(i)?.value?.coerceIn(0.1f, 1f) ?: 0.5f
                val r = radius * axisValue
                val x = center.x + r * cos(angle).toFloat()
                val y = center.y + r * sin(angle).toFloat()
                val pt = Offset(x, y)
                pointList.add(pt)
                if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
            }
            dataPath.close()

            // Fill Data Area with serene organic wash
            drawPath(
                path = dataPath,
                color = sageContainerColor.copy(alpha = 0.6f),
                style = Fill
            )

            // Draw Data Perimeter Outline
            drawPath(
                path = dataPath,
                color = primaryColor,
                style = Stroke(width = 2.5.dp.toPx())
            )

            // 4. Draw Vertex Points with harmonious category chart accents
            for ((index, pt) in pointList.withIndex()) {
                val vertexColor = palette.getOrElse(index % palette.size) { primaryColor }
                drawCircle(
                    color = vertexColor,
                    radius = 5.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.5.dp.toPx(),
                    center = pt
                )
            }

            // 5. Draw Labels
            val textPaint = android.graphics.Paint().apply {
                color = textColor.toArgb()
                textSize = 10.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val arabicPaint = android.graphics.Paint().apply {
                color = arabicTextColor.toArgb()
                textSize = 9.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }

            for (i in 0 until count) {
                val angle = startAngle + i * angleStep
                val labelDistance = radius + 22.dp.toPx()
                val lx = center.x + labelDistance * cos(angle).toFloat()
                val ly = center.y + labelDistance * sin(angle).toFloat()

                val axisData = axes.getOrNull(i)
                if (axisData != null) {
                    drawContext.canvas.nativeCanvas.drawText(
                        axisData.label,
                        lx,
                        ly,
                        textPaint
                    )
                    drawContext.canvas.nativeCanvas.drawText(
                        axisData.arabicLabel,
                        lx,
                        ly + 11.dp.toPx(),
                        arabicPaint
                    )
                }
            }
        }
    }
}

package com.example

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun BeerGlassAnimation(isFilled: Boolean, modifier: Modifier = Modifier) {
    val fillAmount by animateFloatAsState(
        targetValue = if (isFilled) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "fillAnimation"
    )

    Canvas(modifier = modifier.size(100.dp)) {
        val glassTopWidth = size.width * 0.62f
        val glassBottomWidth = size.width * 0.42f
        val glassHeight = size.height * 0.78f
        val left = (size.width - glassTopWidth) / 2f
        val right = left + glassTopWidth
        val bottom = size.height * 0.90f
        val top = bottom - glassHeight
        val taper = (glassTopWidth - glassBottomWidth) / 2f

        // Glass boundary path
        val glassPath = Path().apply {
            moveTo(left, top)
            lineTo(right, top)
            lineTo(right - taper, bottom)
            lineTo(left + taper, bottom)
            close()
        }

        // Draw beer liquid if filled
        if (fillAmount > 0.01f) {
            val maxBeerLevel = top + glassHeight * 0.12f
            val beerLevel = bottom - (bottom - maxBeerLevel) * fillAmount
            val tLevel = ((beerLevel - top) / glassHeight).coerceIn(0f, 1f)
            val currentLeft = left + taper * tLevel + 4f
            val currentRight = right - taper * tLevel - 4f

            val beerPath = Path().apply {
                moveTo(currentLeft, beerLevel)
                lineTo(currentRight, beerLevel)
                lineTo(right - taper - 4f, bottom - 4f)
                lineTo(left + taper + 4f, bottom - 4f)
                close()
            }
            // Golden amber beer
            drawPath(path = beerPath, color = Color(0xFFFFB300))

            // Rising bubbles in the beer
            if (fillAmount > 0.3f) {
                val bubbleColor = Color(0xFFFFF176).copy(alpha = 0.8f * fillAmount)
                val bubbleRadius = size.width * 0.022f
                drawCircle(color = bubbleColor, radius = bubbleRadius * 1.2f, center = Offset(left + glassTopWidth * 0.35f, bottom - glassHeight * 0.25f * fillAmount))
                drawCircle(color = bubbleColor, radius = bubbleRadius * 0.9f, center = Offset(left + glassTopWidth * 0.65f, bottom - glassHeight * 0.42f * fillAmount))
                drawCircle(color = bubbleColor, radius = bubbleRadius * 1.4f, center = Offset(left + glassTopWidth * 0.48f, bottom - glassHeight * 0.58f * fillAmount))
                drawCircle(color = bubbleColor, radius = bubbleRadius * 0.8f, center = Offset(left + glassTopWidth * 0.25f, bottom - glassHeight * 0.68f * fillAmount))
                drawCircle(color = bubbleColor, radius = bubbleRadius * 1.1f, center = Offset(left + glassTopWidth * 0.72f, bottom - glassHeight * 0.75f * fillAmount))
            }

            // Curly fluffy beer foam on top - strictly kept inside left and right glass bounds
            val foamThickness = glassHeight * 0.16f * fillAmount
            val foamTop = beerLevel - foamThickness * 0.5f
            val foamWidth = currentRight - currentLeft

            // Cloud-like curly puffs strictly inside [0..1] range so it doesn't spill out to the sides
            // cx - r >= currentLeft and cx + r <= currentRight
            val outerPuffs = listOf(
                Triple(0.12f, 0.22f, 0.12f), // touches currentLeft (0.12 - 0.12 = 0)
                Triple(0.30f, 0.00f, 0.17f),
                Triple(0.50f, -0.20f, 0.22f), // high central fluffy crest
                Triple(0.70f, 0.00f, 0.17f),
                Triple(0.88f, 0.22f, 0.12f)  // touches currentRight (0.88 + 0.12 = 1.0)
            )

            val fillerPuffs = listOf(
                Triple(0.32f, 0.30f, 0.15f),
                Triple(0.50f, 0.22f, 0.17f),
                Triple(0.68f, 0.30f, 0.15f)
            )

            // 1. Base foam body (strictly inside glass walls)
            val foamBasePath = Path().apply {
                moveTo(currentLeft, beerLevel)
                lineTo(currentRight, beerLevel)
                lineTo(currentRight, foamTop + foamThickness * 0.3f)
                lineTo(currentLeft, foamTop + foamThickness * 0.3f)
                close()
            }
            drawPath(path = foamBasePath, color = Color(0xFFFFF9C4)) // Creamy base foam

            // 2. Curly foam puffs (Filled white clouds)
            (fillerPuffs + outerPuffs).forEach { (xRatio, yFactor, rFactor) ->
                val cx = currentLeft + foamWidth * xRatio
                val cy = foamTop + foamThickness * yFactor
                val r = foamWidth * rFactor
                drawCircle(
                    color = Color.White,
                    radius = r,
                    center = Offset(cx, cy)
                )
            }

            // 3. Crisp outline on the curly puffs so they stand out clearly
            val foamOutlineColor = Color(0xFF6D4C41).copy(alpha = 0.65f)
            outerPuffs.forEach { (xRatio, yFactor, rFactor) ->
                val cx = currentLeft + foamWidth * xRatio
                val cy = foamTop + foamThickness * yFactor
                val r = foamWidth * rFactor
                drawCircle(
                    color = foamOutlineColor,
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = 3f)
                )
            }

            // Redraw inner white circles slightly smaller to keep outer curly contour crisp
            outerPuffs.forEach { (xRatio, yFactor, rFactor) ->
                val cx = currentLeft + foamWidth * xRatio
                val cy = foamTop + foamThickness * yFactor
                val r = foamWidth * rFactor - 1.5f
                drawCircle(
                    color = Color.White,
                    radius = r,
                    center = Offset(cx, cy)
                )
            }

            // 4. Little decorative foam curls (frothy arcs ⌒) inside the foam
            val curlColor = Color(0xFFE0E0E0)
            val innerCurls = listOf(
                Offset(currentLeft + foamWidth * 0.30f, foamTop + foamThickness * 0.20f),
                Offset(currentLeft + foamWidth * 0.50f, foamTop + foamThickness * 0.06f),
                Offset(currentLeft + foamWidth * 0.70f, foamTop + foamThickness * 0.20f)
            )
            innerCurls.forEach { pos ->
                val arcSize = foamWidth * 0.11f
                drawArc(
                    color = curlColor,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(pos.x - arcSize / 2f, pos.y - arcSize / 4f),
                    size = Size(arcSize, arcSize / 2f),
                    style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                )
            }
        }

        // 5. Draw glass outline (drawn on top of the liquid with stylish thick base)
        drawPath(path = glassPath, color = Color(0xFF78909C), style = Stroke(width = 7f))

        // Glass bottom weighted rim
        drawLine(
            color = Color(0xFF546E7A),
            start = Offset(left + taper, bottom),
            end = Offset(right - taper, bottom),
            strokeWidth = 10f,
            cap = StrokeCap.Round
        )
    }
}

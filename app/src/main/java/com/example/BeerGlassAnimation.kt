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
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp

@Composable
fun BeerGlassAnimation(
    isFilled: Boolean,
    modifier: Modifier = Modifier,
    glassType: BeerGlassType = BeerGlassType.PINT
) {
    val fillAmount by animateFloatAsState(
        targetValue = if (isFilled) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "fillAnimation"
    )

    Canvas(modifier = modifier.size(110.dp)) {
        val w = size.width
        val h = size.height

        // Determine geometry based on selected glass type
        var topY = h * 0.15f
        var bottomY = h * 0.88f
        var topOpeningLeft = w * 0.20f
        var topOpeningRight = w * 0.80f

        val glassPath = Path()
        val extraDecorations: () -> Unit

        when (glassType) {
            BeerGlassType.PINT -> {
                topY = h * 0.14f
                bottomY = h * 0.88f
                topOpeningLeft = w * 0.20f
                topOpeningRight = w * 0.80f
                val taper = w * 0.09f

                glassPath.apply {
                    moveTo(topOpeningLeft, topY)
                    lineTo(topOpeningRight, topY)
                    lineTo(topOpeningRight - taper, bottomY)
                    lineTo(topOpeningLeft + taper, bottomY)
                    close()
                }

                extraDecorations = {
                    drawLine(
                        color = Color(0xFF546E7A),
                        start = Offset(topOpeningLeft + taper, bottomY),
                        end = Offset(topOpeningRight - taper, bottomY),
                        strokeWidth = 9f,
                        cap = StrokeCap.Round
                    )
                }
            }

            BeerGlassType.MUG -> {
                topY = h * 0.15f
                bottomY = h * 0.88f
                topOpeningLeft = w * 0.18f
                topOpeningRight = w * 0.68f

                glassPath.apply {
                    moveTo(topOpeningLeft, topY)
                    lineTo(topOpeningRight, topY)
                    lineTo(topOpeningRight, bottomY)
                    lineTo(topOpeningLeft, bottomY)
                    close()
                }

                val handlePath = Path().apply {
                    moveTo(topOpeningRight, h * 0.28f)
                    cubicTo(w * 0.94f, h * 0.28f, w * 0.94f, h * 0.74f, topOpeningRight, h * 0.74f)
                }

                extraDecorations = {
                    // Sturdy handle on the right
                    drawPath(
                        path = handlePath,
                        color = Color(0xFF78909C),
                        style = Stroke(width = 8f, cap = StrokeCap.Round)
                    )
                    // Thick bottom base
                    drawLine(
                        color = Color(0xFF546E7A),
                        start = Offset(topOpeningLeft, bottomY),
                        end = Offset(topOpeningRight, bottomY),
                        strokeWidth = 10f,
                        cap = StrokeCap.Round
                    )
                }
            }

            BeerGlassType.WEIZEN -> {
                topY = h * 0.12f
                bottomY = h * 0.88f
                topOpeningLeft = w * 0.24f
                topOpeningRight = w * 0.76f

                glassPath.apply {
                    moveTo(topOpeningLeft, topY)
                    lineTo(topOpeningRight, topY)
                    // Curvy silhouette: bulbous upper cup, slender waist, flared pedestal
                    cubicTo(w * 0.73f, h * 0.36f, w * 0.57f, h * 0.64f, w * 0.65f, bottomY)
                    lineTo(w * 0.35f, bottomY)
                    cubicTo(w * 0.43f, h * 0.64f, w * 0.27f, h * 0.36f, topOpeningLeft, topY)
                    close()
                }

                extraDecorations = {
                    drawLine(
                        color = Color(0xFF546E7A),
                        start = Offset(w * 0.33f, bottomY),
                        end = Offset(w * 0.67f, bottomY),
                        strokeWidth = 9f,
                        cap = StrokeCap.Round
                    )
                }
            }

            BeerGlassType.TULIP -> {
                topY = h * 0.15f
                val bowlBottom = h * 0.68f
                bottomY = bowlBottom
                topOpeningLeft = w * 0.26f
                topOpeningRight = w * 0.74f

                glassPath.apply {
                    moveTo(topOpeningLeft, topY)
                    lineTo(topOpeningRight, topY)
                    // Tulip curves: lip flare, slight waist, rounded bowl
                    cubicTo(w * 0.69f, h * 0.28f, w * 0.82f, h * 0.50f, w * 0.54f, bowlBottom)
                    lineTo(w * 0.46f, bowlBottom)
                    cubicTo(w * 0.18f, h * 0.50f, w * 0.31f, h * 0.28f, topOpeningLeft, topY)
                    close()
                }

                extraDecorations = {
                    val stemTop = bowlBottom
                    val stemBottom = h * 0.86f
                    // Stem
                    drawLine(
                        color = Color(0xFF78909C),
                        start = Offset(w * 0.50f, stemTop),
                        end = Offset(w * 0.50f, stemBottom),
                        strokeWidth = 7f,
                        cap = StrokeCap.Round
                    )
                    // Foot base
                    drawLine(
                        color = Color(0xFF546E7A),
                        start = Offset(w * 0.28f, stemBottom + 2f),
                        end = Offset(w * 0.72f, stemBottom + 2f),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                    )
                }
            }

            BeerGlassType.BOOT -> {
                topY = h * 0.14f
                bottomY = h * 0.88f
                topOpeningLeft = w * 0.24f
                topOpeningRight = w * 0.62f

                glassPath.apply {
                    moveTo(topOpeningLeft, topY)
                    lineTo(topOpeningRight, topY)
                    lineTo(topOpeningRight, h * 0.56f)
                    // Instep curve towards boot toe
                    cubicTo(w * 0.64f, h * 0.68f, w * 0.88f, h * 0.70f, w * 0.88f, bottomY)
                    // Sole of boot
                    lineTo(w * 0.22f, bottomY)
                    // Heel curve back up to shaft
                    cubicTo(w * 0.22f, h * 0.74f, w * 0.24f, h * 0.60f, topOpeningLeft, topY)
                    close()
                }

                extraDecorations = {
                    // Heel accent
                    drawLine(
                        color = Color(0xFF546E7A),
                        start = Offset(w * 0.22f, bottomY),
                        end = Offset(w * 0.42f, bottomY),
                        strokeWidth = 10f,
                        cap = StrokeCap.Round
                    )
                    // Sole accent
                    drawLine(
                        color = Color(0xFF546E7A),
                        start = Offset(w * 0.42f, bottomY),
                        end = Offset(w * 0.88f, bottomY),
                        strokeWidth = 7f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // Draw Beer Liquid and Foam when filled
        if (fillAmount > 0.01f) {
            val totalHeight = bottomY - topY
            val targetLevel = topY + totalHeight * 0.10f
            val beerLevel = bottomY - (bottomY - targetLevel) * fillAmount

            // 1. Clip liquid & bubbles strictly to the glass shape
            clipPath(glassPath) {
                // Liquid rectangle filled with rich amber beer
                drawRect(
                    color = Color(0xFFFFB300),
                    topLeft = Offset(0f, beerLevel),
                    size = Size(w, h - beerLevel + 30f)
                )

                // Bubbles rising inside the beer
                if (fillAmount > 0.3f) {
                    val bubbleColor = Color(0xFFFFF176).copy(alpha = 0.8f * fillAmount)
                    val bubbleRadius = w * 0.022f
                    val midX = (topOpeningLeft + topOpeningRight) / 2f
                    drawCircle(color = bubbleColor, radius = bubbleRadius * 1.2f, center = Offset(midX - w * 0.10f, bottomY - totalHeight * 0.25f * fillAmount))
                    drawCircle(color = bubbleColor, radius = bubbleRadius * 0.9f, center = Offset(midX + w * 0.12f, bottomY - totalHeight * 0.42f * fillAmount))
                    drawCircle(color = bubbleColor, radius = bubbleRadius * 1.4f, center = Offset(midX - w * 0.02f, bottomY - totalHeight * 0.58f * fillAmount))
                    drawCircle(color = bubbleColor, radius = bubbleRadius * 0.8f, center = Offset(midX - w * 0.14f, bottomY - totalHeight * 0.68f * fillAmount))
                    drawCircle(color = bubbleColor, radius = bubbleRadius * 1.1f, center = Offset(midX + w * 0.08f, bottomY - totalHeight * 0.75f * fillAmount))
                }

                // Creamy base foam inside the glass boundary
                val baseFoamHeight = totalHeight * 0.14f * fillAmount
                drawRect(
                    color = Color(0xFFFFF9C4),
                    topLeft = Offset(0f, beerLevel - baseFoamHeight * 0.2f),
                    size = Size(w, baseFoamHeight + 4f)
                )
            }

            // 2. Curly fluffy foam puffs crowning the top opening
            val foamWidth = topOpeningRight - topOpeningLeft
            val foamThickness = totalHeight * 0.16f * fillAmount
            val foamTop = beerLevel - foamThickness * 0.4f

            val outerPuffs = listOf(
                Triple(0.12f, 0.20f, 0.12f),
                Triple(0.30f, -0.02f, 0.17f),
                Triple(0.50f, -0.20f, 0.21f), // high central fluffy crest
                Triple(0.70f, -0.02f, 0.17f),
                Triple(0.88f, 0.20f, 0.12f)
            )

            val fillerPuffs = listOf(
                Triple(0.32f, 0.26f, 0.15f),
                Triple(0.50f, 0.20f, 0.17f),
                Triple(0.68f, 0.26f, 0.15f)
            )

            // Fill all puffs with white
            (fillerPuffs + outerPuffs).forEach { (xRatio, yFactor, rFactor) ->
                val cx = topOpeningLeft + foamWidth * xRatio
                val cy = foamTop + foamThickness * yFactor
                val r = foamWidth * rFactor
                drawCircle(
                    color = Color.White,
                    radius = r,
                    center = Offset(cx, cy)
                )
            }

            // Crisp outline on the outer curly puffs
            val foamOutlineColor = Color(0xFF6D4C41).copy(alpha = 0.65f)
            outerPuffs.forEach { (xRatio, yFactor, rFactor) ->
                val cx = topOpeningLeft + foamWidth * xRatio
                val cy = foamTop + foamThickness * yFactor
                val r = foamWidth * rFactor
                drawCircle(
                    color = foamOutlineColor,
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = 3f)
                )
            }

            // Inner fill cleanup
            outerPuffs.forEach { (xRatio, yFactor, rFactor) ->
                val cx = topOpeningLeft + foamWidth * xRatio
                val cy = foamTop + foamThickness * yFactor
                val r = foamWidth * rFactor - 1.5f
                drawCircle(
                    color = Color.White,
                    radius = r,
                    center = Offset(cx, cy)
                )
            }

            // Decorative inner froth arcs ⌒
            val curlColor = Color(0xFFE0E0E0)
            val innerCurls = listOf(
                Offset(topOpeningLeft + foamWidth * 0.30f, foamTop + foamThickness * 0.20f),
                Offset(topOpeningLeft + foamWidth * 0.50f, foamTop + foamThickness * 0.06f),
                Offset(topOpeningLeft + foamWidth * 0.70f, foamTop + foamThickness * 0.20f)
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

        // 3. Draw glass outline & decorations
        drawPath(
            path = glassPath,
            color = Color(0xFF78909C),
            style = Stroke(width = 7f, cap = StrokeCap.Round)
        )

        // Draw shape-specific additions (handle for mug, stem for tulip, etc.)
        extraDecorations()
    }
}

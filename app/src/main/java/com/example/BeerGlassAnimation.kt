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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun BeerGlassAnimation(isFilled: Boolean, modifier: Modifier = Modifier) {
    val fillAmount by animateFloatAsState(
        targetValue = if (isFilled) 1f else 0f,
        animationSpec = tween(durationMillis = 1000), label = "fillAnimation"
    )

    Canvas(modifier = modifier.size(100.dp)) {
        val glassTopWidth = size.width * 0.6f
        val glassBottomWidth = size.width * 0.4f
        val glassHeight = size.height * 0.8f
        val left = (size.width - glassTopWidth) / 2
        val bottom = size.height * 0.9f
        val top = bottom - glassHeight

        // Draw glass shape
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(left, top)
            lineTo(left + glassTopWidth, top)
            lineTo(left + glassTopWidth - (glassTopWidth - glassBottomWidth) / 2, bottom)
            lineTo(left + (glassTopWidth - glassBottomWidth) / 2, bottom)
            close()
        }
        drawPath(path = path, color = Color.Gray, style = Stroke(width = 8f))

        // Draw beer
        val beerTop = top + glassHeight * (1 - fillAmount)
        val beerPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(left + 4f, beerTop)
            lineTo(left + glassTopWidth - 4f, beerTop)
            lineTo(left + glassTopWidth - 4f - (glassTopWidth - glassBottomWidth) / 2, bottom - 4f)
            lineTo(left + 4f + (glassTopWidth - glassBottomWidth) / 2, bottom - 4f)
            close()
        }
        drawPath(path = beerPath, color = Color(0xFFFFC107))
    }
}

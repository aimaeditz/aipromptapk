package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * High-performance, lightweight subtle glow modifier.
 * Uses drawBehind for zero recomposition overhead and no expensive GPU blur layers.
 */
fun Modifier.subtleGlow(
    color: Color,
    radius: Dp = 12.dp,
    alpha: Float = 0.22f,
    cornerRadius: Dp = 16.dp
): Modifier = this.then(
    Modifier.drawBehind {
        val radiusPx = radius.toPx()
        val cornerPx = cornerRadius.toPx()

        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = alpha),
                    color.copy(alpha = alpha * 0.4f),
                    Color.Transparent
                ),
                center = Offset(size.width / 2f, size.height / 2f),
                radius = (size.width.coerceAtLeast(size.height) / 2f) + radiusPx
            ),
            size = Size(size.width + radiusPx * 1.5f, size.height + radiusPx * 1.5f),
            topLeft = Offset(-radiusPx * 0.75f, -radiusPx * 0.75f),
            cornerRadius = CornerRadius(cornerPx + radiusPx, cornerPx + radiusPx)
        )
    }
)

/**
 * Fast micro-interaction press effect (~120ms spring).
 * Gives instant high-end tactile feedback on tap.
 */
fun Modifier.premiumPressEffect(
    pressedScale: Float = 0.96f,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "pressScale"
    )

    this
        .scale(scale)
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
            } else Modifier
        )
}

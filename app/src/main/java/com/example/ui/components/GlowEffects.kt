package com.example.ui.components
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlowBlueLight
import com.example.ui.theme.GlowCyanLight
import com.example.ui.theme.GlowVioletLight

/**
 * SaaS-grade dual-layer drop shadow for white mode:
 * 1. Tight contact shadow (2-8px) for definition
 * 2. Wide soft ambient shadow (20-40px) for floating physical depth
 */
fun Modifier.saasElevatedShadow(
    cornerRadius: Dp = 14.dp,
    elevationLevel: Int = 1 // 1: standard card, 2: floating modal/bar
): Modifier = this.then(
    Modifier.drawBehind {
        val cornerPx = cornerRadius.toPx()
        val tightAlpha = if (elevationLevel == 2) 0.08f else 0.05f
        val wideAlpha = if (elevationLevel == 2) 0.10f else 0.06f

        // 1. Tight contact shadow
        drawRoundRect(
            color = Color(0x0F141432).copy(alpha = tightAlpha),
            topLeft = Offset(0f, 2.dp.toPx()),
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(cornerPx, cornerPx)
        )

        // 2. Wide ambient soft shadow
        val spread = if (elevationLevel == 2) 12.dp.toPx() else 8.dp.toPx()
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x141E3A8A).copy(alpha = wideAlpha),
                    Color(0x081E3A8A).copy(alpha = wideAlpha * 0.4f),
                    Color.Transparent
                ),
                center = Offset(size.width / 2f, size.height / 2f + (spread / 2)),
                radius = (size.width.coerceAtLeast(size.height) / 2f) + spread
            ),
            topLeft = Offset(-spread * 0.5f, -spread * 0.2f),
            size = Size(size.width + spread, size.height + spread),
            cornerRadius = CornerRadius(cornerPx + spread, cornerPx + spread)
        )
    }
)

/**
 * Fullscreen / Section SaaS ambient radial-glow background mesh.
 * Renders 2-4 organic multi-stop glow blobs behind content in Light Mode.
 */
fun Modifier.saasBackgroundGlow(
    isLightMode: Boolean = true
): Modifier = this.then(
    Modifier.drawBehind {
        if (isLightMode) {
            // Blob 1: Top-Left Cyan/Sky Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        GlowCyanLight.copy(alpha = 0.25f),
                        GlowCyanLight.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.15f, size.height * 0.08f),
                    radius = size.width * 0.55f
                ),
                radius = size.width * 0.55f,
                center = Offset(size.width * 0.15f, size.height * 0.08f)
            )

            // Blob 2: Mid-Right Violet/Indigo Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        GlowVioletLight.copy(alpha = 0.20f),
                        GlowVioletLight.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.90f, size.height * 0.35f),
                    radius = size.width * 0.60f
                ),
                radius = size.width * 0.60f,
                center = Offset(size.width * 0.90f, size.height * 0.35f)
            )

            // Blob 3: Bottom-Left Royal Blue Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        GlowBlueLight.copy(alpha = 0.18f),
                        GlowBlueLight.copy(alpha = 0.04f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.20f, size.height * 0.85f),
                    radius = size.width * 0.50f
                ),
                radius = size.width * 0.50f,
                center = Offset(size.width * 0.20f, size.height * 0.85f)
            )
        }
    }
)

/**
 * High-performance, lightweight subtle glow modifier.
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
 */
fun Modifier.premiumPressEffect(
    pressedScale: Float = 0.96f,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
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

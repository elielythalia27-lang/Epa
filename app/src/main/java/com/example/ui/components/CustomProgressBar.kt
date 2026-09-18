package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modern Sleek Animated Linear Progress Bar with glowing multi-stop gradient fill,
 * traveling sheen highlight, and dynamic light/dark mode adaptation.
 */
@Composable
fun SleekLinearProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    isDarkTheme: Boolean = true,
    trackColor: Color? = null,
    color: Color? = null,
    gradientColors: List<Color> = listOf(
        Color(0xFF38BDF8),
        Color(0xFF2563EB),
        Color(0xFF8B5CF6)
    )
) {
    val effectiveTrackColor = trackColor ?: if (isDarkTheme) Color(0x33334155) else Color(0xFFE2E8F0)
    val effectiveGradient = if (color != null) listOf(color, color) else gradientColors
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "progress_anim"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val sheenTranslate by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sheen_translate"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(effectiveTrackColor)
    ) {
        if (animatedProgress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(height / 2))
                    .background(Brush.horizontalGradient(effectiveGradient))
            ) {
                // Subtle traveling highlight sheen for premium polish
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.4f),
                                    Color.Transparent
                                ),
                                start = Offset(sheenTranslate - 120f, 0f),
                                end = Offset(sheenTranslate, 0f)
                            )
                        )
                )
            }
        }
    }
}

/**
 * Determinate circular progress indicator with gradient stroke and smooth animation.
 */
@Composable
fun SleekDeterminateCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    strokeWidth: Dp = 3.5.dp,
    isDarkTheme: Boolean = true,
    gradientColors: List<Color> = listOf(
        Color(0xFF38BDF8),
        Color(0xFF2563EB)
    ),
    trackColor: Color? = null
) {
    val effectiveTrackColor = trackColor ?: if (isDarkTheme) Color(0x2E38BDF8) else Color(0x33000000)
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "determinate_circ_progress"
    )

    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        // Base Track
        drawCircle(
            color = effectiveTrackColor,
            style = stroke
        )
        // Filled Sweep
        if (animatedProgress > 0.01f) {
            drawArc(
                brush = Brush.sweepGradient(gradientColors),
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = stroke
            )
        }
    }
}

/**
 * Custom High-End Circular Loading Spinner with gradient sweep and glowing round cap.
 */
@Composable
fun SleekCircularProgressIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    strokeWidth: Dp = 3.dp,
    isDarkTheme: Boolean = true,
    gradientColors: List<Color> = listOf(
        Color(0xFF38BDF8),
        Color(0xFF2563EB),
        Color(0xFF8B5CF6)
    ),
    trackColor: Color? = null
) {
    val effectiveTrackColor = trackColor ?: if (isDarkTheme) Color(0x2238BDF8) else Color(0x22000000)
    val transition = rememberInfiniteTransition(label = "circular_transition")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing)
        ),
        label = "rotation_anim"
    )

    Canvas(modifier = modifier.size(size)) {
        val sweepAngle = 270f
        val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        // Track ring
        drawCircle(
            color = effectiveTrackColor,
            style = stroke
        )
        // Animated gradient arc
        drawArc(
            brush = Brush.sweepGradient(gradientColors),
            startAngle = rotation,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = stroke
        )
    }
}

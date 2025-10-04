package com.YashDoesCode.banking.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.YashDoesCode.banking.domain.model.CategorySpending
import com.YashDoesCode.banking.domain.model.formatMoney

@Composable
fun SpendingDonutChart(
    spendingList: List<CategorySpending>,
    totalSpentMinor: Long,
    modifier: Modifier = Modifier
) {
    val strokeWidth = 32.dp
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(spendingList) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
    }

    Box(
        modifier = modifier
            .size(240.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        val emptyTrackColor = MaterialTheme.colorScheme.surfaceVariant

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(strokeWidth / 2)
        ) {
            val strokePx = strokeWidth.toPx()
            val canvasSize = size.minDimension
            val arcSize = Size(canvasSize, canvasSize)
            val topLeft = Offset((size.width - canvasSize) / 2f, (size.height - canvasSize) / 2f)

            if (spendingList.isEmpty() || totalSpentMinor == 0L) {
                // Empty Track
                drawArc(
                    color = emptyTrackColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            } else {
                var currentAngle = -90f
                val totalSweep = 360f * animationProgress.value

                spendingList.forEach { item ->
                    val sweepAngle = (item.percentage / 100f) * totalSweep
                    val sliceColor = Color(item.category.colorHex)

                    if (sweepAngle > 0.5f) {
                        drawArc(
                            color = sliceColor,
                            startAngle = currentAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokePx, cap = StrokeCap.Butt)
                        )
                    }
                    currentAngle += sweepAngle
                }
            }
        }

        // Center summary label
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Total Spent",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formatMoney(totalSpentMinor),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
        }
    }
}

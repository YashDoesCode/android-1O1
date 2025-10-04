package com.YashDoesCode.calculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ButtonStyle {
    NUMBER,
    OPERATOR,
    FUNCTION,
    ACTION,
    ACCENT
}

@Composable
fun CalculatorButton(
    text: String,
    style: ButtonStyle,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val colorScheme = MaterialTheme.colorScheme

    val (backgroundColor, contentColor) = when (style) {
        ButtonStyle.NUMBER -> colorScheme.surfaceVariant to colorScheme.onSurface
        ButtonStyle.OPERATOR -> colorScheme.primaryContainer to colorScheme.onPrimaryContainer
        ButtonStyle.FUNCTION -> colorScheme.secondary to colorScheme.onSecondary
        ButtonStyle.ACTION -> colorScheme.surfaceVariant.copy(alpha = 0.7f) to colorScheme.onSurfaceVariant
        ButtonStyle.ACCENT -> colorScheme.primary to colorScheme.onPrimary
    }

    Box(
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = contentColor.copy(alpha = 0.3f))
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .semantics { contentDescription = text },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = if (text.length > 3) 18.sp else 26.sp,
            fontWeight = if (style == ButtonStyle.ACCENT || style == ButtonStyle.OPERATOR) FontWeight.Bold else FontWeight.Medium
        )
    }
}

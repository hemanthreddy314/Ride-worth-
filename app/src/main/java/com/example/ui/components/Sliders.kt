package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import com.example.util.rememberAppHaptics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuxurySlider(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    thumbIcon: ImageVector? = null,
    valueFormatter: (Float) -> String = { it.toInt().toString() },
    testTag: String = "luxury_slider"
) {
    val haptics = rememberAppHaptics()
    var lastHapticStep by remember { mutableFloatStateOf(value) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .background(SurfaceCard)
            .border(
                1.dp,
                SoftBorderColor,
                RideWorthShapes.large
            )
            .padding(18.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (thumbIcon != null) {
                    Icon(
                        imageVector = thumbIcon,
                        contentDescription = null,
                        tint = DeepSapphire,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Surface(
                shape = RideWorthShapes.small,
                color = DeepSapphire.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    DeepSapphire.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    text = valueFormatter(value),
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepSapphire,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Slider(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                if (kotlin.math.abs(newValue - lastHapticStep) > (valueRange.endInclusive - valueRange.start) / 20f) {
                    haptics.sliderSnap()
                    lastHapticStep = newValue
                }
            },
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = DeepSapphire,
                activeTrackColor = DeepSapphire,
                inactiveTrackColor = DividerColor
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RideWorthShapes.large)
                        .background(DeepSapphire)
                        .border(2.dp, TextPrimary, RideWorthShapes.large),
                    contentAlignment = Alignment.Center
                ) {
                    if (thumbIcon != null) {
                        Icon(
                            imageVector = thumbIcon,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RideWorthShapes.large)
                                .background(TextPrimary)
                        )
                    }
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = valueFormatter(valueRange.start),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Text(
                text = valueFormatter(valueRange.endInclusive),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.RideWorthShapes
import com.example.ui.theme.*
import com.example.util.AnimationSpecs
import com.example.util.rememberAppHaptics

@Composable
fun LuxuryPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    gradientColors: List<Color>? = null,
    testTag: String = "luxury_primary_button"
) {
    val haptics = rememberAppHaptics()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = AnimationSpecs.QuickSpring,
        label = "btn_scale"
    )

    val buttonBgColor = if (isPressed) ChampagneGoldDark else ChampagneGold

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RideWorthShapes.large)
            .background(buttonBgColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptics.heavyClick()
                onClick()
            }
            .defaultMinSize(minHeight = 56.dp)
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryBackground,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = PrimaryBackground,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LuxuryOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    testTag: String = "luxury_outlined_button"
) {
    val haptics = rememberAppHaptics()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = AnimationSpecs.QuickSpring,
        label = "btn_scale_outlined"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RideWorthShapes.large)
            .background(GlassSurface)
            .border(
                width = 1.dp,
                color = SoftBorderColor,
                shape = RideWorthShapes.large
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptics.lightClick()
                onClick()
            }
            .defaultMinSize(minHeight = 52.dp)
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

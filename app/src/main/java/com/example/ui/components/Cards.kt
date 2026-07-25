package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.shape.CircleShape
import com.example.data.models.ProFeature
import com.example.data.models.VehicleType
import com.example.ui.theme.*
import com.example.util.AnimationSpecs
import com.example.util.rememberAppHaptics

@Composable
fun VehicleSelectionCard(
    vehicleType: VehicleType,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "vehicle_card_${vehicleType.name.lowercase()}"
) {
    val haptics = rememberAppHaptics()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else if (isSelected) 1.02f else 1f,
        animationSpec = AnimationSpecs.QuickSpring,
        label = "vehicle_card_scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "vehicle_card_elevation"
    )

    val borderStrokeColor = if (isSelected) {
        ChampagneGold
    } else {
        SoftBorderColor
    }

    val icon: ImageVector = if (vehicleType == VehicleType.CAR) {
        Icons.Default.DirectionsCar
    } else {
        Icons.Default.TwoWheeler
    }

    Card(
        modifier = modifier
            .scale(scale)
            .clip(RideWorthShapes.large)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = borderStrokeColor,
                shape = RideWorthShapes.large
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptics.heavyClick()
                onSelect()
            }
            .testTag(testTag),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) GlassSurface else SurfaceCard
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RideWorthShapes.medium)
                            .background(
                                if (isSelected) ChampagneGold.copy(alpha = 0.15f) else SecondaryBackground
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = vehicleType.title,
                            tint = if (isSelected) ChampagneGold else TextPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    AnimatedVisibility(
                        visible = isSelected,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ChampagneGold,
                            modifier = Modifier.size(22.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = PrimaryBackground,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = vehicleType.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = vehicleType.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun RideWorthProLockedCard(
    onUnlockClick: () -> Unit,
    modifier: Modifier = Modifier,
    proFeatures: List<ProFeature> = emptyList(),
    testTag: String = "rideworth_pro_card"
) {
    val haptics = rememberAppHaptics()
    val infiniteTransition = rememberInfiniteTransition(label = "pro_shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gold_shimmer"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        ChampagneGold.copy(alpha = 0.3f * shimmerAlpha),
                        ChampagneGold.copy(alpha = 0.8f * shimmerAlpha),
                        ChampagneGold.copy(alpha = 0.3f * shimmerAlpha)
                    )
                ),
                shape = RideWorthShapes.large
            )
            .clickable {
                haptics.lightClick()
                onUnlockClick()
            }
            .testTag(testTag),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            ChampagneGold.copy(alpha = 0.08f * shimmerAlpha),
                            SurfaceCard
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RideWorthShapes.small)
                                .background(ChampagneGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = "Pro",
                                tint = ChampagneGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "RideWorth PRO",
                                style = MaterialTheme.typography.titleLarge,
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Automotive AI Intelligence",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Surface(
                        shape = RideWorthShapes.small,
                        color = ChampagneGold.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = ChampagneGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "COMING SOON",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Unlock AI Negotiation, Image Damage Scans & Live Dealer Price Intelligence.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val previewItems = if (proFeatures.isNotEmpty()) proFeatures else listOf(
                        ProFeature("ai_neg", "Future AI Negotiation Assistant", "Auto-generates seller offer scripts in Hindi & English", "AI PRO", "chat"),
                        ProFeature("ai_report", "Future AI Damage Scanner", "Scan vehicle photos for scratches & paint thickness", "AI PRO", "camera"),
                        ProFeature("ai_market", "Future AI Market Radar", "Predicts price trends over the next 6 months", "AI PRO", "radar")
                    )

                    previewItems.take(3).forEach { feature ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = ChampagneGold.copy(alpha = 0.7f),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = feature.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

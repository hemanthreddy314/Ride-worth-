package com.example.ui.screens.valuation.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ValuationFormState
import com.example.data.models.VehicleType
import com.example.engine.model.ConfidenceRating
import com.example.engine.model.SmartInsight
import com.example.engine.model.SmartWarning
import com.example.engine.model.ValuationResult
import com.example.engine.model.WarningSeverity
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldDark
import com.example.ui.theme.DeepSapphire
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GlassBorderHex
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.RideWorthShapes
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SoftBorderColor
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningYellow
import com.example.util.Formatters

// 1. Animated Currency Text (0 -> Target Price over 1500 ms)
@Composable
fun AnimatedCurrencyText(
    targetValue: Long,
    durationMs: Int = 1500,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(targetValue) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = durationMs,
                easing = FastOutSlowInEasing
            )
        )
    }

    val currentValue = (targetValue * animatedProgress.value).toLong()

    Text(
        text = "₹ ${Formatters.formatCurrency(currentValue)}",
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp,
            letterSpacing = (-0.5).sp
        ),
        color = ChampagneGold,
        modifier = modifier.semantics {
            contentDescription = "Estimated market value ₹ ${Formatters.formatCurrency(targetValue)}"
        }
    )
}

// 2. Vehicle Summary Top Header
@Composable
fun CertificateTopSection(formState: ValuationFormState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .border(1.dp, SoftBorderColor, RideWorthShapes.large),
        colors = CardDefaults.cardColors(containerColor = SecondaryBackground),
        shape = RideWorthShapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Success animation check badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SuccessGreen.copy(alpha = 0.12f))
                    .border(1.dp, SuccessGreen.copy(alpha = 0.3f), CircleShape)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = PrimaryBackground,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Text(
                    text = "Vehicle Valuation Completed",
                    style = MaterialTheme.typography.labelLarge,
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Premium Vehicle Image / Icon Silhouette Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RideWorthShapes.medium)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                GlassSurface,
                                SecondaryBackground
                            )
                        )
                    )
                    .border(1.dp, GlassBorderHex, RideWorthShapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (formState.vehicleType == VehicleType.CAR) Icons.Default.DirectionsCar else Icons.Default.TwoWheeler,
                        contentDescription = "Vehicle Silhouette",
                        tint = ChampagneGold.copy(alpha = 0.85f),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "RIDEWORTH CERTIFIED REPORT",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vehicle Brand & Model Title
            Text(
                text = "${formState.brand} ${formState.model}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = formState.variant,
                style = MaterialTheme.typography.bodyMedium,
                color = ChampagneGold,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Spec badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SpecPill(label = "${formState.registrationYear}")
                SpecPill(label = formState.fuelType)
                SpecPill(label = formState.transmission)
                SpecPill(label = formState.bodyType)
            }
        }
    }
}

@Composable
fun SpecPill(label: String) {
    Surface(
        shape = CircleShape,
        color = GlassSurface,
        border = BorderStroke(1.dp, SoftBorderColor)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontWeight = FontWeight.Medium
        )
    }
}

// 3. Hero Estimated Market Value Card
@Composable
fun HeroPrimaryValueCard(result: ValuationResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .shadow(12.dp, RideWorthShapes.large)
            .border(
                border = BorderStroke(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            ChampagneGold,
                            DeepSapphire,
                            ChampagneGoldDark
                        )
                    )
                ),
                shape = RideWorthShapes.large
            ),
        colors = CardDefaults.cardColors(containerColor = GlassSurface),
        shape = RideWorthShapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "ESTIMATED MARKET VALUE",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedCurrencyText(targetValue = result.range.bestMarketValue)

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SoftBorderColor)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Minimum Range",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "₹ ${Formatters.formatCurrency(result.range.minEstimatedValue)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(20.dp)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Maximum Range",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "₹ ${Formatters.formatCurrency(result.range.maxExpectedValue)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// 4. Horizontal Price Meter (Low Value | Fair Value | Premium Value)
@Composable
fun PriceMeterCard(result: ValuationResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .border(1.dp, SoftBorderColor, RideWorthShapes.large),
        colors = CardDefaults.cardColors(containerColor = SecondaryBackground),
        shape = RideWorthShapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Valuation Price Meter",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                modifier = Modifier.semantics { heading() }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Recommended selling price positioned along current market spectrum",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Meter Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
            ) {
                // Background Track with 3 gradient segments
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    DeepSapphire,
                                    ChampagneGold,
                                    SuccessGreen
                                )
                            )
                        )
                )

                // Recommended Pointer Marker
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .align(Alignment.Center)
                        .background(TextPrimary)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Low Value",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "₹${Formatters.formatCurrency(result.range.minEstimatedValue)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = ChampagneGold.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, ChampagneGold)
                    ) {
                        Text(
                            text = "Fair Value: ₹${Formatters.formatCurrency(result.range.bestMarketValue)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Premium Value",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "₹${Formatters.formatCurrency(result.range.maxExpectedValue)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// 5. Confidence Score Gauge Card
@Composable
fun ConfidenceScoreCard(result: ValuationResult) {
    val confidence = result.confidence
    val statusColor = when (confidence.rating) {
        ConfidenceRating.HIGH -> SuccessGreen
        ConfidenceRating.MEDIUM -> WarningYellow
        ConfidenceRating.LOW -> ErrorRed
    }

    val animatedSweep = remember { Animatable(0f) }
    LaunchedEffect(confidence.score) {
        animatedSweep.animateTo(
            targetValue = confidence.score / 100f,
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = 1400,
                easing = FastOutSlowInEasing
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .border(1.dp, SoftBorderColor, RideWorthShapes.large),
        colors = CardDefaults.cardColors(containerColor = SecondaryBackground),
        shape = RideWorthShapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Valuation Confidence Score",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    modifier = Modifier.semantics { heading() }
                )

                Surface(
                    shape = CircleShape,
                    color = statusColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "${confidence.rating.name} CONFIDENCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Large Circular Gauge Canvas
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(130.dp)
            ) {
                Canvas(modifier = Modifier.size(120.dp)) {
                    val strokeWidth = 12.dp.toPx()
                    drawArc(
                        color = SoftBorderColor,
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = statusColor,
                        startAngle = 135f,
                        sweepAngle = 270f * animatedSweep.value,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(confidence.score * animatedSweep.value).toInt()}%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = "Confidence",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = confidence.reasoning,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

// 6. Vehicle Health Score Gauge Card
@Composable
fun VehicleHealthGaugeCard(healthScore: Int, category: String) {
    val animatedSweep = remember { Animatable(0f) }
    LaunchedEffect(healthScore) {
        animatedSweep.animateTo(
            targetValue = healthScore / 100f,
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = 1400,
                easing = FastOutSlowInEasing
            )
        )
    }

    val healthColor = when {
        healthScore >= 85 -> SuccessGreen
        healthScore >= 70 -> WarningYellow
        else -> ErrorRed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .border(1.dp, SoftBorderColor, RideWorthShapes.large),
        colors = CardDefaults.cardColors(containerColor = SecondaryBackground),
        shape = RideWorthShapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vehicle Health Score",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    modifier = Modifier.semantics { heading() }
                )

                Surface(
                    shape = CircleShape,
                    color = healthColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, healthColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = healthColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(110.dp)
                ) {
                    Canvas(modifier = Modifier.size(100.dp)) {
                        val stroke = 10.dp.toPx()
                        drawArc(
                            color = SoftBorderColor,
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = healthColor,
                            startAngle = 135f,
                            sweepAngle = 270f * animatedSweep.value,
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(healthScore * animatedSweep.value).toInt()}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = TextPrimary
                        )
                        Text(
                            text = "/ 100",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HealthSubPill(label = "Engine & Gearbox", status = "Excellent")
                    HealthSubPill(label = "Tyres & Brakes", status = "Good")
                    HealthSubPill(label = "Body & Paint", status = "Good")
                    HealthSubPill(label = "Service Records", status = "Complete")
                }
            }
        }
    }
}

@Composable
fun HealthSubPill(label: String, status: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(SuccessGreen)
        )
        Text(
            text = "$label:",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// 7. Expandable Condition Breakdown Section
@Composable
fun ConditionBreakdownSection(
    factors: List<ConditionBreakdownFactor>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .border(1.dp, SoftBorderColor, RideWorthShapes.large),
        colors = CardDefaults.cardColors(containerColor = SecondaryBackground),
        shape = RideWorthShapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatListNumbered,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Condition Breakdown",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        modifier = Modifier.semantics { heading() }
                    )
                }

                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse breakdown" else "Expand breakdown",
                        tint = TextSecondary
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    factors.forEach { item ->
                        ConditionFactorCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun ConditionFactorCard(item: ConditionBreakdownFactor) {
    val (badgeBg, textTint) = when (item.isPositive) {
        true -> Pair(SuccessGreen.copy(alpha = 0.15f), SuccessGreen)
        false -> Pair(ErrorRed.copy(alpha = 0.15f), ErrorRed)
        null -> Pair(WarningYellow.copy(alpha = 0.15f), WarningYellow)
    }

    Surface(
        shape = RideWorthShapes.medium,
        color = GlassSurface,
        border = BorderStroke(1.dp, SoftBorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Surface(
                        shape = CircleShape,
                        color = badgeBg
                    ) {
                        Text(
                            text = "${item.score}/100",
                            style = MaterialTheme.typography.labelSmall,
                            color = textTint,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.explanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = item.impact,
                style = MaterialTheme.typography.labelMedium,
                color = textTint,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 8. Market Demand Matrix Section
@Composable
fun MarketDemandSection(items: List<MarketDemandFactor>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .border(1.dp, SoftBorderColor, RideWorthShapes.large),
        colors = CardDefaults.cardColors(containerColor = SecondaryBackground),
        shape = RideWorthShapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Market Demand & Resale Metrics",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    modifier = Modifier.semantics { heading() }
                )
            }

            items.forEach { factor ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = factor.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = factor.impactText,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Mini Progress bar
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(SoftBorderColor)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width((60 * (factor.score / 100f)).dp)
                                    .clip(CircleShape)
                                    .background(ChampagneGold)
                            )
                        }

                        Text(
                            text = "${factor.score}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// 9. Value Impact Timeline (Positive & Negative Factors)
@Composable
fun ValueImpactTimelineSection(adjustments: List<ValueAdjustmentItem>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .border(1.dp, SoftBorderColor, RideWorthShapes.large),
        colors = CardDefaults.cardColors(containerColor = SecondaryBackground),
        shape = RideWorthShapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Insights,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Value Impact Timeline",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    modifier = Modifier.semantics { heading() }
                )
            }

            Text(
                text = "Detailed breakdown showing exactly how each factor adjusted your vehicle's final valuation",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            adjustments.forEach { item ->
                val badgeColor = if (item.isPositive) SuccessGreen else ErrorRed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(badgeColor)
                        )
                        Text(
                            text = item.factor,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = item.amountFormatted,
                        style = MaterialTheme.typography.titleSmall,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// 10. Seller Score & Buyer Recommendation Cards
@Composable
fun SellerScoreAndBuyerRecommendationSection(
    sellerScore: Int,
    sellerCategory: String,
    buyerRecommendation: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Seller Score Card
        Card(
            modifier = Modifier
                .weight(1f)
                .clip(RideWorthShapes.medium)
                .border(1.dp, SoftBorderColor, RideWorthShapes.medium),
            colors = CardDefaults.cardColors(containerColor = SecondaryBackground),
            shape = RideWorthShapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "SELLER SCORE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$sellerScore/100",
                    style = MaterialTheme.typography.titleLarge,
                    color = ChampagneGold,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = sellerCategory,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Buyer Recommendation Card
        Card(
            modifier = Modifier
                .weight(1f)
                .clip(RideWorthShapes.medium)
                .border(1.dp, SoftBorderColor, RideWorthShapes.medium),
            colors = CardDefaults.cardColors(containerColor = SecondaryBackground),
            shape = RideWorthShapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "BUYER ADVICE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = CircleShape,
                    color = SuccessGreen.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = buyerRecommendation,
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// 11. Smart Insights Section
@Composable
fun SmartInsightsSection(insights: List<SmartInsight>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .border(1.dp, SoftBorderColor, RideWorthShapes.large),
        colors = CardDefaults.cardColors(containerColor = SecondaryBackground),
        shape = RideWorthShapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Smart Insights",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    modifier = Modifier.semantics { heading() }
                )
            }

            insights.forEach { insight ->
                Surface(
                    shape = RideWorthShapes.medium,
                    color = GlassSurface,
                    border = BorderStroke(1.dp, GlassBorderHex),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(
                            text = insight.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = insight.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

// 12. Smart Warnings Section
@Composable
fun SmartWarningsSection(warnings: List<SmartWarning>) {
    if (warnings.isEmpty()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .border(1.dp, ErrorRed.copy(alpha = 0.5f), RideWorthShapes.large),
        colors = CardDefaults.cardColors(containerColor = SecondaryBackground),
        shape = RideWorthShapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Smart Warnings & Flags",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    modifier = Modifier.semantics { heading() }
                )
            }

            warnings.forEach { warning ->
                val (borderColor, tint) = when (warning.severity) {
                    WarningSeverity.CRITICAL -> Pair(ErrorRed, ErrorRed)
                    WarningSeverity.MODERATE -> Pair(WarningYellow, WarningYellow)
                    WarningSeverity.INFO -> Pair(DeepSapphire, DeepSapphire)
                }

                Surface(
                    shape = RideWorthShapes.medium,
                    color = tint.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, borderColor.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(
                            text = warning.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = tint,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = warning.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

// 13. Negotiation Tips Section
@Composable
fun NegotiationTipsSection(recommendations: List<String>, smartTip: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .border(1.dp, SoftBorderColor, RideWorthShapes.large),
        colors = CardDefaults.cardColors(containerColor = SecondaryBackground),
        shape = RideWorthShapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Negotiation Tips",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    modifier = Modifier.semantics { heading() }
                )
            }

            Surface(
                shape = RideWorthShapes.medium,
                color = ChampagneGold.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "💡 $smartTip",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(14.dp),
                    lineHeight = 20.sp
                )
            }

            recommendations.forEachIndexed { index, tip ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(GlassSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// 14. Comprehensive Certificate Summary Card
@Composable
fun ValuationSummaryCard(
    result: ValuationResult,
    healthScore: Int,
    healthCategory: String,
    difficulty: String,
    timeToSell: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .border(1.dp, SoftBorderColor, RideWorthShapes.large),
        colors = CardDefaults.cardColors(containerColor = SecondaryBackground),
        shape = RideWorthShapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Valuation Summary Certificate",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                modifier = Modifier.semantics { heading() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryRow(
                    label = "Estimated Fair Value",
                    value = "₹ ${Formatters.formatCurrency(result.range.bestMarketValue)}"
                )
                SummaryRow(
                    label = "Vehicle Health Score",
                    value = "$healthScore / 100 ($healthCategory)"
                )
                SummaryRow(
                    label = "Engine Accuracy Confidence",
                    value = "${result.confidence.score}% (${result.confidence.rating.name})"
                )
                SummaryRow(
                    label = "Overall Vehicle Condition",
                    value = "${result.scores.conditionScore} / 100"
                )
                SummaryRow(
                    label = "Selling Difficulty Level",
                    value = difficulty
                )
                SummaryRow(
                    label = "Estimated Time to Sell",
                    value = timeToSell
                )
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// 15. Action Buttons Bottom Bar (Primary: Save Report, Secondary: Share, Compare, New Valuation)
@Composable
fun ActionButtonsBar(
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onResetClick: () -> Unit,
    onExportPdfClick: () -> Unit = {}
) {
    Surface(
        color = PrimaryBackground,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Primary Save Report Button
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RideWorthShapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSaved) SuccessGreen else ChampagneGold,
                    contentColor = PrimaryBackground
                )
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.CheckCircle else Icons.Default.Bookmark,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSaved) "Report Saved" else "Save Report",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Secondary Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onShareClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RideWorthShapes.medium,
                    border = BorderStroke(1.dp, ChampagneGold)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = ChampagneGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Share",
                        style = MaterialTheme.typography.labelMedium,
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onExportPdfClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RideWorthShapes.medium,
                    border = BorderStroke(1.dp, ChampagneGold)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "PDF Export",
                        tint = ChampagneGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PDF",
                        style = MaterialTheme.typography.labelMedium,
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onResetClick,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(44.dp),
                    shape = RideWorthShapes.medium,
                    border = BorderStroke(1.dp, SoftBorderColor)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "New Valuation",
                        tint = TextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "New Valuation",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

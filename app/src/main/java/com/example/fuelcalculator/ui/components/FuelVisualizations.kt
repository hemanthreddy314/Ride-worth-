package com.example.fuelcalculator.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.Formatters

@Composable
fun EfficiencyGauge(
    score: Int,
    category: String,
    modifier: Modifier = Modifier,
    testTag: String = "efficiency_gauge"
) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "gauge_anim"
    )

    val gaugeColor = when {
        score >= 85 -> SuccessGreen
        score >= 70 -> ChampagneGold
        score >= 55 -> WarningYellow
        else -> ErrorRed
    }

    Box(
        modifier = modifier
            .size(170.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = strokeWidth / 2f

            // Background Track Arc (240 degrees)
            drawArc(
                color = SoftBorderColor.copy(alpha = 0.4f),
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = androidx.compose.ui.geometry.Size(diameter, diameter),
                topLeft = androidx.compose.ui.geometry.Offset(topLeft, topLeft)
            )

            // Progress Arc
            val progressSweep = (animatedScore / 100f) * 240f
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(gaugeColor.copy(alpha = 0.6f), gaugeColor)
                ),
                startAngle = 150f,
                sweepAngle = progressSweep,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = androidx.compose.ui.geometry.Size(diameter, diameter),
                topLeft = androidx.compose.ui.geometry.Offset(topLeft, topLeft)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${animatedScore.toInt()}",
                style = MaterialTheme.typography.displayMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "SCORE / 100",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 9.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = gaugeColor.copy(alpha = 0.2f),
                shape = CircleShape
            ) {
                Text(
                    text = category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = gaugeColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun FuelTankIndicator(
    refillsPerMonth: Double,
    unitSymbol: String,
    tankCapacity: Float,
    fuelRequiredMonth: Double,
    modifier: Modifier = Modifier
) {
    val fillPercent = (fuelRequiredMonth / (tankCapacity * 5.0)).coerceIn(0.1, 1.0).toFloat()
    val animatedFill by animateFloatAsState(
        targetValue = fillPercent,
        animationSpec = tween(1000),
        label = "tank_fill_anim"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalGasStation,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Fuel Required per Month",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = "${String.format("%.1f", fuelRequiredMonth)} $unitSymbol",
                style = MaterialTheme.typography.titleMedium,
                color = ChampagneGold,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Fuel Tank Bar Visual
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RideWorthShapes.medium)
                .background(SecondaryBackground)
                .border(1.dp, GlassBorderHex, RideWorthShapes.medium)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedFill)
                    .clip(RideWorthShapes.medium)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(DeepSapphire, ChampagneGold)
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tank Capacity: ${tankCapacity.toInt()} $unitSymbol",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Text(
                text = "⚡ ~${String.format("%.1f", refillsPerMonth)} Tank Refills / month",
                style = MaterialTheme.typography.bodySmall,
                color = ChampagneGold,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun YearlyCostVisualCard(
    daily: Double,
    weekly: Double,
    monthly: Double,
    yearly: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Fuel Cost Breakdown",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CostMetricChip(
                    label = "Daily",
                    amount = daily,
                    modifier = Modifier.weight(1f)
                )
                CostMetricChip(
                    label = "Weekly",
                    amount = weekly,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CostMetricChip(
                    label = "Monthly",
                    amount = monthly,
                    highlight = true,
                    modifier = Modifier.weight(1f)
                )
                CostMetricChip(
                    label = "Yearly",
                    amount = yearly,
                    highlight = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CostMetricChip(
    label: String,
    amount: Double,
    highlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RideWorthShapes.medium)
            .background(if (highlight) ChampagneGold.copy(alpha = 0.12f) else SecondaryBackground)
            .border(
                1.dp,
                if (highlight) ChampagneGold.copy(alpha = 0.4f) else GlassBorderHex,
                RideWorthShapes.medium
            )
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = if (highlight) ChampagneGold else TextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = Formatters.formatLakhs(amount.toLong()),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

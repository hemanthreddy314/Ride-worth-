package com.example.maintenance.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maintenance.model.*
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun VehicleHealthGauge(
    healthScore: VehicleHealthScore,
    modifier: Modifier = Modifier
) {
    val animatedScore by animateIntAsState(
        targetValue = healthScore.score,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "scoreAnim"
    )

    val scoreColor = Color(healthScore.colorHex)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RideWorthShapes.large,
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "OWNERSHIP HEALTH SCORE",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(140.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 12.dp.toPx()
                    val sweep = (animatedScore / 100f) * 260f

                    // Track background arc
                    drawArc(
                        color = GlassSurface,
                        startAngle = 140f,
                        sweepAngle = 260f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Active progress arc
                    drawArc(
                        color = scoreColor,
                        startAngle = 140f,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$animatedScore",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "/100",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = CircleShape,
                color = scoreColor.copy(alpha = 0.16f),
                border = androidx.compose.foundation.BorderStroke(1.dp, scoreColor.copy(alpha = 0.4f))
            ) {
                Text(
                    text = healthScore.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = scoreColor,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = healthScore.mainSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            if (healthScore.riskFactors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryBackground, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    healthScore.riskFactors.forEach { risk ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = WarningYellow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = risk,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MaintenanceSummaryCards(
    estimate: MaintenanceCostEstimate,
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
            maximumFractionDigits = 0
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Top Hero Card: Monthly & Cost/KM
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RideWorthShapes.large,
            color = GlassSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ESTIMATED MONTHLY COST",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currencyFormat.format(estimate.monthlyCost),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChampagneGold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "COST PER KM",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Text(
                            text = String.format(Locale.ENGLISH, "₹%.2f/km", estimate.costPerKm),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // Secondary 3-Card Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricStatBox(
                title = "Yearly Cost",
                value = currencyFormat.format(estimate.yearlyCost),
                modifier = Modifier.weight(1f)
            )
            MetricStatBox(
                title = "3-Year Cost",
                value = currencyFormat.format(estimate.threeYearCost),
                modifier = Modifier.weight(1f)
            )
            MetricStatBox(
                title = "5-Year Cost",
                value = currencyFormat.format(estimate.fiveYearCost),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricStatBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RideWorthShapes.medium,
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun OwnershipCostCard(
    summary: OwnershipCostSummary,
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply { maximumFractionDigits = 0 }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RideWorthShapes.large,
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Ownership Cost",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Surface(
                    shape = CircleShape,
                    color = DeepSapphire.copy(alpha = 0.16f)
                ) {
                    Text(
                        text = "Fuel + Maintenance + Insurance",
                        style = MaterialTheme.typography.labelSmall,
                        color = DeepSapphire,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OwnershipRow(label = "Monthly Fuel Expense", value = currencyFormat.format(summary.monthlyFuelCost), icon = Icons.Default.LocalGasStation)
            OwnershipRow(label = "Monthly Maintenance", value = currencyFormat.format(summary.monthlyMaintenanceCost), icon = Icons.Default.Build)
            OwnershipRow(label = "Monthly Insurance Allocation", value = currencyFormat.format(summary.monthlyInsuranceCost), icon = Icons.Default.Shield)
            OwnershipRow(label = "Monthly Consumables", value = currencyFormat.format(summary.monthlyConsumablesCost), icon = Icons.Default.ShoppingBag)

            Divider(color = DividerColor, modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Monthly Cost",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = currencyFormat.format(summary.monthlyTotal),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ChampagneGold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "5-Year Cumulative Ownership",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Text(
                    text = currencyFormat.format(summary.fiveYearTotal),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun OwnershipRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

@Composable
fun ServiceScheduleItemCard(
    item: ServiceItem,
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply { maximumFractionDigits = 0 }
    }

    val (badgeBg, badgeText) = when (item.status) {
        ServiceStatusCategory.DUE_NOW -> ErrorRed.copy(alpha = 0.16f) to ErrorRed
        ServiceStatusCategory.DUE_SOON -> WarningYellow.copy(alpha = 0.16f) to WarningYellow
        ServiceStatusCategory.UPCOMING -> DeepSapphire.copy(alpha = 0.16f) to DeepSapphire
        ServiceStatusCategory.GOOD -> SuccessGreen.copy(alpha = 0.16f) to SuccessGreen
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RideWorthShapes.medium,
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GlassSurface,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (item.isMajor) Icons.Default.Engineering else Icons.Default.Handyman,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Interval: ${item.intervalKm} km / ${item.intervalMonths} mo",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = badgeBg
                ) {
                    Text(
                        text = item.status.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = badgeText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Due in ~${item.dueInKm} km (${item.dueInMonths} mo)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }

                Text(
                    text = currencyFormat.format(item.estimatedCost),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ChampagneGold
                )
            }
        }
    }
}

@Composable
fun ServiceTimelineVisualizer(
    milestones: List<TimelineMilestone>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RideWorthShapes.large,
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Upcoming Service Timeline",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Key milestones scheduled for your vehicle",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            milestones.forEachIndexed { index, m ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(28.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (m.isUrgent) ErrorRed else ChampagneGold,
                            modifier = Modifier.size(12.dp)
                        ) {}

                        if (index < milestones.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(36.dp)
                                    .background(SoftBorderColor)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = m.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "in ${m.dueInMonths} mo",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (m.isUrgent) ErrorRed else ChampagneGold
                            )
                        }
                        Text(
                            text = m.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableCostBreakdownCategoryCard(
    category: CostBreakdownCategory,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply { maximumFractionDigits = 0 } }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RideWorthShapes.medium,
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = getCategoryIcon(category.iconType),
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${category.percentageOfTotal.toInt()}% of yearly budget",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currencyFormat.format(category.estimatedAnnualCost),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    Divider(color = DividerColor, modifier = Modifier.padding(bottom = 10.dp))
                    Text(
                        text = "Included Consumables & Inspections:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ChampagneGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    category.items.forEach { item ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getCategoryIcon(iconType: String): ImageVector {
    return when (iconType) {
        "ENGINE" -> Icons.Default.Build
        "TYRES" -> Icons.Default.Speed
        "BRAKES" -> Icons.Default.Report
        "SUSPENSION" -> Icons.Default.Tune
        "ELECTRICAL" -> Icons.Default.ElectricCar
        else -> Icons.Default.Category
    }
}

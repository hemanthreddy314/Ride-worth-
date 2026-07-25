package com.example.ui.screens.comparison

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comparison.model.*
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ComparisonHeader(
    specA: ComparisonVehicleSpec,
    specB: ComparisonVehicleSpec,
    onSwapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("comparison_header_card"),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GlassBorderHex, ChampagneGold.copy(alpha = 0.3f), GlassBorderHex)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vehicle A
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "VEHICLE A",
                        style = MaterialTheme.typography.labelSmall,
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${specA.formState.brand} ${specA.formState.model}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${specA.formState.manufacturingYear} • ${specA.formState.fuelType}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = formatCurrency(specA.result.range.bestMarketValue),
                        style = MaterialTheme.typography.titleSmall,
                        color = SuccessGreen,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                IconButton(
                    onClick = onSwapClick,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(40.dp)
                        .background(DeepSapphire.copy(alpha = 0.2f), CircleShape)
                        .border(1.dp, DeepSapphire.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swap Vehicles",
                        tint = DeepSapphire
                    )
                }

                // Vehicle B
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "VEHICLE B",
                        style = MaterialTheme.typography.labelSmall,
                        color = DeepSapphire,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${specB.formState.brand} ${specB.formState.model}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${specB.formState.manufacturingYear} • ${specB.formState.fuelType}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = formatCurrency(specB.result.range.bestMarketValue),
                        style = MaterialTheme.typography.titleSmall,
                        color = SuccessGreen,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun WinnerSummaryCard(
    winner: WinnerSummary,
    specA: ComparisonVehicleSpec,
    specB: ComparisonVehicleSpec,
    modifier: Modifier = Modifier
) {
    val isEqual = winner.overallWinner == BetterVehicle.EQUAL
    val winnerColor = if (isEqual) ChampagneGold else SuccessGreen

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("winner_summary_card"),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(winnerColor.copy(alpha = 0.6f), GlassBorderHex)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(winnerColor.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isEqual) Icons.Default.Balance else Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = winnerColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "OVERALL VERDICT",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = winner.winnerTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = winner.winnerReason,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary.copy(alpha = 0.9f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = DividerColor)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = winner.buyerRecommendation,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun ValueForMoneyCard(
    vfm: ValueForMoneyRating,
    nameA: String,
    nameB: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("value_for_money_card"),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GlassBorderHex, GlassBorderHex)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = ChampagneGold
                )
                Text(
                    text = "Value for Money Rating",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                VfmItem(
                    title = nameA,
                    index = vfm.indexA,
                    rating = vfm.ratingA,
                    badgeColor = ChampagneGold,
                    modifier = Modifier.weight(1f)
                )

                VfmItem(
                    title = nameB,
                    index = vfm.indexB,
                    rating = vfm.ratingB,
                    badgeColor = DeepSapphire,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun VfmItem(
    title: String,
    index: Int,
    rating: String,
    badgeColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(SecondaryBackground, RideWorthShapes.medium)
            .border(1.dp, GlassBorderHex, RideWorthShapes.medium)
            .padding(14.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$index / 100",
                style = MaterialTheme.typography.titleLarge,
                color = badgeColor,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                color = badgeColor.copy(alpha = 0.2f),
                shape = CircleShape
            ) {
                Text(
                    text = rating,
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ComparisonTable(
    rows: List<ComparisonRow>,
    nameA: String,
    nameB: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("comparison_table_card"),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GlassBorderHex, GlassBorderHex)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Detailed Side-by-Side Specs",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Table Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SecondaryBackground, RideWorthShapes.small)
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SPECIFICATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.weight(1.2f)
                )
                Text(
                    text = nameA,
                    style = MaterialTheme.typography.labelSmall,
                    color = ChampagneGold,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = nameB,
                    style = MaterialTheme.typography.labelSmall,
                    color = DeepSapphire,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            rows.forEachIndexed { index, row ->
                ComparisonTableRow(
                    row = row,
                    isEven = index % 2 == 0
                )
            }
        }
    }
}

@Composable
private fun ComparisonTableRow(
    row: ComparisonRow,
    isEven: Boolean
) {
    val bg = if (isEven) SecondaryBackground.copy(alpha = 0.5f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(6.dp))
            .padding(vertical = 10.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = row.label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.weight(1.2f)
        )

        RowValueBadge(
            value = row.valueA,
            highlight = row.highlightA,
            modifier = Modifier.weight(1f)
        )

        RowValueBadge(
            value = row.valueB,
            highlight = row.highlightB,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RowValueBadge(
    value: String,
    highlight: HighlightType,
    modifier: Modifier = Modifier
) {
    val bgColor = when (highlight) {
        HighlightType.BETTER -> SuccessGreen.copy(alpha = 0.15f)
        HighlightType.LOWER -> ErrorRed.copy(alpha = 0.12f)
        HighlightType.SIMILAR -> Color.Transparent
    }

    val textColor = when (highlight) {
        HighlightType.BETTER -> SuccessGreen
        HighlightType.LOWER -> TextPrimary.copy(alpha = 0.8f)
        HighlightType.SIMILAR -> TextPrimary
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(vertical = 4.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = if (highlight == HighlightType.BETTER) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CategoryScoreboard(
    scores: List<CategoryScoreItem>,
    nameA: String,
    nameB: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_scoreboard_card"),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GlassBorderHex, GlassBorderHex)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Score,
                    contentDescription = null,
                    tint = DeepSapphire
                )
                Text(
                    text = "Category Performance Scoreboard",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            scores.forEach { item ->
                CategoryScoreRow(item = item)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun CategoryScoreRow(item: CategoryScoreItem) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.categoryName,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${item.scoreA} vs ${item.scoreB}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = { (item.scoreA / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(CircleShape),
                color = ChampagneGold,
                trackColor = SecondaryBackground
            )

            LinearProgressIndicator(
                progress = { (item.scoreB / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(CircleShape),
                color = DeepSapphire,
                trackColor = SecondaryBackground
            )
        }
    }
}

@Composable
fun MonthlyRunningCostCard(
    cost: MonthlyRunningCost,
    nameA: String,
    nameB: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("monthly_running_cost_card"),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GlassBorderHex, GlassBorderHex)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalGasStation,
                    contentDescription = null,
                    tint = SuccessGreen
                )
                Text(
                    text = "Monthly Running Cost Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            CostBreakdownRow("Fuel / Charging", formatCurrency(cost.fuelCostA), formatCurrency(cost.fuelCostB))
            CostBreakdownRow("Maintenance Reserves", formatCurrency(cost.maintenanceCostA), formatCurrency(cost.maintenanceCostB))
            CostBreakdownRow("Insurance Allocation", formatCurrency(cost.insuranceCostA), formatCurrency(cost.insuranceCostB))

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(modifier = Modifier.height(8.dp))

            CostBreakdownRow(
                label = "Total Monthly Est.",
                valA = formatCurrency(cost.totalMonthlyA),
                valB = formatCurrency(cost.totalMonthlyB),
                isBold = true
            )
        }
    }
}

@Composable
private fun CostBreakdownRow(
    label: String,
    valA: String,
    valB: String,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall,
            color = if (isBold) TextPrimary else TextSecondary,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = valA,
                style = MaterialTheme.typography.bodySmall,
                color = ChampagneGold,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
            )
            Text(
                text = valB,
                style = MaterialTheme.typography.bodySmall,
                color = DeepSapphire,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun SmartInsightsSection(
    insights: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("smart_insights_card"),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GlassBorderHex, GlassBorderHex)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = ChampagneGold
                )
                Text(
                    text = "Smart Comparison Insights",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            insights.forEach { insight ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )
                    Text(
                        text = insight,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun ProsConsCard(
    prosCons: VehicleProsCons,
    nameA: String,
    nameB: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pros_cons_card"),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GlassBorderHex, GlassBorderHex)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbsUpDown,
                        contentDescription = null,
                        tint = ChampagneGold
                    )
                    Text(
                        text = "Pros & Cons Analysis",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = TextSecondary
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = nameA,
                        style = MaterialTheme.typography.titleSmall,
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    prosCons.prosA.forEach { pro ->
                        BulletItem(pro, isPro = true)
                    }
                    prosCons.consA.forEach { con ->
                        BulletItem(con, isPro = false)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = nameB,
                        style = MaterialTheme.typography.titleSmall,
                        color = DeepSapphire,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    prosCons.prosB.forEach { pro ->
                        BulletItem(pro, isPro = true)
                    }
                    prosCons.consB.forEach { con ->
                        BulletItem(con, isPro = false)
                    }
                }
            }
        }
    }
}

@Composable
private fun BulletItem(text: String, isPro: Boolean) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (isPro) Icons.Default.AddCircle else Icons.Default.RemoveCircle,
            contentDescription = null,
            tint = if (isPro) SuccessGreen else ErrorRed,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
fun DifferenceSummaryCard(
    summary: DifferenceSummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("difference_summary_card"),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GlassBorderHex, GlassBorderHex)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = DeepSapphire
                )
                Text(
                    text = "Key Differences & Deltas",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = SecondaryBackground,
                shape = RideWorthShapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = summary.estimatedOwnershipDifference,
                        style = MaterialTheme.typography.bodySmall,
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = summary.expectedResaleDifference,
                        style = MaterialTheme.typography.bodySmall,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun formatCurrency(amount: Long): String {
    return "₹ " + NumberFormat.getNumberInstance(Locale("en", "IN")).format(amount)
}

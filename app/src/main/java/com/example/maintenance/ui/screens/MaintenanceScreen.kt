package com.example.maintenance.ui.screens

import android.content.Intent
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.models.VehicleType
import com.example.maintenance.model.*
import com.example.maintenance.ui.components.*
import com.example.maintenance.ui.viewmodel.MaintenanceViewModel
import com.example.ui.components.SectionTitle
import com.example.ui.theme.*
import com.example.util.rememberAppHaptics
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceScreen(
    viewModel: MaintenanceViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    testTag: String = "maintenance_screen"
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val haptics = rememberAppHaptics()

    LaunchedEffect(Unit) {
        viewModel.initRepository(context)
    }

    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = Modifier.testTag(testTag),
        topBar = {
            Surface(color = PrimaryBackground) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            haptics.lightClick()
                            onNavigateBack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Maintenance Estimator",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Smart Service Planner & Ownership Cost",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = {
                            haptics.lightClick()
                            viewModel.openHistorySheet()
                        }) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History",
                                tint = ChampagneGold
                            )
                        }

                        IconButton(onClick = {
                            haptics.lightClick()
                            val shareText = viewModel.getShareSummaryText()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Maintenance Summary")
                            context.startActivity(shareIntent)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = ChampagneGold
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = SurfaceCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            haptics.heavyClick()
                            viewModel.saveEstimate(context)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.isSaved) SuccessGreen else ChampagneGold,
                            contentColor = PrimaryBackground
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isSaved) Icons.Default.Check else Icons.Default.Bookmark,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.isSaved) "Saved to History" else "Save Estimate",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(PrimaryBackground)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Vehicle Type & Details Selection Header
            item {
                VehicleSelectionHeader(
                    inputs = uiState.inputs,
                    onVehicleTypeChange = { type ->
                        haptics.lightClick()
                        viewModel.updateVehicleType(type)
                    },
                    onBrandChange = { brand ->
                        viewModel.updateManufacturerAndModel(brand, uiState.inputs.model)
                    },
                    onYearChange = { year ->
                        viewModel.updateYear(year)
                    }
                )
            }

            // Odometer & Driving Sliders Card
            item {
                InputsControlCard(
                    inputs = uiState.inputs,
                    onOdometerChange = { odo -> viewModel.updateOdometerKm(odo) },
                    onMonthlyDistanceChange = { km -> viewModel.updateMonthlyDistanceKm(km) },
                    onServiceTypeChange = { st -> viewModel.updateServiceType(st) },
                    onRoadConditionChange = { rc -> viewModel.updateRoadCondition(rc) },
                    onDrivingStyleChange = { ds -> viewModel.updateDrivingStyle(ds) }
                )
            }

            // Tab Navigation Bar
            item {
                TabRow(
                    selectedTabIndex = uiState.selectedTab,
                    containerColor = GlassSurface,
                    contentColor = ChampagneGold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, SoftBorderColor, RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = uiState.selectedTab == 0,
                        onClick = {
                            haptics.lightClick()
                            viewModel.setTab(0)
                        },
                        text = { Text("Overview", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = uiState.selectedTab == 1,
                        onClick = {
                            haptics.lightClick()
                            viewModel.setTab(1)
                        },
                        text = { Text("Services", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = uiState.selectedTab == 2,
                        onClick = {
                            haptics.lightClick()
                            viewModel.setTab(2)
                        },
                        text = { Text("Ownership", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = uiState.selectedTab == 3,
                        onClick = {
                            haptics.lightClick()
                            viewModel.setTab(3)
                        },
                        text = { Text("Breakdown", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            when (uiState.selectedTab) {
                0 -> {
                    item { MaintenanceSummaryCards(estimate = uiState.result.costEstimate) }
                    item { VehicleHealthGauge(healthScore = uiState.result.healthScore) }
                    item { ServiceTimelineVisualizer(milestones = uiState.result.timelineMilestones) }
                    item { InsightsListSection(insights = uiState.result.smartInsights) }
                }
                1 -> {
                    item {
                        SectionTitle(
                            title = "Upcoming Service Schedule",
                            badgeText = "${uiState.result.upcomingServices.size} ITEMS"
                        )
                    }
                    items(uiState.result.upcomingServices) { service ->
                        ServiceScheduleItemCard(item = service)
                    }
                }
                2 -> {
                    item { OwnershipCostCard(summary = uiState.result.ownershipCost) }
                    item { InsightsListSection(insights = uiState.result.smartRecommendations, title = "Smart Recommendations") }
                }
                3 -> {
                    item {
                        SectionTitle(title = "Component Cost Breakdown")
                    }
                    items(uiState.result.costBreakdown) { category ->
                        ExpandableCostBreakdownCategoryCard(category = category)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // History Bottom Sheet
    if (uiState.showHistorySheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeHistorySheet() },
            sheetState = historySheetState,
            containerColor = PrimaryBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Saved Maintenance History",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.historyQuery,
                    onValueChange = { viewModel.updateHistoryQuery(it) },
                    placeholder = { Text("Search by vehicle name...", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ChampagneGold) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = SoftBorderColor
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                val filteredHistory = uiState.historyList.filter {
                    it.vehicleName.contains(uiState.historyQuery, ignoreCase = true)
                }

                if (filteredHistory.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No saved maintenance records found.",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredHistory) { item ->
                            HistoryItemRow(
                                item = item,
                                onReopen = {
                                    haptics.lightClick()
                                    viewModel.reopenHistoryRecord(item)
                                },
                                onDelete = {
                                    haptics.heavyClick()
                                    viewModel.deleteHistoryRecord(item.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VehicleSelectionHeader(
    inputs: MaintenanceInputs,
    onVehicleTypeChange: (VehicleType) -> Unit,
    onBrandChange: (String) -> Unit,
    onYearChange: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
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
                    text = "Selected Vehicle",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                // Bike / Car Toggle
                Row(
                    modifier = Modifier
                        .background(GlassSurface, CircleShape)
                        .padding(4.dp)
                ) {
                    val isCar = inputs.vehicleType == VehicleType.CAR
                    Surface(
                        modifier = Modifier.clickable { onVehicleTypeChange(VehicleType.CAR) },
                        shape = CircleShape,
                        color = if (isCar) ChampagneGold else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = if (isCar) PrimaryBackground else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Car",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isCar) PrimaryBackground else TextSecondary
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.clickable { onVehicleTypeChange(VehicleType.BIKE) },
                        shape = CircleShape,
                        color = if (!isCar) ChampagneGold else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.TwoWheeler,
                                contentDescription = null,
                                tint = if (!isCar) PrimaryBackground else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Bike",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (!isCar) PrimaryBackground else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "${inputs.manufacturer} ${inputs.model} ${inputs.variant}".trim(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ChampagneGold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${inputs.year} • ${inputs.fuelType} • ${inputs.transmission} • ${inputs.engineCapacityCc} cc",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun InputsControlCard(
    inputs: MaintenanceInputs,
    onOdometerChange: (Int) -> Unit,
    onMonthlyDistanceChange: (Float) -> Unit,
    onServiceTypeChange: (ServiceType) -> Unit,
    onRoadConditionChange: (RoadCondition) -> Unit,
    onDrivingStyleChange: (MaintenanceDrivingStyle) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RideWorthShapes.large,
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Vehicle Usage & Service Setup",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Current Odometer Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Current Odometer", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Text(text = "${inputs.currentOdometerKm} km", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ChampagneGold)
            }

            Slider(
                value = inputs.currentOdometerKm.toFloat(),
                onValueChange = { onOdometerChange(it.toInt()) },
                valueRange = 1000f..200000f,
                colors = SliderDefaults.colors(
                    thumbColor = ChampagneGold,
                    activeTrackColor = ChampagneGold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Monthly Driving Distance Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Monthly Driving Distance", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Text(text = "${inputs.monthlyDistanceKm.toInt()} km/mo", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ChampagneGold)
            }

            Slider(
                value = inputs.monthlyDistanceKm,
                onValueChange = { onMonthlyDistanceChange(it) },
                valueRange = 200f..5000f,
                colors = SliderDefaults.colors(
                    thumbColor = ChampagneGold,
                    activeTrackColor = ChampagneGold
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Service Partner Choice
            Text(text = "Service Partner", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ServiceType.values().forEach { st ->
                    val selected = inputs.serviceType == st
                    FilterChip(
                        selected = selected,
                        onClick = { onServiceTypeChange(st) },
                        label = { Text(st.displayName, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ChampagneGold,
                            selectedLabelColor = PrimaryBackground
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Road Condition Choice
            Text(text = "Road Condition", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RoadCondition.values().forEach { rc ->
                    val selected = inputs.roadCondition == rc
                    FilterChip(
                        selected = selected,
                        onClick = { onRoadConditionChange(rc) },
                        label = { Text(rc.displayName, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepSapphire,
                            selectedLabelColor = TextPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Driving Style Choice
            Text(text = "Driving Style", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MaintenanceDrivingStyle.values().forEach { ds ->
                    val selected = inputs.drivingStyle == ds
                    FilterChip(
                        selected = selected,
                        onClick = { onDrivingStyleChange(ds) },
                        label = { Text(ds.displayName, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WarningYellow,
                            selectedLabelColor = PrimaryBackground
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightsListSection(
    insights: List<String>,
    title: String = "Smart Ownership Insights"
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RideWorthShapes.large,
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            insights.forEach { insight ->
                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = insight,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryItemRow(
    item: MaintenanceHistoryItem,
    onReopen: () -> Unit,
    onDelete: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply { maximumFractionDigits = 0 } }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.vehicleName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${item.currentOdometerKm} km • Health ${item.healthScore}/100",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Est. ${currencyFormat.format(item.monthlyCost)}/mo (${currencyFormat.format(item.yearlyCost)}/yr)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = ChampagneGold
                )
            }

            Row {
                IconButton(onClick = onReopen) {
                    Icon(Icons.Default.OpenInNew, contentDescription = "Reopen", tint = ChampagneGold)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed)
                }
            }
        }
    }
}

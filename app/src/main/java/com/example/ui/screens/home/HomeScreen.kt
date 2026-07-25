package com.example.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ElectricCar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.models.SmartTip
import com.example.data.models.VehicleType
import com.example.ui.components.LuxuryPrimaryButton
import com.example.ui.components.ProFeatureBottomSheet
import com.example.ui.components.RideWorthProLockedCard
import com.example.ui.components.SectionTitle
import com.example.ui.components.TipDetailBottomSheet
import com.example.ui.components.ValuationPreviewBottomSheet
import com.example.ui.components.VehicleSelectionCard
import com.example.ui.theme.RideWorthShapes
import com.example.ui.viewmodel.HomeViewModel
import com.example.util.rememberAppHaptics

import com.example.garage.ui.components.AddVehicleSheet
import com.example.garage.ui.components.GarageHorizontalSection
import com.example.garage.ui.viewmodel.GarageViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    garageViewModel: GarageViewModel = viewModel(),
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToValuation: () -> Unit = {},
    onNavigateToCompare: () -> Unit = {},
    onNavigateToFuelCalculator: () -> Unit = {},
    onNavigateToMaintenancePlanner: () -> Unit = {},
    onNavigateToGarageDetail: (String) -> Unit = {},
    testTag: String = "home_screen"
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val garageUiState by garageViewModel.uiState.collectAsState()
    val haptics = rememberAppHaptics()

    LaunchedEffect(Unit) {
        garageViewModel.init(context)
    }

    val proSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val tipSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val valuationSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            Surface(
                color = PrimaryBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    ChampagneGold.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Surface(
                                shape = CircleShape,
                                color = ChampagneGold.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    ChampagneGold.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = ChampagneGold,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "RIDEWORTH AUTOMOTIVE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ChampagneGold,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = androidx.compose.ui.unit.TextUnit(1.5f, androidx.compose.ui.unit.TextUnitType.Sp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Fair Market Valuation",
                                style = MaterialTheme.typography.headlineLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = GlassSurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    GlassBorderHex
                                )
                            ) {
                                IconButton(
                                    onClick = {
                                        haptics.lightClick()
                                        onNavigateToHistory()
                                    },
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "History",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = GlassSurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    GlassBorderHex
                                )
                            ) {
                                IconButton(
                                    onClick = {
                                        haptics.lightClick()
                                        onNavigateToSettings()
                                    },
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = PrimaryBackground,
        modifier = Modifier.testTag(testTag)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // My Garage Horizontal Section
            GarageHorizontalSection(
                vehicles = garageUiState.vehicles,
                onAddVehicleClick = { garageViewModel.openAddVehicleSheet() },
                onVehicleClick = { vehicle -> onNavigateToGarageDetail(vehicle.id) },
                onValuationClick = { vehicle -> onNavigateToValuation() },
                onMaintenanceClick = { vehicle -> onNavigateToMaintenancePlanner() },
                onFuelClick = { vehicle -> onNavigateToFuelCalculator() },
                onCompareClick = { vehicle -> onNavigateToCompare() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Vehicle Category Selector (Side-by-Side Cards)
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                SectionTitle(
                    title = "Select Vehicle Category",
                    badgeText = uiState.selectedVehicleType.title.uppercase()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                VehicleSelectionCard(
                    vehicleType = VehicleType.CAR,
                    isSelected = uiState.selectedVehicleType == VehicleType.CAR,
                    onSelect = { viewModel.selectVehicleType(VehicleType.CAR) },
                    modifier = Modifier.weight(1f)
                )

                VehicleSelectionCard(
                    vehicleType = VehicleType.BIKE,
                    isSelected = uiState.selectedVehicleType == VehicleType.BIKE,
                    onSelect = { viewModel.selectVehicleType(VehicleType.BIKE) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Calculate Valuation Call-To-Action Button
            LuxuryPrimaryButton(
                text = "Calculate ${uiState.selectedVehicleType.title} Market Value",
                onClick = { 
                    viewModel.showValuationPreview()
                    onNavigateToValuation()
                },
                icon = Icons.Default.AutoAwesome,
                modifier = Modifier.fillMaxWidth(),
                testTag = "calculate_valuation_button"
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Today's Smart Tip Section
            SectionTitle(
                title = "Today's Smart Tip",
                icon = Icons.Default.Lightbulb
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(end = 12.dp)
            ) {
                items(uiState.smartTips) { tip ->
                    SmartTipCard(
                        tip = tip,
                        onClick = { viewModel.selectTip(tip) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // RideWorth Pro Locked Card Section
            SectionTitle(
                title = "RideWorth Pro",
                badgeText = "AI PREVIEW"
            )

            Spacer(modifier = Modifier.height(8.dp))

            RideWorthProLockedCard(
                onUnlockClick = { viewModel.showProSheet() },
                proFeatures = uiState.proFeatures
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Quick Actions Grid
            SectionTitle(title = "Quick Actions")

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                QuickActionCard(
                    title = "Compare Vehicles",
                    subtitle = "Side-by-side specs & value",
                    icon = Icons.Default.CompareArrows,
                    onClick = onNavigateToCompare,
                    modifier = Modifier.weight(1f)
                )

                QuickActionCard(
                    title = "Mileage Calculator",
                    subtitle = "Fuel cost & efficiency",
                    icon = Icons.Default.Calculate,
                    onClick = onNavigateToFuelCalculator,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            QuickActionCard(
                title = "Smart Maintenance Planner",
                subtitle = "Service schedule, 5-year cost & health score",
                icon = Icons.Default.Build,
                onClick = onNavigateToMaintenancePlanner,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Pro Feature Modal Bottom Sheet
    if (uiState.showProSheet) {
        ProFeatureBottomSheet(
            onDismissRequest = { viewModel.dismissProSheet() },
            sheetState = proSheetState
        )
    }

    // Smart Tip Detail Bottom Sheet
    if (uiState.selectedTipForDetail != null) {
        TipDetailBottomSheet(
            tip = uiState.selectedTipForDetail,
            onDismissRequest = { viewModel.dismissTipDetail() },
            sheetState = tipSheetState
        )
    }

    // Valuation Parameter Sliders Sheet
    if (uiState.showValuationPreviewSheet) {
        ValuationPreviewBottomSheet(
            vehicleType = uiState.selectedVehicleType,
            onDismissRequest = { viewModel.dismissValuationPreview() },
            sheetState = valuationSheetState
        )
    }

    // Add / Edit Garage Vehicle Sheet
    if (garageUiState.showAddVehicleSheet) {
        AddVehicleSheet(
            existingVehicle = garageUiState.editingVehicle,
            onDismiss = { garageViewModel.closeAddVehicleSheet() },
            onSave = { vehicle -> garageViewModel.saveVehicle(vehicle) }
        )
    }
}

@Composable
fun SmartTipCard(
    tip: SmartTip,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()

    Card(
        modifier = modifier
            .width(280.dp)
            .clip(RideWorthShapes.large)
            .border(
                1.dp,
                GlassBorderHex,
                RideWorthShapes.large
            )
            .clickable {
                haptics.lightClick()
                onClick()
            },
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = GlassSurface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Surface(
                shape = RideWorthShapes.extraSmall,
                color = ChampagneGold.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.3f))
            ) {
                Text(
                    text = tip.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = ChampagneGold,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = androidx.compose.ui.unit.TextUnit(1f, androidx.compose.ui.unit.TextUnitType.Sp),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = tip.title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tip.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 2,
                lineHeight = androidx.compose.ui.unit.TextUnit(18f, androidx.compose.ui.unit.TextUnitType.Sp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = tip.impact,
                    style = MaterialTheme.typography.labelLarge,
                    color = ChampagneGold,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()

    Card(
        modifier = modifier
            .clip(RideWorthShapes.large)
            .border(
                1.dp,
                SoftBorderColor,
                RideWorthShapes.large
            )
            .clickable {
                haptics.lightClick()
                onClick()
            },
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RideWorthShapes.small)
                    .background(ChampagneGold.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

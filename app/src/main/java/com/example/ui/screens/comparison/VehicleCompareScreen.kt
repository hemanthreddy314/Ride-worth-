package com.example.ui.screens.comparison

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.comparison.model.VehicleCatalogData
import com.example.comparison.model.VehicleTarget
import com.example.data.models.VehicleType
import com.example.ui.components.LuxuryPrimaryButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.BottomSheetType
import com.example.ui.viewmodel.VehicleCompareStep
import com.example.ui.viewmodel.VehicleCompareViewModel
import com.example.util.rememberAppHaptics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleCompareScreen(
    viewModel: VehicleCompareViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    testTag: String = "vehicle_compare_screen"
) {
    val context = LocalContext.current
    val haptics = rememberAppHaptics()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.initRepository(context)
    }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToastMessage()
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            CompareTopAppBar(
                title = if (uiState.step == VehicleCompareStep.SELECT_VEHICLES) "Vehicle Comparison" else "Comparison Report",
                onBackClick = {
                    haptics.lightClick()
                    if (uiState.step == VehicleCompareStep.REPORT) {
                        viewModel.setStep(VehicleCompareStep.SELECT_VEHICLES)
                    } else {
                        onNavigateBack()
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.step == VehicleCompareStep.REPORT) {
                CompareReportBottomBar(
                    isSaved = uiState.isSaved,
                    onSaveClick = {
                        haptics.heavyClick()
                        viewModel.saveComparison()
                    },
                    onShareClick = {
                        haptics.lightClick()
                        viewModel.shareComparison(context)
                    },
                    onNewCompareClick = {
                        haptics.lightClick()
                        viewModel.setStep(VehicleCompareStep.SELECT_VEHICLES)
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PrimaryBackground,
        modifier = Modifier.testTag(testTag)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.step) {
                VehicleCompareStep.SELECT_VEHICLES -> {
                    VehicleSelectionView(
                        uiState = uiState,
                        onOpenPicker = { target, type ->
                            haptics.lightClick()
                            viewModel.openPicker(target, type)
                        },
                        onSwapVehicles = {
                            haptics.heavyClick()
                            viewModel.swapVehicles()
                        },
                        onCompareClick = {
                            haptics.heavyClick()
                            viewModel.setStep(VehicleCompareStep.REPORT)
                        },
                        onVehicleTypeChange = { type ->
                            haptics.lightClick()
                            viewModel.updateVehicleType(type)
                        }
                    )
                }

                VehicleCompareStep.REPORT -> {
                    val result = uiState.comparisonResult
                    if (result != null) {
                        VehicleReportView(
                            result = result,
                            onSwapClick = {
                                haptics.heavyClick()
                                viewModel.swapVehicles()
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for Selection
    if (uiState.activePickerType != BottomSheetType.NONE) {
        val targetState = if (uiState.activePickerTarget == VehicleTarget.VEHICLE_A) uiState.vehicleAState else uiState.vehicleBState
        val isBike = targetState.vehicleType == VehicleType.BIKE

        val items = when (uiState.activePickerType) {
            BottomSheetType.BRAND -> VehicleCatalogData.getBrands(isBike)
            BottomSheetType.MODEL -> VehicleCatalogData.getModels(targetState.brand, isBike)
            BottomSheetType.VARIANT -> VehicleCatalogData.getVariants(targetState.model, isBike)
            BottomSheetType.MANUFACTURING_YEAR, BottomSheetType.REGISTRATION_YEAR -> VehicleCatalogData.years.map { it.toString() }
            BottomSheetType.FUEL_TYPE -> VehicleCatalogData.fuelTypes
            BottomSheetType.TRANSMISSION -> VehicleCatalogData.transmissions
            else -> emptyList()
        }

        val title = when (uiState.activePickerType) {
            BottomSheetType.BRAND -> "Select Brand"
            BottomSheetType.MODEL -> "Select Model"
            BottomSheetType.VARIANT -> "Select Variant"
            BottomSheetType.MANUFACTURING_YEAR, BottomSheetType.REGISTRATION_YEAR -> "Select Manufacturing Year"
            BottomSheetType.FUEL_TYPE -> "Select Fuel Type"
            BottomSheetType.TRANSMISSION -> "Select Transmission"
            else -> "Select Option"
        }

        SearchableSelectionSheet(
            title = title,
            searchQuery = uiState.searchQuery,
            onSearchQueryChange = { viewModel.setSearchQuery(it) },
            items = items,
            onItemSelected = { selected ->
                haptics.lightClick()
                when (uiState.activePickerType) {
                    BottomSheetType.BRAND -> viewModel.selectBrand(selected)
                    BottomSheetType.MODEL -> viewModel.selectModel(selected)
                    BottomSheetType.VARIANT -> viewModel.selectVariant(selected)
                    BottomSheetType.MANUFACTURING_YEAR, BottomSheetType.REGISTRATION_YEAR -> viewModel.selectYear(selected.toIntOrNull() ?: 2021)
                    BottomSheetType.FUEL_TYPE -> viewModel.selectFuelType(selected)
                    BottomSheetType.TRANSMISSION -> viewModel.selectTransmission(selected)
                    else -> viewModel.closePicker()
                }
            },
            onDismiss = { viewModel.closePicker() },
            sheetState = sheetState
        )
    }
}

@Composable
private fun VehicleSelectionView(
    uiState: com.example.ui.viewmodel.VehicleCompareUiState,
    onOpenPicker: (VehicleTarget, BottomSheetType) -> Unit,
    onSwapVehicles: () -> Unit,
    onCompareClick: () -> Unit,
    onVehicleTypeChange: (VehicleType) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Vehicle Type Selector Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCard, RideWorthShapes.medium)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val currentType = uiState.vehicleAState.vehicleType
                
                SegmentedTab(
                    title = "Cars",
                    icon = Icons.Default.DirectionsCar,
                    isSelected = currentType == VehicleType.CAR,
                    onClick = { onVehicleTypeChange(VehicleType.CAR) },
                    modifier = Modifier.weight(1f)
                )

                SegmentedTab(
                    title = "Bikes & Scooters",
                    icon = Icons.Default.TwoWheeler,
                    isSelected = currentType == VehicleType.BIKE,
                    onClick = { onVehicleTypeChange(VehicleType.BIKE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            VehicleSelectionCard(
                target = VehicleTarget.VEHICLE_A,
                formState = uiState.vehicleAState,
                onOpenPicker = { type -> onOpenPicker(VehicleTarget.VEHICLE_A, type) }
            )
        }

        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onSwapVehicles,
                    modifier = Modifier
                        .size(44.dp)
                        .background(SurfaceCard, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Swap Vehicles",
                        tint = ChampagneGold
                    )
                }
            }
        }

        item {
            VehicleSelectionCard(
                target = VehicleTarget.VEHICLE_B,
                formState = uiState.vehicleBState,
                onOpenPicker = { type -> onOpenPicker(VehicleTarget.VEHICLE_B, type) }
            )
        }

        if (uiState.sameVehicleError != null) {
            item {
                Text(
                    text = uiState.sameVehicleError,
                    style = MaterialTheme.typography.bodySmall,
                    color = ErrorRed,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            LuxuryPrimaryButton(
                text = "Generate Side-by-Side Report",
                onClick = onCompareClick,
                icon = Icons.Default.CompareArrows,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SegmentedTab(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RideWorthShapes.small,
        color = if (isSelected) ChampagneGold else Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) PrimaryBackground else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) PrimaryBackground else TextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun VehicleReportView(
    result: com.example.comparison.model.VehicleComparisonResult,
    onSwapClick: () -> Unit
) {
    val nameA = "${result.specA.formState.brand} ${result.specA.formState.model}".trim()
    val nameB = "${result.specB.formState.brand} ${result.specB.formState.model}".trim()

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                ComparisonHeader(
                    specA = result.specA,
                    specB = result.specB,
                    onSwapClick = onSwapClick
                )
            }

            item {
                WinnerSummaryCard(
                    winner = result.winnerSummary,
                    specA = result.specA,
                    specB = result.specB
                )
            }

            item {
                ValueForMoneyCard(
                    vfm = result.valueForMoney,
                    nameA = nameA,
                    nameB = nameB
                )
            }

            item {
                ComparisonTable(
                    rows = result.comparisonRows,
                    nameA = nameA,
                    nameB = nameB
                )
            }

            item {
                CategoryScoreboard(
                    scores = result.categoryScores,
                    nameA = nameA,
                    nameB = nameB
                )
            }

            item {
                MonthlyRunningCostCard(
                    cost = result.monthlyRunningCost,
                    nameA = nameA,
                    nameB = nameB
                )
            }

            item {
                SmartInsightsSection(
                    insights = result.smartInsights
                )
            }

            item {
                ProsConsCard(
                    prosCons = result.prosCons,
                    nameA = nameA,
                    nameB = nameB
                )
            }

            item {
                DifferenceSummaryCard(
                    summary = result.differenceSummary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompareTopAppBar(
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryBackground
        )
    )
}

@Composable
private fun CompareReportBottomBar(
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onNewCompareClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceCard,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onSaveClick,
                modifier = Modifier.background(SecondaryBackground, CircleShape)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Save Report",
                    tint = if (isSaved) ChampagneGold else TextPrimary
                )
            }

            IconButton(
                onClick = onShareClick,
                modifier = Modifier.background(SecondaryBackground, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share Report",
                    tint = TextPrimary
                )
            }

            Button(
                onClick = onNewCompareClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RideWorthShapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChampagneGold,
                    contentColor = PrimaryBackground
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "New Comparison",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

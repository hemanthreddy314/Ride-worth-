package com.example.fuelcalculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.fuelcalculator.ui.components.*
import com.example.fuelcalculator.ui.viewmodel.FuelCalculatorViewModel
import com.example.ui.screens.comparison.SearchableSelectionSheet
import com.example.ui.theme.*
import com.example.ui.viewmodel.BottomSheetType
import com.example.util.rememberAppHaptics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelCalculatorScreen(
    viewModel: FuelCalculatorViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToMaintenance: () -> Unit = {},
    testTag: String = "fuel_calculator_screen"
) {
    val context = LocalContext.current
    val haptics = rememberAppHaptics()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.initRepository(context)
    }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToastMessage()
        }
    }

    val tabs = listOf("Calculator", "Dashboard", "Trip Cost", "Compare Fuels", "History")

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(PrimaryBackground)) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Fuel & Mileage Intelligence",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            haptics.lightClick()
                            onNavigateBack()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBackground)
                )

                // Scrollable Tab Row
                ScrollableTabRow(
                    selectedTabIndex = uiState.selectedTab,
                    containerColor = PrimaryBackground,
                    contentColor = ChampagneGold,
                    edgePadding = 16.dp
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = uiState.selectedTab == index,
                            onClick = {
                                haptics.lightClick()
                                viewModel.selectTab(index)
                            },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (uiState.selectedTab == index) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (uiState.selectedTab == 1) { // Dashboard Tab
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
                            onClick = {
                                haptics.heavyClick()
                                viewModel.saveCalculation()
                            },
                            modifier = Modifier.background(SecondaryBackground, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (uiState.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save Record",
                                tint = if (uiState.isSaved) ChampagneGold else TextPrimary
                            )
                        }

                        Button(
                            onClick = {
                                haptics.lightClick()
                                viewModel.shareSummary(context)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RideWorthShapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ChampagneGold,
                                contentColor = PrimaryBackground
                            )
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Share Fuel Report",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
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
                .padding(horizontal = 20.dp)
        ) {
            when (uiState.selectedTab) {
                0 -> {
                    CalculatorInputTab(
                        inputs = uiState.inputs,
                        result = uiState.result,
                        onOpenPicker = { type ->
                            haptics.lightClick()
                            viewModel.openPicker(type)
                        },
                        onVehicleTypeChange = { type ->
                            haptics.lightClick()
                            viewModel.updateVehicleType(type)
                        },
                        onDailyDistanceChange = { viewModel.updateDailyDistance(it) },
                        onMonthlyDistanceChange = { viewModel.updateMonthlyDistance(it) },
                        onFuelPriceChange = { viewModel.updateFuelPrice(it) },
                        onAcUsageChange = { viewModel.updateAcUsage(it) },
                        onCityDrivingChange = { viewModel.updateCityDrivingPercent(it) },
                        onDrivingStyleChange = { viewModel.updateDrivingStyle(it) },
                        onTrafficConditionChange = { viewModel.updateTrafficCondition(it) },
                        onOfficialMileageChange = { viewModel.updateOfficialMileage(it) },
                        onTankCapacityChange = { viewModel.updateTankCapacity(it) },
                        onGenerateReportClick = {
                            haptics.heavyClick()
                            viewModel.selectTab(1) // Move to Dashboard
                        }
                    )
                }

                1 -> {
                    DashboardTab(
                        inputs = uiState.inputs,
                        result = uiState.result,
                        onSaveClick = {
                            haptics.heavyClick()
                            viewModel.saveCalculation()
                        },
                        onShareClick = {
                            haptics.lightClick()
                            viewModel.shareSummary(context)
                        },
                        isSaved = uiState.isSaved
                    )
                }

                2 -> {
                    TripCalculatorTab(
                        tripInputs = uiState.tripInputs,
                        tripResult = uiState.tripResult,
                        inputs = uiState.inputs,
                        onTripDistanceChange = { viewModel.updateTripDistance(it) },
                        onCustomPriceChange = { viewModel.updateTripCustomPrice(it) }
                    )
                }

                3 -> {
                    CompareFuelTypesTab(result = uiState.result)
                }

                4 -> {
                    HistoryTab(
                        records = uiState.savedRecords,
                        searchQuery = uiState.searchQuery,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onReopenRecord = { rec ->
                            haptics.lightClick()
                            viewModel.reopenRecord(rec)
                        },
                        onDeleteRecord = { id ->
                            haptics.heavyClick()
                            viewModel.deleteRecord(id)
                        }
                    )
                }
            }
        }
    }

    // Modal Bottom Sheet for Selection
    if (uiState.activePickerType != BottomSheetType.NONE) {
        val isBike = uiState.inputs.vehicleType == VehicleType.BIKE

        val items = when (uiState.activePickerType) {
            BottomSheetType.BRAND -> VehicleCatalogData.getBrands(isBike)
            BottomSheetType.MODEL -> VehicleCatalogData.getModels(uiState.inputs.brand, isBike)
            BottomSheetType.VARIANT -> VehicleCatalogData.getVariants(uiState.inputs.model, isBike)
            BottomSheetType.MANUFACTURING_YEAR, BottomSheetType.REGISTRATION_YEAR -> VehicleCatalogData.years.map { it.toString() }
            BottomSheetType.FUEL_TYPE -> VehicleCatalogData.fuelTypes
            else -> emptyList()
        }

        val title = when (uiState.activePickerType) {
            BottomSheetType.BRAND -> "Select Brand"
            BottomSheetType.MODEL -> "Select Model"
            BottomSheetType.VARIANT -> "Select Variant"
            BottomSheetType.MANUFACTURING_YEAR, BottomSheetType.REGISTRATION_YEAR -> "Select Manufacturing Year"
            BottomSheetType.FUEL_TYPE -> "Select Fuel Type"
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
                    BottomSheetType.MANUFACTURING_YEAR, BottomSheetType.REGISTRATION_YEAR -> viewModel.selectYear(selected.toIntOrNull() ?: 2022)
                    BottomSheetType.FUEL_TYPE -> viewModel.selectFuelType(selected)
                    else -> viewModel.closePicker()
                }
            },
            onDismiss = { viewModel.closePicker() },
            sheetState = sheetState
        )
    }
}

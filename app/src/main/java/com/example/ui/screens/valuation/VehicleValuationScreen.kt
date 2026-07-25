package com.example.ui.screens.valuation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AccidentStatus
import com.example.data.models.ConditionLevel
import com.example.data.models.EngineStatus
import com.example.data.models.InsuranceStatus
import com.example.data.models.OwnerType
import com.example.data.models.ServiceStatus
import com.example.data.models.TyreHealth
import com.example.data.models.VehicleType
import com.example.ui.components.LuxuryPrimaryButton
import com.example.ui.components.SectionTitle
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldDark
import com.example.ui.theme.DeepSapphire
import com.example.ui.theme.GlassBorderHex
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.RideWorthShapes
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SoftBorderColor
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.BottomSheetType
import com.example.ui.viewmodel.CalculatedValuationResult
import com.example.ui.viewmodel.ValuationViewModel
import com.example.util.AnimationSpecs
import com.example.util.rememberAppHaptics
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleValuationScreen(
    viewModel: ValuationViewModel,
    onNavigateBack: () -> Unit,
    onContinueToAiValuation: () -> Unit,
    testTag: String = "vehicle_valuation_screen"
) {
    val formState by viewModel.formState.collectAsState()
    val activeSheet by viewModel.activeSheet.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val valuationResult = viewModel.calculateValuation()
    val haptics = rememberAppHaptics()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            ValuationTopAppBar(
                vehicleType = formState.vehicleType,
                onBackClick = {
                    haptics.lightClick()
                    onNavigateBack()
                }
            )
        },
        bottomBar = {
            LiveValuationBottomBar(
                result = valuationResult,
                onContinueClick = {
                    haptics.heavyClick()
                    onContinueToAiValuation()
                }
            )
        },
        containerColor = PrimaryBackground,
        modifier = Modifier.testTag(testTag)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Step Indicator
            ValuationStepIndicator(currentStep = 1)

            Spacer(modifier = Modifier.height(16.dp))

            // Top Hero Card
            ValuationTopHeroCard(
                vehicleType = formState.vehicleType,
                brandName = formState.brand,
                modelName = formState.model,
                onToggleVehicleType = { newType ->
                    haptics.heavyClick()
                    viewModel.updateVehicleType(newType)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section 1: Vehicle Specifications (Dropdown Cards)
            SectionTitle(
                title = "Vehicle Specifications",
                badgeText = formState.vehicleType.title.uppercase()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Brand Selector
            ValuationDropdownCard(
                label = "BRAND",
                value = formState.brand,
                onClick = { viewModel.openBottomSheet(BottomSheetType.BRAND) },
                testTag = "brand_dropdown"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Model Selector
            ValuationDropdownCard(
                label = "MODEL",
                value = formState.model,
                onClick = { viewModel.openBottomSheet(BottomSheetType.MODEL) },
                testTag = "model_dropdown"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Variant Selector
            ValuationDropdownCard(
                label = "VARIANT / TRIM",
                value = formState.variant,
                onClick = { viewModel.openBottomSheet(BottomSheetType.VARIANT) },
                testTag = "variant_dropdown"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Fuel Type & Transmission Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ValuationDropdownCard(
                    label = "FUEL TYPE",
                    value = formState.fuelType,
                    onClick = { viewModel.openBottomSheet(BottomSheetType.FUEL_TYPE) },
                    modifier = Modifier.weight(1f),
                    testTag = "fuel_dropdown"
                )

                ValuationDropdownCard(
                    label = "TRANSMISSION",
                    value = formState.transmission,
                    onClick = { viewModel.openBottomSheet(BottomSheetType.TRANSMISSION) },
                    modifier = Modifier.weight(1f),
                    testTag = "transmission_dropdown"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body Type & Registration State Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ValuationDropdownCard(
                    label = "BODY TYPE",
                    value = formState.bodyType,
                    onClick = { viewModel.openBottomSheet(BottomSheetType.BODY_TYPE) },
                    modifier = Modifier.weight(1f),
                    testTag = "body_dropdown"
                )

                ValuationDropdownCard(
                    label = "REG. STATE",
                    value = formState.registrationState,
                    onClick = { viewModel.openBottomSheet(BottomSheetType.REGISTRATION_STATE) },
                    modifier = Modifier.weight(1f),
                    testTag = "state_dropdown"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Registration & Manufacturing Year Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ValuationDropdownCard(
                    label = "REGISTRATION YEAR",
                    value = "${formState.registrationYear}",
                    onClick = { viewModel.openBottomSheet(BottomSheetType.REGISTRATION_YEAR) },
                    modifier = Modifier.weight(1f),
                    testTag = "reg_year_dropdown"
                )

                ValuationDropdownCard(
                    label = "MFG. YEAR",
                    value = "${formState.manufacturingYear}",
                    onClick = { viewModel.openBottomSheet(BottomSheetType.MANUFACTURING_YEAR) },
                    modifier = Modifier.weight(1f),
                    testTag = "mfg_year_dropdown"
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Section 2: Glass Slider Cards
            SectionTitle(title = "Usage & Pricing Metrics")

            Spacer(modifier = Modifier.height(12.dp))

            // Vehicle Age Slider Card
            GlassSliderCard(
                title = "Vehicle Age",
                valueText = "${formState.vehicleAgeYears.toInt()} Years",
                value = formState.vehicleAgeYears,
                valueRange = 0f..20f,
                steps = 19,
                onValueChange = { viewModel.updateVehicleAge(it) },
                testTag = "age_slider"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Kilometers Driven Slider Card
            GlassSliderCard(
                title = "Kilometers Driven",
                valueText = "${formatNumberWithCommas(formState.kilometersDriven.toInt())} km",
                value = formState.kilometersDriven,
                valueRange = 1000f..200000f,
                steps = 199,
                onValueChange = { viewModel.updateKilometers(it) },
                testTag = "kms_slider"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Expected Asking Price Slider Card
            GlassSliderCard(
                title = "Expected Asking Price",
                valueText = formatCurrencyInLakhs(formState.expectedAskingPrice.toInt()),
                value = formState.expectedAskingPrice,
                valueRange = 50000f..15000000f,
                steps = 299,
                onValueChange = { viewModel.updateAskingPrice(it) },
                testTag = "price_slider"
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Section 3: Ownership Cards
            SectionTitle(title = "Ownership History")

            Spacer(modifier = Modifier.height(12.dp))

            OwnerTypeSelectionGrid(
                selectedOwner = formState.ownerType,
                onSelectOwner = { owner ->
                    haptics.lightClick()
                    viewModel.updateOwnerType(owner)
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Section 4: Condition Score Cards
            SectionTitle(title = "Overall Vehicle Condition")

            Spacer(modifier = Modifier.height(12.dp))

            ConditionScoreCardsGroup(
                selectedCondition = formState.conditionLevel,
                onSelectCondition = { condition ->
                    haptics.heavyClick()
                    viewModel.updateConditionLevel(condition)
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Section 5: Component Health & Detailed Ratings
            SectionTitle(title = "Detailed Health Ratings")

            Spacer(modifier = Modifier.height(12.dp))

            // Accident History
            SegmentedChoiceGroup(
                title = "Accident History",
                options = AccidentStatus.values().map { it.label },
                selectedIndex = AccidentStatus.values().indexOf(formState.accidentHistory),
                onSelectIndex = { idx ->
                    haptics.lightClick()
                    viewModel.updateAccidentHistory(AccidentStatus.values()[idx])
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Service History
            SegmentedChoiceGroup(
                title = "Service Records",
                options = ServiceStatus.values().map { it.label },
                selectedIndex = ServiceStatus.values().indexOf(formState.serviceHistory),
                onSelectIndex = { idx ->
                    haptics.lightClick()
                    viewModel.updateServiceHistory(ServiceStatus.values()[idx])
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Insurance Status
            SegmentedChoiceGroup(
                title = "Insurance Status",
                options = InsuranceStatus.values().map { it.label },
                selectedIndex = InsuranceStatus.values().indexOf(formState.insuranceStatus),
                onSelectIndex = { idx ->
                    haptics.lightClick()
                    viewModel.updateInsuranceStatus(InsuranceStatus.values()[idx])
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tyres Condition
            SegmentedChoiceGroup(
                title = "Tyres Condition",
                options = TyreHealth.values().map { it.label },
                selectedIndex = TyreHealth.values().indexOf(formState.tyreHealth),
                onSelectIndex = { idx ->
                    haptics.lightClick()
                    viewModel.updateTyreHealth(TyreHealth.values()[idx])
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Engine Health
            SegmentedChoiceGroup(
                title = "Engine Health",
                options = EngineStatus.values().map { it.label },
                selectedIndex = EngineStatus.values().indexOf(formState.engineStatus),
                onSelectIndex = { idx ->
                    haptics.lightClick()
                    viewModel.updateEngineStatus(EngineStatus.values()[idx])
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Interior Condition
            SegmentedChoiceGroup(
                title = "Interior Condition",
                options = ConditionLevel.values().map { it.label },
                selectedIndex = ConditionLevel.values().indexOf(formState.interiorCondition),
                onSelectIndex = { idx ->
                    haptics.lightClick()
                    viewModel.updateInteriorCondition(ConditionLevel.values()[idx])
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Exterior Condition
            SegmentedChoiceGroup(
                title = "Exterior Condition",
                options = ConditionLevel.values().map { it.label },
                selectedIndex = ConditionLevel.values().indexOf(formState.exteriorCondition),
                onSelectIndex = { idx ->
                    haptics.lightClick()
                    viewModel.updateExteriorCondition(ConditionLevel.values()[idx])
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Smart Buyer Tip Card
            SmartTipBannerCard(tipMessage = valuationResult.smartTip)

            Spacer(modifier = Modifier.height(100.dp)) // Extra space for pinned bottom bar
        }
    }

    // Modal Bottom Sheets for Dropdown Selection & Year Pickers
    if (activeSheet != BottomSheetType.NONE) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeBottomSheet() },
            sheetState = sheetState,
            containerColor = SecondaryBackground,
            scrimColor = Color.Black.copy(alpha = 0.65f),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            when (activeSheet) {
                BottomSheetType.BRAND -> {
                    val items = if (formState.vehicleType == VehicleType.CAR) viewModel.carBrands else viewModel.bikeBrands
                    SearchableListSheet(
                        title = "Select ${formState.vehicleType.title} Brand",
                        items = items,
                        selectedItem = formState.brand,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.updateSearchQuery(it) },
                        onSelectItem = { viewModel.selectBrand(it) }
                    )
                }

                BottomSheetType.MODEL -> {
                    val items = viewModel.getModelsForBrand(formState.brand)
                    SearchableListSheet(
                        title = "Select ${formState.brand} Model",
                        items = items,
                        selectedItem = formState.model,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.updateSearchQuery(it) },
                        onSelectItem = { viewModel.selectModel(it) }
                    )
                }

                BottomSheetType.VARIANT -> {
                    val items = listOf("Base Standard", "VXi / Mid Trim", "ZXi / Top Trim", "ZX Dual Tone", "Luxury Spec", "Special Edition")
                    SearchableListSheet(
                        title = "Select Variant / Trim",
                        items = items,
                        selectedItem = formState.variant,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.updateSearchQuery(it) },
                        onSelectItem = { viewModel.selectVariant(it) }
                    )
                }

                BottomSheetType.FUEL_TYPE -> {
                    SearchableListSheet(
                        title = "Select Fuel Type",
                        items = viewModel.fuelTypes,
                        selectedItem = formState.fuelType,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.updateSearchQuery(it) },
                        onSelectItem = { viewModel.selectFuelType(it) }
                    )
                }

                BottomSheetType.TRANSMISSION -> {
                    val items = if (formState.vehicleType == VehicleType.CAR) viewModel.carTransmissions else viewModel.bikeTransmissions
                    SearchableListSheet(
                        title = "Select Transmission",
                        items = items,
                        selectedItem = formState.transmission,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.updateSearchQuery(it) },
                        onSelectItem = { viewModel.selectTransmission(it) }
                    )
                }

                BottomSheetType.BODY_TYPE -> {
                    val items = if (formState.vehicleType == VehicleType.CAR) viewModel.carBodyTypes else viewModel.bikeBodyTypes
                    SearchableListSheet(
                        title = "Select Body Type",
                        items = items,
                        selectedItem = formState.bodyType,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.updateSearchQuery(it) },
                        onSelectItem = { viewModel.selectBodyType(it) }
                    )
                }

                BottomSheetType.REGISTRATION_STATE -> {
                    SearchableListSheet(
                        title = "Select Registration State",
                        items = viewModel.states,
                        selectedItem = formState.registrationState,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.updateSearchQuery(it) },
                        onSelectItem = { viewModel.selectRegistrationState(it) }
                    )
                }

                BottomSheetType.REGISTRATION_YEAR -> {
                    YearWheelPickerSheet(
                        title = "Select Registration Year",
                        years = viewModel.years,
                        selectedYear = formState.registrationYear,
                        onSelectYear = { viewModel.selectRegistrationYear(it) }
                    )
                }

                BottomSheetType.MANUFACTURING_YEAR -> {
                    YearWheelPickerSheet(
                        title = "Select Manufacturing Year",
                        years = viewModel.years,
                        selectedYear = formState.manufacturingYear,
                        onSelectYear = { viewModel.selectManufacturingYear(it) }
                    )
                }

                else -> {}
            }
        }
    }
}

// Top Bar with Back Button & Title
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValuationTopAppBar(
    vehicleType: VehicleType,
    onBackClick: () -> Unit
) {
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = GlassSurface,
                    border = BorderStroke(1.dp, GlassBorderHex)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Vehicle Valuation",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Configure details to estimate market worth",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

// Premium Step Progress Indicator
@Composable
fun ValuationStepIndicator(currentStep: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.medium)
            .border(1.dp, SoftBorderColor, RideWorthShapes.medium),
        shape = RideWorthShapes.medium,
        colors = CardDefaults.cardColors(containerColor = GlassSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Step 1: Vehicle (Active)
            StepBadge(
                stepNumber = "1",
                label = "Vehicle",
                isActive = currentStep >= 1,
                isCompleted = false
            )

            StepConnectorLine(isActive = currentStep >= 2)

            // Step 2: Condition
            StepBadge(
                stepNumber = "2",
                label = "Condition",
                isActive = currentStep >= 2,
                isCompleted = false
            )

            StepConnectorLine(isActive = currentStep >= 3)

            // Step 3: Result
            StepBadge(
                stepNumber = "3",
                label = "Result",
                isActive = currentStep >= 3,
                isCompleted = false
            )
        }
    }
}

@Composable
fun StepBadge(
    stepNumber: String,
    label: String,
    isActive: Boolean,
    isCompleted: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) ChampagneGold else SurfaceCard
                )
                .border(
                    width = 1.dp,
                    color = if (isActive) ChampagneGoldDark else SoftBorderColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                style = MaterialTheme.typography.labelMedium,
                color = if (isActive) PrimaryBackground else TextSecondary,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (isActive) ChampagneGold else TextSecondary,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun StepConnectorLine(isActive: Boolean) {
    Box(
        modifier = Modifier
            .width(28.dp)
            .height(2.dp)
            .background(
                if (isActive) ChampagneGold else SoftBorderColor
            )
    )
}

// Top Hero Card
@Composable
fun ValuationTopHeroCard(
    vehicleType: VehicleType,
    brandName: String,
    modelName: String,
    onToggleVehicleType: (VehicleType) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .border(
                BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.35f)),
                RideWorthShapes.large
            ),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = GlassSurface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ChampagneGold.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Badge Pill
                    Surface(
                        shape = CircleShape,
                        color = ChampagneGold.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (vehicleType == VehicleType.CAR) Icons.Default.DirectionsCar else Icons.Default.TwoWheeler,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${vehicleType.title.uppercase()} SELECTION",
                                style = MaterialTheme.typography.labelMedium,
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Toggle Car / Bike button
                    Surface(
                        shape = RideWorthShapes.small,
                        color = SurfaceCard,
                        border = BorderStroke(1.dp, SoftBorderColor),
                        modifier = Modifier.clickable {
                            val nextType = if (vehicleType == VehicleType.CAR) VehicleType.BIKE else VehicleType.CAR
                            onToggleVehicleType(nextType)
                        }
                    ) {
                        Text(
                            text = if (vehicleType == VehicleType.CAR) "Switch to Bike" else "Switch to Car",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Tell us about your $brandName $modelName.",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "We will estimate its true market value based on live resale parameters.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

// Custom Glass Dropdown Field Card
@Composable
fun ValuationDropdownCard(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.medium)
            .border(1.dp, SoftBorderColor, RideWorthShapes.medium)
            .clickable { onClick() }
            .testTag(testTag),
        shape = RideWorthShapes.medium,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = ChampagneGold,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(GlassSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.UnfoldMore,
                    contentDescription = "Select",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// Glass Slider Card with Spring Animation & Value Bubble
@Composable
fun GlassSliderCard(
    title: String,
    valueText: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
    testTag: String = ""
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .border(1.dp, SoftBorderColor, RideWorthShapes.large)
            .testTag(testTag),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = GlassSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                // Floating Value Bubble
                Surface(
                    shape = CircleShape,
                    color = ChampagneGold.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = valueText,
                        style = MaterialTheme.typography.labelLarge,
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                colors = SliderDefaults.colors(
                    thumbColor = ChampagneGold,
                    activeTrackColor = ChampagneGold,
                    inactiveTrackColor = SurfaceCard,
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                )
            )
        }
    }
}

// Ownership Interactive Cards Grid
@Composable
fun OwnerTypeSelectionGrid(
    selectedOwner: OwnerType,
    onSelectOwner: (OwnerType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OwnerType.values().forEach { owner ->
            val isSelected = owner == selectedOwner
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RideWorthShapes.medium)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) ChampagneGold else SoftBorderColor,
                        shape = RideWorthShapes.medium
                    )
                    .clickable { onSelectOwner(owner) },
                shape = RideWorthShapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) ChampagneGold.copy(alpha = 0.15f) else SurfaceCard
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = owner.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected) ChampagneGold else TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = owner.badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// Overall Condition Interactive Score Cards
@Composable
fun ConditionScoreCardsGroup(
    selectedCondition: ConditionLevel,
    onSelectCondition: (ConditionLevel) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ConditionLevel.values().forEach { cond ->
            val isSelected = cond == selectedCondition
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RideWorthShapes.medium)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) ChampagneGold else SoftBorderColor,
                        shape = RideWorthShapes.medium
                    )
                    .clickable { onSelectCondition(cond) },
                shape = RideWorthShapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) ChampagneGold.copy(alpha = 0.12f) else SurfaceCard
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) ChampagneGold.copy(alpha = 0.2f) else GlassSurface
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.Verified else Icons.Default.Star,
                                contentDescription = null,
                                tint = if (isSelected) ChampagneGold else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = cond.label,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isSelected) ChampagneGold else TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = cond.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Score Badge
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) ChampagneGold else GlassSurface,
                        border = BorderStroke(1.dp, if (isSelected) ChampagneGoldDark else SoftBorderColor)
                    ) {
                        Text(
                            text = "${cond.score} Pts",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) PrimaryBackground else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

// Segmented Choice Cards Row
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SegmentedChoiceGroup(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = index == selectedIndex
                Surface(
                    shape = RideWorthShapes.small,
                    color = if (isSelected) ChampagneGold.copy(alpha = 0.15f) else SurfaceCard,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) ChampagneGold else SoftBorderColor
                    ),
                    modifier = Modifier.clickable { onSelectIndex(index) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) ChampagneGold else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

// Contextual Smart Tip Banner Card
@Composable
fun SmartTipBannerCard(tipMessage: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.medium)
            .border(
                BorderStroke(1.dp, DeepSapphire.copy(alpha = 0.4f)),
                RideWorthShapes.medium
            ),
        shape = RideWorthShapes.medium,
        colors = CardDefaults.cardColors(containerColor = GlassSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            DeepSapphire.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(DeepSapphire.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "VALUATION INSIGHT TIP",
                    style = MaterialTheme.typography.labelSmall,
                    color = DeepSapphire,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tipMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
            }
        }
    }
}

// Pinned Bottom Live Valuation Bar
@Composable
fun LiveValuationBottomBar(
    result: CalculatedValuationResult,
    onContinueClick: () -> Unit
) {
    Surface(
        color = SecondaryBackground,
        border = BorderStroke(1.dp, GlassBorderHex),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ESTIMATED MARKET VALUE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${formatCurrencyInLakhs(result.estimatedLow.toInt())} - ${formatCurrencyInLakhs(result.estimatedHigh.toInt())}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = SuccessGreen.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = "${result.confidenceScore}% Confidence",
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = GlassSurface,
                        border = BorderStroke(1.dp, SoftBorderColor)
                    ) {
                        Text(
                            text = "${result.conditionScore}/100 Score",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LuxuryPrimaryButton(
                text = "Continue to AI Valuation",
                onClick = onContinueClick,
                icon = Icons.Default.AutoAwesome,
                modifier = Modifier.fillMaxWidth(),
                testTag = "continue_ai_valuation_button"
            )
        }
    }
}

// Searchable List Bottom Sheet
@Composable
fun SearchableListSheet(
    title: String,
    items: List<String>,
    selectedItem: String,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSelectItem: (String) -> Unit
) {
    val haptics = rememberAppHaptics()
    val filteredItems = remember(items, searchQuery) {
        if (searchQuery.isBlank()) items
        else items.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search...", color = TextSecondary) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = ChampagneGold
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = TextSecondary
                        )
                    }
                }
            },
            singleLine = true,
            shape = RideWorthShapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChampagneGold,
                unfocusedBorderColor = SoftBorderColor,
                focusedContainerColor = SurfaceCard,
                unfocusedContainerColor = SurfaceCard,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredItems) { item ->
                val isSelected = item == selectedItem
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RideWorthShapes.small)
                        .border(
                            1.dp,
                            if (isSelected) ChampagneGold else SoftBorderColor,
                            RideWorthShapes.small
                        )
                        .clickable {
                            haptics.lightClick()
                            onSelectItem(item)
                        },
                    shape = RideWorthShapes.small,
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) ChampagneGold.copy(alpha = 0.15f) else SurfaceCard
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isSelected) ChampagneGold else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = ChampagneGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Year Wheel Picker Bottom Sheet
@Composable
fun YearWheelPickerSheet(
    title: String,
    years: List<Int>,
    selectedYear: Int,
    onSelectYear: (Int) -> Unit
) {
    val haptics = rememberAppHaptics()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(years) { year ->
                val isSelected = year == selectedYear
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RideWorthShapes.medium)
                        .border(
                            1.dp,
                            if (isSelected) ChampagneGold else SoftBorderColor,
                            RideWorthShapes.medium
                        )
                        .clickable {
                            haptics.lightClick()
                            onSelectYear(year)
                        },
                    shape = RideWorthShapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) ChampagneGold.copy(alpha = 0.15f) else SurfaceCard
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$year",
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (isSelected) ChampagneGold else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper formatting functions
fun formatNumberWithCommas(number: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return formatter.format(number)
}

fun formatCurrencyInLakhs(amount: Int): String {
    return when {
        amount >= 10000000 -> {
            val crores = amount / 10000000.0
            String.format(Locale.ENGLISH, "₹%.2f Cr", crores)
        }
        amount >= 100000 -> {
            val lakhs = amount / 100000.0
            String.format(Locale.ENGLISH, "₹%.2f Lakhs", lakhs)
        }
        else -> {
            "₹" + formatNumberWithCommas(amount)
        }
    }
}

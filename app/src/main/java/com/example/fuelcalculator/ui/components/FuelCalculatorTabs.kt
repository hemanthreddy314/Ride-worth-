package com.example.fuelcalculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.VehicleType
import com.example.fuelcalculator.engine.FuelCalculatorEngine
import com.example.fuelcalculator.model.*
import com.example.fuelcalculator.ui.viewmodel.FuelCalculatorUiState
import com.example.ui.components.LuxuryPrimaryButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.BottomSheetType
import com.example.util.Formatters

@Composable
fun CalculatorInputTab(
    inputs: FuelCalculatorInputs,
    result: FuelCalculatorResult,
    onOpenPicker: (BottomSheetType) -> Unit,
    onVehicleTypeChange: (VehicleType) -> Unit,
    onDailyDistanceChange: (Float) -> Unit,
    onMonthlyDistanceChange: (Float) -> Unit,
    onFuelPriceChange: (Double) -> Unit,
    onAcUsageChange: (Float) -> Unit,
    onCityDrivingChange: (Float) -> Unit,
    onDrivingStyleChange: (DrivingStyle) -> Unit,
    onTrafficConditionChange: (TrafficCondition) -> Unit,
    onOfficialMileageChange: (Float) -> Unit,
    onTankCapacityChange: (Float) -> Unit,
    onGenerateReportClick: () -> Unit
) {
    val isBike = inputs.vehicleType == VehicleType.BIKE
    val unitSymbol = FuelCalculatorEngine.getFuelUnit(inputs.fuelType).symbol

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Vehicle Category Switch
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCard, RideWorthShapes.medium)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SegmentedTab(
                    title = "Car",
                    icon = Icons.Default.DirectionsCar,
                    isSelected = !isBike,
                    onClick = { onVehicleTypeChange(VehicleType.CAR) },
                    modifier = Modifier.weight(1f)
                )

                SegmentedTab(
                    title = "Bike / Scooter",
                    icon = Icons.Default.TwoWheeler,
                    isSelected = isBike,
                    onClick = { onVehicleTypeChange(VehicleType.BIKE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // STEP 1: Select Vehicle
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RideWorthShapes.large,
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
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
                            text = "1. Vehicle Details",
                            style = MaterialTheme.typography.titleMedium,
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            color = ChampagneGold.copy(alpha = 0.15f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = inputs.fuelType.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SelectionDropdownField(
                            label = "Brand / Make",
                            value = inputs.brand.ifBlank { "Select Brand" },
                            onClick = { onOpenPicker(BottomSheetType.BRAND) },
                            modifier = Modifier.weight(1f)
                        )

                        SelectionDropdownField(
                            label = "Model",
                            value = inputs.model.ifBlank { "Select Model" },
                            onClick = { onOpenPicker(BottomSheetType.MODEL) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SelectionDropdownField(
                            label = "Variant",
                            value = inputs.variant.ifBlank { "Select Variant" },
                            onClick = { onOpenPicker(BottomSheetType.VARIANT) },
                            modifier = Modifier.weight(1.2f)
                        )

                        SelectionDropdownField(
                            label = "Fuel Type",
                            value = inputs.fuelType,
                            onClick = { onOpenPicker(BottomSheetType.FUEL_TYPE) },
                            modifier = Modifier.weight(0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(color = DividerColor)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Official Mileage (km/$unitSymbol)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = inputs.officialMileage.toString(),
                                onValueChange = { str ->
                                    str.toFloatOrNull()?.let { onOfficialMileageChange(it) }
                                },
                                singleLine = true,
                                shape = RideWorthShapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ChampagneGold,
                                    unfocusedBorderColor = GlassBorderHex,
                                    focusedContainerColor = SecondaryBackground,
                                    unfocusedContainerColor = SecondaryBackground,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Tank Capacity ($unitSymbol)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = inputs.tankCapacity.toString(),
                                onValueChange = { str ->
                                    str.toFloatOrNull()?.let { onTankCapacityChange(it) }
                                },
                                singleLine = true,
                                shape = RideWorthShapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ChampagneGold,
                                    unfocusedBorderColor = GlassBorderHex,
                                    focusedContainerColor = SecondaryBackground,
                                    unfocusedContainerColor = SecondaryBackground,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // STEP 2: Commute & Driving Parameters
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RideWorthShapes.large,
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "2. Driving & Commute Inputs",
                        style = MaterialTheme.typography.titleMedium,
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Daily Distance Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Daily Commute Distance", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
                        Text(text = "${inputs.dailyDistanceKm.toInt()} km / day", style = MaterialTheme.typography.titleSmall, color = ChampagneGold, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = inputs.dailyDistanceKm,
                        onValueChange = onDailyDistanceChange,
                        valueRange = 1f..150f,
                        colors = SliderDefaults.colors(thumbColor = ChampagneGold, activeTrackColor = ChampagneGold)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Fuel Price Input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Current Fuel Price (₹ / $unitSymbol)", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
                        Text(text = "₹${String.format("%.2f", inputs.fuelPrice)}", style = MaterialTheme.typography.titleSmall, color = ChampagneGold, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = inputs.fuelPrice.toFloat(),
                        onValueChange = { onFuelPriceChange(it.toDouble()) },
                        valueRange = 50f..150f,
                        colors = SliderDefaults.colors(thumbColor = ChampagneGold, activeTrackColor = ChampagneGold)
                    )

                    if (!isBike) {
                        Spacer(modifier = Modifier.height(10.dp))
                        // AC Usage Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Air Conditioner Usage", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
                            Text(text = "${inputs.acUsagePercent.toInt()}%", style = MaterialTheme.typography.titleSmall, color = DeepSapphire, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = inputs.acUsagePercent,
                            onValueChange = onAcUsageChange,
                            valueRange = 0f..100f,
                            colors = SliderDefaults.colors(thumbColor = DeepSapphire, activeTrackColor = DeepSapphire)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // City vs Highway Split Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "City Driving Ratio", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
                        Text(
                            text = "${inputs.cityDrivingPercent.toInt()}% City / ${(100 - inputs.cityDrivingPercent).toInt()}% Hwy",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                    Slider(
                        value = inputs.cityDrivingPercent,
                        onValueChange = onCityDrivingChange,
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = ChampagneGold, activeTrackColor = ChampagneGold)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Driving Style Selector
                    Text(text = "Driving Style", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DrivingStyle.values().forEach { style ->
                            val isSelected = inputs.drivingStyle == style
                            Surface(
                                onClick = { onDrivingStyleChange(style) },
                                modifier = Modifier.weight(1f),
                                shape = RideWorthShapes.small,
                                color = if (isSelected) ChampagneGold else SecondaryBackground,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ChampagneGold else GlassBorderHex)
                            ) {
                                Text(
                                    text = style.label.split(" ").first(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) PrimaryBackground else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier
                                        .padding(vertical = 10.dp)
                                        .wrapContentWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Traffic Condition Selector
                    Text(text = "Traffic Conditions", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TrafficCondition.values().forEach { traffic ->
                            val isSelected = inputs.trafficCondition == traffic
                            Surface(
                                onClick = { onTrafficConditionChange(traffic) },
                                modifier = Modifier.weight(1f),
                                shape = RideWorthShapes.small,
                                color = if (isSelected) DeepSapphire else SecondaryBackground,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) DeepSapphire else GlassBorderHex)
                            ) {
                                Text(
                                    text = traffic.label.split(" ").first(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) TextPrimary else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier
                                        .padding(vertical = 10.dp)
                                        .wrapContentWidth()
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            LuxuryPrimaryButton(
                text = "Calculate Fuel Dashboard",
                onClick = onGenerateReportClick,
                icon = Icons.Default.Calculate,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DashboardTab(
    inputs: FuelCalculatorInputs,
    result: FuelCalculatorResult,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    isSaved: Boolean
) {
    val unitSymbol = FuelCalculatorEngine.getFuelUnit(inputs.fuelType).symbol

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Monthly Fuel Expense Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RideWorthShapes.large,
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = ChampagneGold.copy(alpha = 0.15f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "ESTIMATED MONTHLY FUEL COST",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = Formatters.formatIndianRupees(result.monthlyFuelCost.toLong()),
                        style = MaterialTheme.typography.displayMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cost per KM: ₹${String.format("%.2f", result.costPerKm)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = ChampagneGold,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(text = "•", color = TextSecondary)
                        Text(
                            text = "Real Mileage: ${String.format("%.1f", result.adjustedMileage)} km/$unitSymbol",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Efficiency Gauge Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RideWorthShapes.large,
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Fuel Efficiency Score",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Real-world vs official claimed mileage (${inputs.officialMileage} km/$unitSymbol)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    EfficiencyGauge(
                        score = result.efficiencyScore,
                        category = result.efficiencyCategory
                    )
                }
            }
        }

        // Fuel Tank & Usage Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RideWorthShapes.large,
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
            ) {
                Box(modifier = Modifier.padding(18.dp)) {
                    FuelTankIndicator(
                        refillsPerMonth = result.monthlyRefillCount,
                        unitSymbol = unitSymbol,
                        tankCapacity = inputs.tankCapacity,
                        fuelRequiredMonth = result.fuelRequiredPerMonth
                    )
                }
            }
        }

        // Yearly Breakdown Visual
        item {
            YearlyCostVisualCard(
                daily = result.dailyFuelCost,
                weekly = result.weeklyFuelCost,
                monthly = result.monthlyFuelCost,
                yearly = result.yearlyFuelCost
            )
        }

        // Smart Insights
        if (result.smartInsights.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RideWorthShapes.large,
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Smart Driving Insights", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        result.smartInsights.forEach { insight ->
                            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(text = "•", color = ChampagneGold, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
                                Text(text = insight, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }

        // Smart Tips
        if (result.smartTips.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RideWorthShapes.large,
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Efficiency Recommendations", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        result.smartTips.forEach { tip ->
                            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = tip, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TripCalculatorTab(
    tripInputs: TripInputs,
    tripResult: TripResult,
    inputs: FuelCalculatorInputs,
    onTripDistanceChange: (Float) -> Unit,
    onCustomPriceChange: (Double?) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RideWorthShapes.large,
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Trip Cost Estimator",
                        style = MaterialTheme.typography.titleMedium,
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Trip Distance", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
                        Text(text = "${tripInputs.tripDistanceKm.toInt()} km", style = MaterialTheme.typography.titleSmall, color = ChampagneGold, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = tripInputs.tripDistanceKm,
                        onValueChange = onTripDistanceChange,
                        valueRange = 10f..1500f,
                        colors = SliderDefaults.colors(thumbColor = ChampagneGold, activeTrackColor = ChampagneGold)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = (tripInputs.customPrice ?: inputs.fuelPrice).toString(),
                        onValueChange = { str -> onCustomPriceChange(str.toDoubleOrNull()) },
                        label = { Text("Fuel Price (₹ / ${tripResult.fuelUnitSymbol})", color = TextSecondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RideWorthShapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChampagneGold,
                            unfocusedBorderColor = GlassBorderHex,
                            focusedContainerColor = SecondaryBackground,
                            unfocusedContainerColor = SecondaryBackground,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            }
        }

        // Trip Result Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RideWorthShapes.large,
                colors = CardDefaults.cardColors(containerColor = GlassSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ESTIMATED TRIP COST",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = Formatters.formatIndianRupees(tripResult.estimatedCost.toLong()),
                        style = MaterialTheme.typography.displayMedium,
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    HorizontalDivider(color = GlassBorderHex)

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        TripStatItem(
                            label = "Fuel Needed",
                            value = "${String.format("%.1f", tripResult.fuelNeeded)} ${tripResult.fuelUnitSymbol}"
                        )

                        TripStatItem(
                            label = "Cost / KM",
                            value = "₹${String.format("%.2f", tripResult.costPerKm)}"
                        )

                        TripStatItem(
                            label = "Est. Drive Time",
                            value = "${tripResult.drivingTimeHours}h ${tripResult.drivingTimeMinutes}m"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TripStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CompareFuelTypesTab(
    result: FuelCalculatorResult
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Fuel Fuel Type Cost Comparison",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Estimated running costs for your commute distance across fuel variants.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        items(result.fuelTypeComparisons) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RideWorthShapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = if (item.isCurrent) ChampagneGold.copy(alpha = 0.12f) else SurfaceCard
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (item.isBestValue) SuccessGreen else if (item.isCurrent) ChampagneGold else GlassBorderHex
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.fuelType,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )

                            if (item.isCurrent) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = ChampagneGold.copy(alpha = 0.2f),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "CURRENT",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ChampagneGold,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            if (item.isBestValue) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = SuccessGreen.copy(alpha = 0.2f),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "BEST VALUE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = Formatters.formatIndianRupees(item.monthlyCost.toLong()) + " / mo",
                            style = MaterialTheme.typography.titleMedium,
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Price: ₹${item.pricePerUnit}/${item.unit}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Text(
                            text = "Est. Mileage: ${String.format("%.1f", item.estimatedMileage)} km/${item.unit}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Text(
                            text = "Cost/KM: ₹${String.format("%.2f", item.costPerKm)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (item.yearlySavingsVersusCurrent > 500 && !item.isCurrent) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "💡 Potential Savings: Save ~${Formatters.formatIndianRupees(item.yearlySavingsVersusCurrent.toLong())} / year vs current fuel!",
                            style = MaterialTheme.typography.bodySmall,
                            color = SuccessGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryTab(
    records: List<SavedFuelRecord>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onReopenRecord: (SavedFuelRecord) -> Unit,
    onDeleteRecord: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(vertical = 16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search saved fuel records...", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
            singleLine = true,
            shape = RideWorthShapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChampagneGold,
                unfocusedBorderColor = GlassBorderHex,
                focusedContainerColor = SecondaryBackground,
                unfocusedContainerColor = SecondaryBackground,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        val filtered = if (searchQuery.isBlank()) records else records.filter {
            it.vehicleName.contains(searchQuery, ignoreCase = true) || it.fuelType.contains(searchQuery, ignoreCase = true)
        }

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (records.isEmpty()) "No saved calculations yet." else "No matching records.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RideWorthShapes.medium)
                            .clickable { onReopenRecord(record) },
                        shape = RideWorthShapes.medium,
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = record.vehicleName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "${record.fuelType} • ${record.dailyKm.toInt()} km/day • Real: ${String.format("%.1f", record.adjustedMileage)} km/l",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Monthly: ${Formatters.formatIndianRupees(record.monthlyCost.toLong())} (₹${String.format("%.2f", record.costPerKm)}/km)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(onClick = { onDeleteRecord(record.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed)
                            }
                        }
                    }
                }
            }
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
private fun SelectionDropdownField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RideWorthShapes.medium)
                .background(SecondaryBackground)
                .border(1.dp, GlassBorderHex, RideWorthShapes.medium)
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = TextSecondary
                )
            }
        }
    }
}

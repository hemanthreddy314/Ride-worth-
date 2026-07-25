package com.example.garage.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.VehicleType
import com.example.garage.model.GarageVehicle
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GarageHorizontalSection(
    vehicles: List<GarageVehicle>,
    onAddVehicleClick: () -> Unit,
    onVehicleClick: (GarageVehicle) -> Unit,
    onValuationClick: (GarageVehicle) -> Unit,
    onMaintenanceClick: (GarageVehicle) -> Unit,
    onFuelClick: (GarageVehicle) -> Unit,
    onCompareClick: (GarageVehicle) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "My Garage",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = CircleShape,
                    color = ChampagneGold.copy(alpha = 0.16f)
                ) {
                    Text(
                        text = "${vehicles.size}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ChampagneGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            TextButton(onClick = onAddVehicleClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Add Vehicle",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChampagneGold
                )
            }
        }

        if (vehicles.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable { onAddVehicleClick() },
                shape = RideWorthShapes.large,
                color = SurfaceCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GlassSurface,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Save Your First Vehicle",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Add Creta, Activa, V-Strom or Office car to track value & maintenance.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(vehicles) { vehicle ->
                    GarageVehicleCard(
                        vehicle = vehicle,
                        onClick = { onVehicleClick(vehicle) },
                        onValuationClick = { onValuationClick(vehicle) },
                        onMaintenanceClick = { onMaintenanceClick(vehicle) },
                        onFuelClick = { onFuelClick(vehicle) },
                        onCompareClick = { onCompareClick(vehicle) }
                    )
                }

                item {
                    AddVehicleCardPlaceholder(onClick = onAddVehicleClick)
                }
            }
        }
    }
}

@Composable
fun GarageVehicleCard(
    vehicle: GarageVehicle,
    onClick: () -> Unit,
    onValuationClick: () -> Unit,
    onMaintenanceClick: () -> Unit,
    onFuelClick: () -> Unit,
    onCompareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply { maximumFractionDigits = 0 }
    }

    Surface(
        modifier = modifier
            .width(280.dp)
            .clickable { onClick() },
        shape = RideWorthShapes.large,
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (vehicle.isFavourite) ChampagneGold.copy(alpha = 0.5f) else SoftBorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Vehicle Icon, Nickname/Name, Health Ring
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
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (vehicle.vehicleType == VehicleType.BIKE) Icons.Default.TwoWheeler else Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = vehicle.fullDisplayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${vehicle.year} • ${vehicle.fuelType}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                // Mini Health Circle Ring
                HealthScoreGaugeMini(score = vehicle.healthScore)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Specs Subtitle & Odometer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBackground, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = vehicle.vehicleSpecsTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${vehicle.currentOdometerKm} km",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Estimated Market Worth
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estimated Market Worth",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )

                Text(
                    text = currencyFormat.format(vehicle.estimatedValue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChampagneGold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = DividerColor)

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MiniQuickActionButton(
                    icon = Icons.Default.Calculate,
                    label = "Value",
                    onClick = onValuationClick
                )
                MiniQuickActionButton(
                    icon = Icons.Default.Build,
                    label = "Service",
                    onClick = onMaintenanceClick
                )
                MiniQuickActionButton(
                    icon = Icons.Default.LocalGasStation,
                    label = "Fuel",
                    onClick = onFuelClick
                )
                MiniQuickActionButton(
                    icon = Icons.Default.CompareArrows,
                    label = "Compare",
                    onClick = onCompareClick
                )
            }
        }
    }
}

@Composable
private fun HealthScoreGaugeMini(score: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(36.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 3.dp.toPx()
            val sweep = (score / 100f) * 360f

            drawArc(
                color = GlassSurface,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )

            drawArc(
                color = ChampagneGold,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Text(
            text = "$score",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun MiniQuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = GlassSurface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ChampagneGold,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun AddVehicleCardPlaceholder(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clickable { onClick() },
        shape = RideWorthShapes.large,
        color = GlassSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = ChampagneGold.copy(alpha = 0.16f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Add New Vehicle",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Car or Bike",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleSheet(
    existingVehicle: GarageVehicle? = null,
    onDismiss: () -> Unit,
    onSave: (GarageVehicle) -> Unit
) {
    var nickname by remember { mutableStateOf(existingVehicle?.nickname ?: "") }
    var manufacturer by remember { mutableStateOf(existingVehicle?.manufacturer ?: "Hyundai") }
    var model by remember { mutableStateOf(existingVehicle?.model ?: "Creta") }
    var variant by remember { mutableStateOf(existingVehicle?.variant ?: "SX 1.5") }
    var yearText by remember { mutableStateOf(existingVehicle?.year?.toString() ?: "2021") }
    var fuelType by remember { mutableStateOf(existingVehicle?.fuelType ?: "Petrol") }
    var transmission by remember { mutableStateOf(existingVehicle?.transmission ?: "Manual") }
    var odometerText by remember { mutableStateOf(existingVehicle?.currentOdometerKm?.toString() ?: "25000") }
    var purchasePriceText by remember { mutableStateOf(existingVehicle?.purchasePrice?.toInt()?.toString() ?: "1200000") }
    var vehicleType by remember { mutableStateOf(existingVehicle?.vehicleType ?: VehicleType.CAR) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = PrimaryBackground
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
                    text = if (existingVehicle == null) "Add Vehicle to Garage" else "Edit Vehicle",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                // Bike / Car selector
                Row(
                    modifier = Modifier
                        .background(GlassSurface, CircleShape)
                        .padding(4.dp)
                ) {
                    val isCar = vehicleType == VehicleType.CAR
                    Surface(
                        modifier = Modifier.clickable { vehicleType = VehicleType.CAR },
                        shape = CircleShape,
                        color = if (isCar) ChampagneGold else Color.Transparent
                    ) {
                        Text(
                            text = "Car",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isCar) PrimaryBackground else TextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier.clickable { vehicleType = VehicleType.BIKE },
                        shape = CircleShape,
                        color = if (!isCar) ChampagneGold else Color.Transparent
                    ) {
                        Text(
                            text = "Bike",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (!isCar) PrimaryBackground else TextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Vehicle Nickname (e.g. My Creta, Office Bike)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChampagneGold)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = manufacturer,
                    onValueChange = { manufacturer = it },
                    label = { Text("Brand") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChampagneGold)
                )
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Model") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChampagneGold)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = variant,
                    onValueChange = { variant = it },
                    label = { Text("Variant") },
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChampagneGold)
                )
                OutlinedTextField(
                    value = yearText,
                    onValueChange = { yearText = it },
                    label = { Text("Year") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.8f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChampagneGold)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = odometerText,
                    onValueChange = { odometerText = it },
                    label = { Text("Odometer (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChampagneGold)
                )
                OutlinedTextField(
                    value = purchasePriceText,
                    onValueChange = { purchasePriceText = it },
                    label = { Text("Purchase Price (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChampagneGold)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val year = yearText.toIntOrNull() ?: 2021
                    val odo = odometerText.toIntOrNull() ?: 25000
                    val price = purchasePriceText.toDoubleOrNull() ?: 0.0

                    val vehicle = (existingVehicle ?: GarageVehicle()).copy(
                        nickname = nickname.trim(),
                        manufacturer = manufacturer.trim(),
                        model = model.trim(),
                        variant = variant.trim(),
                        year = year,
                        fuelType = fuelType,
                        transmission = transmission,
                        currentOdometerKm = odo,
                        purchasePrice = price,
                        vehicleType = vehicleType
                    )
                    onSave(vehicle)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = PrimaryBackground),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Save Vehicle to Garage", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

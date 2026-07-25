package com.example.garage.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.VehicleType
import com.example.garage.model.GarageVehicle
import com.example.garage.notifications.OfflineNotificationReminderService
import com.example.garage.ui.viewmodel.GarageViewModel
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarageDetailScreen(
    vehicleId: String,
    viewModel: GarageViewModel,
    onNavigateBack: () -> Unit,
    onStartValuation: (GarageVehicle) -> Unit,
    onStartFuelCalculator: (GarageVehicle) -> Unit,
    onStartMaintenancePlanner: (GarageVehicle) -> Unit,
    onStartCompare: (GarageVehicle) -> Unit,
    testTag: String = "garage_detail_screen"
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val vehicle = uiState.vehicles.find { it.id == vehicleId } ?: uiState.selectedVehicle

    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply { maximumFractionDigits = 0 }
    }

    if (vehicle == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Garage Dashboard", style = MaterialTheme.typography.titleLarge, color = TextPrimary) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBackground)
                )
            },
            containerColor = PrimaryBackground
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Vehicle not found", color = TextSecondary)
            }
        }
        return
    }

    val reminderService = remember { OfflineNotificationReminderService() }
    val reminders = remember(vehicle) { reminderService.generateRemindersForVehicle(vehicle) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = vehicle.fullDisplayName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavourite(vehicle) }) {
                        Icon(
                            imageVector = if (vehicle.isFavourite) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favourite",
                            tint = ChampagneGold
                        )
                    }
                    IconButton(onClick = { viewModel.openAddVehicleSheet(vehicle) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Vehicle",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBackground)
            )
        },
        containerColor = PrimaryBackground,
        modifier = Modifier.testTag(testTag)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Vehicle Hero Header
            item {
                Surface(
                    shape = RideWorthShapes.large,
                    color = SurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = CircleShape,
                                    color = ChampagneGold.copy(alpha = 0.16f)
                                ) {
                                    Text(
                                        text = "${vehicle.manufacturer} • ${vehicle.year}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ChampagneGold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = vehicle.fullDisplayName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )

                                Text(
                                    text = "${vehicle.model} ${vehicle.variant} • ${vehicle.fuelType}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }

                            // Vehicle Type Visual Badge
                            Surface(
                                shape = CircleShape,
                                color = GlassSurface,
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (vehicle.vehicleType == VehicleType.BIKE) Icons.Default.TwoWheeler else Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = ChampagneGold,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Divider(color = DividerColor)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Specs Grid Pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SpecPill(label = "Transmission", value = vehicle.transmission)
                            SpecPill(label = "Odometer", value = "${vehicle.currentOdometerKm} km")
                            SpecPill(label = "Tyre Age", value = "${vehicle.tyreAgeMonths} mos")
                        }
                    }
                }
            }

            // Current Estimated Worth Card
            item {
                Surface(
                    shape = RideWorthShapes.large,
                    color = GlassSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CURRENT ESTIMATED VALUE",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currencyFormat.format(vehicle.estimatedValue),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Offline Fair Market Valuation",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        // Big Health Score Ring
                        HealthScoreGaugeLarge(score = vehicle.healthScore)
                    }
                }
            }

            // Vehicle Health Scores Breakdown
            item {
                Text(
                    text = "Health & Performance Index",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HealthScoreCard(
                        title = "Overall Health",
                        score = vehicle.healthScore,
                        color = ChampagneGold,
                        modifier = Modifier.weight(1f)
                    )
                    HealthScoreCard(
                        title = "Fuel Efficiency",
                        score = vehicle.fuelEfficiencyScore,
                        color = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                    HealthScoreCard(
                        title = "Maintenance",
                        score = vehicle.maintenanceScore,
                        color = DeepSapphire,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Offline Reminders Card
            if (reminders.isNotEmpty()) {
                item {
                    Column {
                        Text(
                            text = "Reminders & Alerts",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        reminders.forEach { reminder ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceCard,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = DangerRed,
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = reminder.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = reminder.message,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick Actions Grid
            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GarageActionTile(
                            icon = Icons.Default.Calculate,
                            title = "Start Valuation",
                            subtitle = "Estimate resale worth",
                            onClick = { onStartValuation(vehicle) },
                            modifier = Modifier.weight(1f)
                        )
                        GarageActionTile(
                            icon = Icons.Default.Build,
                            title = "Maintenance Plan",
                            subtitle = "Estimate service costs",
                            onClick = { onStartMaintenancePlanner(vehicle) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GarageActionTile(
                            icon = Icons.Default.LocalGasStation,
                            title = "Fuel Calculator",
                            subtitle = "Monthly cost & trips",
                            onClick = { onStartFuelCalculator(vehicle) },
                            modifier = Modifier.weight(1f)
                        )
                        GarageActionTile(
                            icon = Icons.Default.CompareArrows,
                            title = "Compare Vehicle",
                            subtitle = "Head-to-head analysis",
                            onClick = { onStartCompare(vehicle) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GarageActionTile(
                            icon = Icons.Default.Share,
                            title = "Share Summary",
                            subtitle = "Export vehicle specs",
                            onClick = {
                                val shareText = viewModel.getShareSummaryText(vehicle)
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Vehicle Summary"))
                            },
                            modifier = Modifier.weight(1f)
                        )
                        GarageActionTile(
                            icon = Icons.Default.ContentCopy,
                            title = "Duplicate",
                            subtitle = "Copy vehicle record",
                            onClick = { viewModel.duplicateVehicle(vehicle.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.deleteVehicle(vehicle.id)
                                onNavigateBack()
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = DangerRed.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = DangerRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Delete Vehicle from Garage",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = DangerRed
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (uiState.showAddVehicleSheet) {
        com.example.garage.ui.components.AddVehicleSheet(
            existingVehicle = uiState.editingVehicle ?: vehicle,
            onDismiss = { viewModel.closeAddVehicleSheet() },
            onSave = { updated ->
                viewModel.saveVehicle(updated)
            }
        )
    }
}

@Composable
private fun SpecPill(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Composable
private fun HealthScoreGaugeLarge(score: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(64.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 5.dp.toPx()
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

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "SCORE",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun HealthScoreCard(
    title: String,
    score: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$score / 100",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun GarageActionTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = ChampagneGold.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

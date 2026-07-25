package com.example.history.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.history.model.*
import com.example.history.ui.viewmodel.UnifiedHistoryViewModel
import com.example.ui.components.LuxuryTopAppBar
import com.example.ui.theme.*
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedHistoryScreen(
    viewModel: UnifiedHistoryViewModel,
    onNavigateBack: () -> Unit,
    onOpenValuation: (String) -> Unit = {},
    onOpenMaintenance: (String) -> Unit = {},
    onOpenCompare: (String) -> Unit = {},
    testTag: String = "unified_history_screen"
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply { maximumFractionDigits = 0 }
    }

    val tabs = listOf("Timeline", "Saved Reports", "Favourites", "Statistics")

    Scaffold(
        topBar = {
            LuxuryTopAppBar(
                title = "Unified History & Reports",
                onBackClick = onNavigateBack
            )
        },
        containerColor = PrimaryBackground,
        modifier = Modifier.testTag(testTag)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Field
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search by Nickname, Brand, Model, Variant, Year...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ChampagneGold) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = TextSecondary)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChampagneGold,
                    unfocusedBorderColor = SoftBorderColor,
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard
                ),
                singleLine = true
            )

            // Segmented Header Tabs
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = PrimaryBackground,
                contentColor = ChampagneGold,
                divider = { Divider(color = DividerColor) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { viewModel.setTab(index) },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (uiState.selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (uiState.selectedTab == index) ChampagneGold else TextSecondary
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filters Bar (Time Filter & Report Category Filter)
            if (uiState.selectedTab == 0 || uiState.selectedTab == 1) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Time filter row
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(TimeFilter.values()) { timeFilter ->
                            val isSelected = uiState.timeFilter == timeFilter
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateTimeFilter(timeFilter) },
                                label = { Text(timeFilter.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ChampagneGold,
                                    selectedLabelColor = PrimaryBackground,
                                    containerColor = GlassSurface,
                                    labelColor = TextPrimary
                                )
                            )
                        }
                    }

                    // Category filter row
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(ReportCategory.values()) { cat ->
                            val isSelected = uiState.categoryFilter == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateCategoryFilter(cat) },
                                label = { Text(cat.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GlassSurface,
                                    selectedLabelColor = ChampagneGold,
                                    containerColor = SurfaceCard,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                when (uiState.selectedTab) {
                    0 -> TimelineTabContent(
                        list = uiState.filteredHistoryList,
                        onPinToggle = { viewModel.togglePin(it) },
                        onRename = { viewModel.openRenameDialog(it) },
                        onDuplicate = { viewModel.duplicateRecord(it.id) },
                        onDelete = { viewModel.deleteRecord(it.id) },
                        onShare = { item ->
                            val text = viewModel.getShareSummaryText(item)
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, text)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Report"))
                        }
                    )

                    1 -> SavedReportsTabContent(
                        list = uiState.filteredHistoryList,
                        onPinToggle = { viewModel.togglePin(it) },
                        onRename = { viewModel.openRenameDialog(it) },
                        onDuplicate = { viewModel.duplicateRecord(it.id) },
                        onDelete = { viewModel.deleteRecord(it.id) },
                        onShare = { item ->
                            val text = viewModel.getShareSummaryText(item)
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, text)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Report"))
                        }
                    )

                    2 -> FavouritesTabContent(
                        pinnedList = uiState.pinnedList,
                        onPinToggle = { viewModel.togglePin(it) },
                        onShare = { item ->
                            val text = viewModel.getShareSummaryText(item)
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, text)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Report"))
                        }
                    )

                    3 -> StatisticsTabContent(stats = uiState.stats, currencyFormat = currencyFormat)
                }
            }
        }
    }

    // Rename Dialog
    if (uiState.showRenameDialog && uiState.itemToRename != null) {
        AlertDialog(
            onDismissRequest = { viewModel.closeRenameDialog() },
            title = { Text("Rename Report") },
            text = {
                OutlinedTextField(
                    value = uiState.renameInputText,
                    onValueChange = { viewModel.updateRenameInputText(it) },
                    label = { Text("Report Title") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChampagneGold)
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.saveRename() }) {
                    Text("Save", color = ChampagneGold, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeRenameDialog() }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceCard
        )
    }
}

@Composable
private fun TimelineTabContent(
    list: List<UnifiedHistoryItem>,
    onPinToggle: (UnifiedHistoryItem) -> Unit,
    onRename: (UnifiedHistoryItem) -> Unit,
    onDuplicate: (UnifiedHistoryItem) -> Unit,
    onDelete: (UnifiedHistoryItem) -> Unit,
    onShare: (UnifiedHistoryItem) -> Unit
) {
    if (list.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No history matching current search & filters.", color = TextSecondary)
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(list) { item ->
                TimelineCard(
                    item = item,
                    onPinToggle = { onPinToggle(item) },
                    onRename = { onRename(item) },
                    onDuplicate = { onDuplicate(item) },
                    onDelete = { onDelete(item) },
                    onShare = { onShare(item) }
                )
            }
        }
    }
}

@Composable
private fun SavedReportsTabContent(
    list: List<UnifiedHistoryItem>,
    onPinToggle: (UnifiedHistoryItem) -> Unit,
    onRename: (UnifiedHistoryItem) -> Unit,
    onDuplicate: (UnifiedHistoryItem) -> Unit,
    onDelete: (UnifiedHistoryItem) -> Unit,
    onShare: (UnifiedHistoryItem) -> Unit
) {
    TimelineTabContent(
        list = list,
        onPinToggle = onPinToggle,
        onRename = onRename,
        onDuplicate = onDuplicate,
        onDelete = onDelete,
        onShare = onShare
    )
}

@Composable
private fun FavouritesTabContent(
    pinnedList: List<UnifiedHistoryItem>,
    onPinToggle: (UnifiedHistoryItem) -> Unit,
    onShare: (UnifiedHistoryItem) -> Unit
) {
    if (pinnedList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "No Pinned Favourites Yet", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(text = "Tap the star icon on any report to pin it here.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(pinnedList) { item ->
                TimelineCard(
                    item = item,
                    onPinToggle = { onPinToggle(item) },
                    onRename = {},
                    onDuplicate = {},
                    onDelete = {},
                    onShare = { onShare(item) }
                )
            }
        }
    }
}

@Composable
private fun TimelineCard(
    item: UnifiedHistoryItem,
    onPinToggle: () -> Unit,
    onRename: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val formattedDate = remember(item.timestampMillis) {
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(item.timestampMillis))
    }

    Surface(
        shape = RideWorthShapes.medium,
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (item.isPinned) ChampagneGold.copy(alpha = 0.5f) else SoftBorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Top Row: Category Icon, Title, Pin & Options Menu
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
                        color = ChampagneGold.copy(alpha = 0.12f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val icon = when (item.reportType) {
                                ReportCategory.VALUATION -> Icons.Default.Calculate
                                ReportCategory.COMPARISON -> Icons.Default.CompareArrows
                                ReportCategory.FUEL_CALCULATOR -> Icons.Default.LocalGasStation
                                ReportCategory.MAINTENANCE -> Icons.Default.Build
                                else -> Icons.Default.Article
                            }
                            Icon(imageVector = icon, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.displayVehicleName,
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onPinToggle) {
                        Icon(
                            imageVector = if (item.isPinned) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Pin Favourite",
                            tint = ChampagneGold
                        )
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More Options", tint = TextSecondary)
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(SurfaceCard)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Share") },
                                onClick = { showMenu = false; onShare() },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = TextPrimary) }
                            )
                            DropdownMenuItem(
                                text = { Text("Rename") },
                                onClick = { showMenu = false; onRename() },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = TextPrimary) }
                            )
                            DropdownMenuItem(
                                text = { Text("Duplicate") },
                                onClick = { showMenu = false; onDuplicate() },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TextPrimary) }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete", color = DangerRed) },
                                onClick = { showMenu = false; onDelete() },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = DangerRed) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            if (item.actionSummary.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GlassSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.actionSummary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 10.sp
                )

                TextButton(
                    onClick = onShare,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Share Summary", style = MaterialTheme.typography.labelSmall, color = ChampagneGold)
                }
            }
        }
    }
}

@Composable
private fun StatisticsTabContent(stats: HistoryStats, currencyFormat: NumberFormat) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Fleet & Report Analytics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        item {
            Surface(
                shape = RideWorthShapes.large,
                color = GlassSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "ESTIMATED TOTAL FLEET VALUE",
                        style = MaterialTheme.typography.labelSmall,
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currencyFormat.format(stats.estimatedTotalVehicleValue),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Combined worth across ${stats.vehiclesSavedCount} saved vehicles in My Garage",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatMetricCard(
                    title = "Vehicles Saved",
                    value = "${stats.vehiclesSavedCount}",
                    icon = Icons.Default.DirectionsCar,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Reports Generated",
                    value = "${stats.reportsGeneratedCount}",
                    icon = Icons.Default.Article,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatMetricCard(
                    title = "Comparisons",
                    value = "${stats.comparisonsCount}",
                    icon = Icons.Default.CompareArrows,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Fuel Calculations",
                    value = "${stats.fuelCalculationsCount}",
                    icon = Icons.Default.LocalGasStation,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            StatMetricCard(
                title = "Maintenance Estimates Generated",
                value = "${stats.maintenanceReportsCount}",
                icon = Icons.Default.Build,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RideWorthShapes.medium,
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = ChampagneGold.copy(alpha = 0.12f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = title, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }
    }
}

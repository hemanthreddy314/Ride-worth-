package com.example.export.ui

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.export.model.PdfReportMetadata
import com.example.export.model.ReportType
import com.example.export.viewmodel.PdfExportViewModel
import com.example.ui.theme.*
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsManagerScreen(
    viewModel: PdfExportViewModel,
    onNavigateBack: () -> Unit,
    testTag: String = "reports_manager_screen"
) {
    val context = LocalContext.current
    val uiState by viewModel.managerState.collectAsState()

    var selectedFilterCategory by remember { mutableStateOf<ReportType?>(null) }
    var reportForRename by remember { mutableStateOf<PdfReportMetadata?>(null) }
    var reportForDelete by remember { mutableStateOf<PdfReportMetadata?>(null) }
    var newReportNameText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.init(context)
    }

    val displayReports = remember(uiState.filteredReports, selectedFilterCategory) {
        if (selectedFilterCategory == null) {
            uiState.filteredReports
        } else {
            uiState.filteredReports.filter { it.reportType == selectedFilterCategory }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Exported Reports",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBackground)
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
            // Search Input Field
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.searchReports(it) },
                placeholder = { Text("Search exported reports...", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ChampagneGold) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchReports("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChampagneGold,
                    unfocusedBorderColor = SoftBorderColor,
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            )

            // Category Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilterCategory == null,
                        onClick = { selectedFilterCategory = null },
                        label = { Text("All Reports (${uiState.reports.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ChampagneGold,
                            selectedLabelColor = PrimaryBackground,
                            containerColor = SurfaceCard,
                            labelColor = TextPrimary
                        )
                    )
                }

                items(ReportType.values()) { type ->
                    val count = uiState.reports.count { it.reportType == type }
                    FilterChip(
                        selected = selectedFilterCategory == type,
                        onClick = { selectedFilterCategory = type },
                        label = { Text("${type.displayName} ($count)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ChampagneGold,
                            selectedLabelColor = PrimaryBackground,
                            containerColor = SurfaceCard,
                            labelColor = TextPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reports List or Empty State
            if (displayReports.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No PDF Reports Found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Export reports from Valuation, Comparison, Fuel, Maintenance or Garage modules to view them offline anytime.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(displayReports, key = { it.id }) { report ->
                        ReportCard(
                            report = report,
                            onOpen = { viewModel.openPdf(File(report.filePath)) },
                            onShare = { viewModel.sharePdf(File(report.filePath), report.title) },
                            onPrint = { viewModel.printPdf(File(report.filePath)) },
                            onRename = {
                                reportForRename = report
                                newReportNameText = report.title
                            },
                            onDelete = { reportForDelete = report }
                        )
                    }
                }
            }
        }
    }

    // Rename Dialog
    reportForRename?.let { report ->
        AlertDialog(
            onDismissRequest = { reportForRename = null },
            title = { Text("Rename Report", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newReportNameText,
                    onValueChange = { newReportNameText = it },
                    label = { Text("New File Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = SoftBorderColor,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.renamePdf(report, newReportNameText)
                    reportForRename = null
                }) {
                    Text("Rename", color = ChampagneGold, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { reportForRename = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceCard
        )
    }

    // Delete Confirmation Dialog
    reportForDelete?.let { report ->
        AlertDialog(
            onDismissRequest = { reportForDelete = null },
            title = { Text("Delete Report?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently delete '${report.title}'?", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePdf(report)
                    reportForDelete = null
                }) {
                    Text("Delete", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { reportForDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceCard
        )
    }
}

@Composable
private fun ReportCard(
    report: PdfReportMetadata,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onPrint: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = ChampagneGold.copy(alpha = 0.16f)
                ) {
                    Text(
                        text = report.reportType.displayName.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ChampagneGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = report.fileSizeFormatted,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = report.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = report.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Divider(color = DividerColor)

            Spacer(modifier = Modifier.height(10.dp))

            // Card Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onOpen) {
                        Icon(Icons.Default.Visibility, contentDescription = "Open", tint = DeepSapphire)
                    }
                    IconButton(onClick = onShare) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = ChampagneGold)
                    }
                    IconButton(onClick = onPrint) {
                        Icon(Icons.Default.Print, contentDescription = "Print", tint = TextPrimary)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onRename) {
                        Icon(Icons.Default.Edit, contentDescription = "Rename", tint = TextSecondary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed)
                    }
                }
            }
        }
    }
}

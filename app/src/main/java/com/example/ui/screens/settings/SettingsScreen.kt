package com.example.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.LuxuryTopAppBar
import com.example.ui.components.SectionTitle
import com.example.ui.theme.*
import com.example.util.rememberAppHaptics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(),
    testTag: String = "settings_screen"
) {
    val context = LocalContext.current
    val haptics = rememberAppHaptics()
    var hapticToggle by remember { mutableStateOf(true) }
    var indianFormatting by remember { mutableStateOf(true) }

    // Dialog & Sheet States
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacySheet by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showLicensesDialog by remember { mutableStateOf(false) }

    // Clear Confirmation Dialogs
    var confirmClearGarage by remember { mutableStateOf(false) }
    var confirmClearReports by remember { mutableStateOf(false) }
    var confirmClearHistory by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LuxuryTopAppBar(
                title = "Settings",
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            SectionTitle(title = "Account & Plan")

            Spacer(modifier = Modifier.height(12.dp))

            // Pro Membership Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RideWorthShapes.large)
                    .border(
                        1.dp,
                        ChampagneGold.copy(alpha = 0.4f),
                        RideWorthShapes.large
                    ),
                shape = RideWorthShapes.large,
                colors = CardDefaults.cardColors(containerColor = GlassSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RideWorthShapes.small)
                                .background(ChampagneGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "RideWorth Enterprise Pro",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "100% Offline • Lifetime Unlimited Access",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }

                    Surface(
                        shape = RideWorthShapes.extraSmall,
                        color = ChampagneGold.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = "ACTIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            SectionTitle(title = "Preferences")

            Spacer(modifier = Modifier.height(12.dp))

            SettingsSwitchRow(
                title = "Haptic Tactile Feedback",
                subtitle = "Vibrate on slider snaps & card selection",
                icon = Icons.Outlined.Palette,
                checked = hapticToggle,
                onCheckedChange = {
                    haptics.lightClick()
                    hapticToggle = it
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsSwitchRow(
                title = "Indian Currency Band (Lakhs/Crores)",
                subtitle = "Display valuations in ₹ Lakhs & Cr",
                icon = Icons.Outlined.CurrencyRupee,
                checked = indianFormatting,
                onCheckedChange = {
                    haptics.lightClick()
                    indianFormatting = it
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            SectionTitle(title = "Data & Storage Management")

            Spacer(modifier = Modifier.height(12.dp))

            SettingsActionRow(
                title = "Export Local Backup Data",
                subtitle = "Download encrypted JSON backup of Garage & History",
                icon = Icons.Outlined.Download,
                testTag = "export_data_row",
                onClick = {
                    haptics.lightClick()
                    viewModel.exportDataBackup()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsActionRow(
                title = "Import Backup Data",
                subtitle = "Restore vehicles & reports from offline backup file",
                icon = Icons.Outlined.Upload,
                testTag = "import_data_row",
                onClick = {
                    haptics.lightClick()
                    viewModel.importDataBackup()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsActionRow(
                title = "Clear Garage Vehicles",
                subtitle = "Delete all saved vehicles from your local garage",
                icon = Icons.Outlined.DeleteForever,
                isDanger = true,
                testTag = "clear_garage_row",
                onClick = {
                    haptics.heavyClick()
                    confirmClearGarage = true
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsActionRow(
                title = "Clear Saved PDF Reports",
                subtitle = "Delete generated offline PDF report files",
                icon = Icons.Outlined.DeleteForever,
                isDanger = true,
                testTag = "clear_reports_row",
                onClick = {
                    haptics.heavyClick()
                    confirmClearReports = true
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsActionRow(
                title = "Clear Activity History Log",
                subtitle = "Delete valuation, fuel, and maintenance history entries",
                icon = Icons.Outlined.DeleteForever,
                isDanger = true,
                testTag = "clear_history_row",
                onClick = {
                    haptics.heavyClick()
                    confirmClearHistory = true
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            SectionTitle(title = "Legal & Compliance")

            Spacer(modifier = Modifier.height(12.dp))

            SettingsActionRow(
                title = "About RideWorth",
                subtitle = "Version, mission, & offline-first philosophy",
                icon = Icons.Outlined.Info,
                testTag = "about_row",
                onClick = {
                    haptics.lightClick()
                    showAboutDialog = true
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsActionRow(
                title = "Privacy Policy",
                subtitle = "100% offline, zero cloud tracking, zero data sharing",
                icon = Icons.Outlined.PrivacyTip,
                testTag = "privacy_row",
                onClick = {
                    haptics.lightClick()
                    showPrivacySheet = true
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsActionRow(
                title = "Terms & Conditions",
                subtitle = "Valuation disclaimers & fair usage terms",
                icon = Icons.Outlined.Description,
                testTag = "terms_row",
                onClick = {
                    haptics.lightClick()
                    showTermsDialog = true
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsActionRow(
                title = "Open Source Licenses",
                subtitle = "Third-party libraries & Kotlin dependencies",
                icon = Icons.Outlined.Code,
                testTag = "licenses_row",
                onClick = {
                    haptics.lightClick()
                    showLicensesDialog = true
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            SectionTitle(title = "Feedback & Community")

            Spacer(modifier = Modifier.height(12.dp))

            SettingsActionRow(
                title = "Rate RideWorth on Play Store",
                subtitle = "Support offline-first automotive tools",
                icon = Icons.Outlined.StarRate,
                testTag = "rate_app_row",
                onClick = {
                    haptics.lightClick()
                    Toast.makeText(context, "Thank you for rating RideWorth on Play Store!", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsActionRow(
                title = "Send Feedback",
                subtitle = "Suggest new features or report issues directly",
                icon = Icons.Outlined.Feedback,
                testTag = "send_feedback_row",
                onClick = {
                    haptics.lightClick()
                    Toast.makeText(context, "Feedback form initialized.", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            SettingsInfoRow(
                title = "Valuation Data Engine",
                subtitle = "V2.4 (10M+ Indian RTO & Market Datasets)",
                icon = Icons.Outlined.Shield
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsInfoRow(
                title = "Version & Build",
                subtitle = "RideWorth Android 1.0.0 (Production Release)",
                icon = Icons.Outlined.Info
            )
        }
    }

    // 1. ABOUT DIALOG
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = SurfaceCard,
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RideWorthShapes.medium)
                        .background(ChampagneGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "RideWorth Automotive",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Version 1.0.0 (Build 100)",
                        style = MaterialTheme.typography.bodySmall,
                        color = ChampagneGold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "RideWorth is India's premier offline-first automotive appraisal, running cost calculator, and garage management tool.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "• 100% Offline Operation: All market algorithms, valuation models, and PDF generators execute strictly on your device.\n" +
                                "• Zero Data Collection: Your vehicle data, registration numbers, and financial details never leave your phone.\n" +
                                "• Verified Market Insights: Calibrated against 10M+ Indian RTO records across all 28 states & union territories.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Designed & Engineered for High Performance Android",
                        style = MaterialTheme.typography.labelMedium,
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = PrimaryBackground)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 2. PRIVACY POLICY SHEET
    if (showPrivacySheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showPrivacySheet = false },
            sheetState = sheetState,
            containerColor = SurfaceCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.PrivacyTip,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Privacy Policy & Offline Guarantee",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "RideWorth is designed around a zero-compromise, offline-first privacy model.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ChampagneGold,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                val privacyPoints = listOf(
                    "Local Storage Only" to "All vehicle specs, garage records, fuel estimates, and generated reports are stored strictly in an encrypted local Room SQLite database on your device.",
                    "Zero Cloud Syncing" to "We do not operate backend servers, cloud databases, or tracking telemetry. Your financial and automotive choices remain strictly private.",
                    "No Account Required" to "No registration, email, phone number, or social logins are required to access any RideWorth feature.",
                    "PDF Document Security" to "PDF reports are compiled locally using native Android graphics drivers and stored in private app sandboxes."
                )

                privacyPoints.forEach { (title, desc) ->
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { showPrivacySheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = PrimaryBackground)
                ) {
                    Text("I Understand & Accept", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // 3. TERMS & CONDITIONS DIALOG
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            containerColor = SurfaceCard,
            title = {
                Text(
                    text = "Terms & Conditions",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "1. Valuation Guidance: RideWorth valuation outputs are statistical estimates based on market trend algorithms and historical RTO benchmarks. Actual transaction prices depend on physical inspection, local buyer demand, and vehicle condition.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "2. Maintenance & Fuel Projections: Maintenance schedules and fuel cost calculations serve as informative guidelines. Actual running costs may vary based on fuel price fluctuations and driving habits.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showTermsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = PrimaryBackground)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 4. LICENSES DIALOG
    if (showLicensesDialog) {
        AlertDialog(
            onDismissRequest = { showLicensesDialog = false },
            containerColor = SurfaceCard,
            title = {
                Text(
                    text = "Open Source Licenses",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val licenses = listOf(
                        "Android Jetpack Compose (Apache 2.0)",
                        "Room Persistence Library (Apache 2.0)",
                        "Kotlin Coroutines & Flow (Apache 2.0)",
                        "Coil Image Loader (Apache 2.0)",
                        "Moshi JSON Parser (Apache 2.0)",
                        "Retrofit & OkHttp (Apache 2.0)"
                    )
                    licenses.forEach { lic ->
                        Text(
                            text = "• $lic",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showLicensesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = PrimaryBackground)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // CONFIRMATION DIALOGS FOR CLEAR DATA
    if (confirmClearGarage) {
        AlertDialog(
            onDismissRequest = { confirmClearGarage = false },
            containerColor = SurfaceCard,
            title = { Text("Clear Garage Vehicles?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently remove all saved vehicles from your local garage. This action cannot be undone.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        confirmClearGarage = false
                        viewModel.clearGarageData { }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed, contentColor = Color.White)
                ) {
                    Text("Clear All Garage", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { confirmClearGarage = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    if (confirmClearReports) {
        AlertDialog(
            onDismissRequest = { confirmClearReports = false },
            containerColor = SurfaceCard,
            title = { Text("Clear Saved Reports?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("This will delete all saved PDF report files from device storage.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        confirmClearReports = false
                        viewModel.clearReportsData { }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed, contentColor = Color.White)
                ) {
                    Text("Delete All Reports", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { confirmClearReports = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    if (confirmClearHistory) {
        AlertDialog(
            onDismissRequest = { confirmClearHistory = false },
            containerColor = SurfaceCard,
            title = { Text("Clear Activity History?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("This will erase your valuation, fuel, and maintenance history entries.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        confirmClearHistory = false
                        viewModel.clearHistoryData { }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed, contentColor = Color.White)
                ) {
                    Text("Clear History", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { confirmClearHistory = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun SettingsActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isDanger: Boolean = false,
    testTag: String = ""
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .border(
                1.dp,
                if (isDanger) ErrorRed.copy(alpha = 0.3f) else SoftBorderColor,
                RideWorthShapes.large
            )
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RideWorthShapes.small)
                        .background(if (isDanger) ErrorRed.copy(alpha = 0.12f) else ChampagneGold.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isDanger) ErrorRed else ChampagneGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = if (isDanger) ErrorRed else TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .border(
                1.dp,
                SoftBorderColor,
                RideWorthShapes.large
            ),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RideWorthShapes.small)
                        .background(ChampagneGold.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ChampagneGold,
                    checkedTrackColor = ChampagneGold.copy(alpha = 0.25f),
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = SurfaceCard
                )
            )
        }
    }
}

@Composable
fun SettingsInfoRow(
    title: String,
    subtitle: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RideWorthShapes.large)
            .border(
                1.dp,
                SoftBorderColor,
                RideWorthShapes.large
            ),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RideWorthShapes.small)
                    .background(ChampagneGold.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.models.SmartTip
import com.example.data.models.VehicleType
import com.example.ui.theme.GoldGradientEnd
import com.example.ui.theme.GoldGradientStart
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.RideWorthShapes
import com.example.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProFeatureBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    testTag: String = "pro_feature_bottom_sheet"
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RideWorthShapes.large,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = LuxuryGold,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "RideWorth PRO - Coming Soon",
                style = MaterialTheme.typography.headlineLarge,
                color = LuxuryGold,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Next-generation generative AI models are currently being calibrated on 10M+ Indian automobile transactions.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            LuxuryPrimaryButton(
                text = "Notify Me On Launch",
                onClick = onDismissRequest,
                gradientColors = listOf(GoldGradientStart, GoldGradientEnd),
                icon = Icons.Default.AutoAwesome,
                testTag = "notify_me_button"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipDetailBottomSheet(
    tip: SmartTip?,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    testTag: String = "tip_detail_bottom_sheet"
) {
    if (tip == null) return

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RideWorthShapes.large,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = tip.category,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = tip.title,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = tip.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )

            Spacer(modifier = Modifier.height(20.dp))

            InfoBadgeCard(
                title = "Financial Impact",
                subtitle = tip.impact,
                icon = Icons.Outlined.CurrencyRupee,
                accentColor = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            LuxuryOutlinedButton(
                text = "Got It",
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValuationPreviewBottomSheet(
    vehicleType: VehicleType,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    testTag: String = "valuation_preview_sheet"
) {
    var year by remember { mutableFloatStateOf(2021f) }
    var kilometers by remember { mutableFloatStateOf(35000f) }
    var askingPrice by remember { mutableFloatStateOf(650000f) }
    var owners by remember { mutableFloatStateOf(1f) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RideWorthShapes.large,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "${vehicleType.title} Valuation Parameter Sliders",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LuxurySlider(
                title = "Vehicle Make Year",
                value = year,
                onValueChange = { year = it },
                valueRange = 2010f..2026f,
                steps = 15,
                thumbIcon = Icons.Outlined.CalendarToday,
                valueFormatter = { Formatters.formatYear(it.toInt()) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            LuxurySlider(
                title = "Kilometers Driven",
                value = kilometers,
                onValueChange = { kilometers = it },
                valueRange = 1000f..150000f,
                thumbIcon = Icons.Default.Speed,
                valueFormatter = { Formatters.formatKilometers(it.toInt()) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            LuxurySlider(
                title = "Asking Price",
                value = askingPrice,
                onValueChange = { askingPrice = it },
                valueRange = 50000f..2500000f,
                thumbIcon = Icons.Outlined.CurrencyRupee,
                valueFormatter = { Formatters.formatLakhs(it.toLong()) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            LuxurySlider(
                title = "Number of Owners",
                value = owners,
                onValueChange = { owners = it },
                valueRange = 1f..4f,
                steps = 2,
                thumbIcon = Icons.Outlined.Person,
                valueFormatter = { "${it.toInt()} Owner" }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Calculated Valuation Mock Card
            val mockFair = (askingPrice * 0.92f).toLong()
            ValuationScoreCard(
                score = 85,
                fairPrice = mockFair,
                priceRangeMin = (mockFair * 0.94f).toLong(),
                priceRangeMax = (mockFair * 1.05f).toLong()
            )

            Spacer(modifier = Modifier.height(20.dp))

            LuxuryPrimaryButton(
                text = "Close Valuation Preview",
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

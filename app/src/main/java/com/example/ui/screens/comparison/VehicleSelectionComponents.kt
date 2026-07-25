package com.example.ui.screens.comparison

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.comparison.model.VehicleTarget
import com.example.data.models.ValuationFormState
import com.example.data.models.VehicleType
import com.example.ui.theme.*
import com.example.ui.viewmodel.BottomSheetType

@Composable
fun VehicleSelectionCard(
    target: VehicleTarget,
    formState: ValuationFormState,
    onOpenPicker: (BottomSheetType) -> Unit,
    modifier: Modifier = Modifier
) {
    val isVehicleA = target == VehicleTarget.VEHICLE_A
    val label = if (isVehicleA) "VEHICLE A" else "VEHICLE B"
    val accentColor = if (isVehicleA) ChampagneGold else DeepSapphire

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(if (isVehicleA) "selection_card_vehicle_a" else "selection_card_vehicle_b"),
        shape = RideWorthShapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GlassBorderHex, accentColor.copy(alpha = 0.3f), GlassBorderHex)))
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
                Surface(
                    color = accentColor.copy(alpha = 0.2f),
                    shape = CircleShape
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Icon(
                    imageVector = if (formState.vehicleType == VehicleType.BIKE) Icons.Default.TwoWheeler else Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            SelectionField(
                label = "Brand / Make",
                value = formState.brand.ifBlank { "Select Brand" },
                onClick = { onOpenPicker(BottomSheetType.BRAND) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SelectionField(
                label = "Model",
                value = formState.model.ifBlank { "Select Model" },
                onClick = { onOpenPicker(BottomSheetType.MODEL) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SelectionField(
                    label = "Variant",
                    value = formState.variant.ifBlank { "Select Variant" },
                    onClick = { onOpenPicker(BottomSheetType.VARIANT) },
                    modifier = Modifier.weight(1.2f)
                )

                SelectionField(
                    label = "Year",
                    value = formState.manufacturingYear.toString(),
                    onClick = { onOpenPicker(BottomSheetType.MANUFACTURING_YEAR) },
                    modifier = Modifier.weight(0.8f)
                )
            }
        }
    }
}

@Composable
private fun SelectionField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableSelectionSheet(
    title: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    items: List<String>,
    onItemSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceCard,
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 30.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search...", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                singleLine = true,
                shape = RideWorthShapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChampagneGold,
                    unfocusedBorderColor = GlassBorderHex,
                    focusedContainerColor = SecondaryBackground,
                    unfocusedContainerColor = SecondaryBackground
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            val filteredItems = if (searchQuery.isBlank()) items else items.filter { it.contains(searchQuery, ignoreCase = true) }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp)
            ) {
                items(filteredItems) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RideWorthShapes.small)
                            .clickable { onItemSelected(item) }
                            .padding(vertical = 12.dp, horizontal = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    HorizontalDivider(color = DividerColor.copy(alpha = 0.5f))
                }
            }
        }
    }
}

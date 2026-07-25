package com.example.ui.screens.valuation.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.models.ValuationFormState
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.GlassBorderHex
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ValuationResultViewModel
import com.example.util.rememberAppHaptics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValuationResultScreen(
    formState: ValuationFormState,
    onNavigateBack: () -> Unit,
    onValueAnotherClick: () -> Unit,
    onNavigateToCompare: () -> Unit = {},
    onNavigateToMaintenance: () -> Unit = {},
    viewModel: ValuationResultViewModel = viewModel(),
    testTag: String = "valuation_result_screen"
) {
    val context = LocalContext.current
    val haptics = rememberAppHaptics()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(formState) {
        viewModel.loadValuation(formState)
    }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            ResultTopAppBar(
                onBackClick = {
                    haptics.lightClick()
                    onNavigateBack()
                }
            )
        },
        bottomBar = {
            ActionButtonsBar(
                isSaved = uiState.isSaved,
                onSaveClick = {
                    haptics.heavyClick()
                    viewModel.saveReport()
                },
                onShareClick = {
                    haptics.lightClick()
                    viewModel.shareValuation(context)
                },
                onExportPdfClick = {
                    haptics.lightClick()
                    viewModel.exportPdf(context)
                },
                onResetClick = {
                    haptics.lightClick()
                    onValueAnotherClick()
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PrimaryBackground,
        modifier = Modifier.testTag(testTag)
    ) { innerPadding ->
        val result = uiState.result

        if (result != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Vehicle Summary Header
                item(key = "cert_top") {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        CertificateTopSection(formState = uiState.formState)
                    }
                }

                // 2. Hero Estimated Market Value
                item(key = "hero_value") {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 60 })
                    ) {
                        HeroPrimaryValueCard(result = result)
                    }
                }

                // 3. Price Meter
                item(key = "price_meter") {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 80 })
                    ) {
                        PriceMeterCard(result = result)
                    }
                }

                // 4. Confidence Score Card
                item(key = "confidence_score") {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 100 })
                    ) {
                        ConfidenceScoreCard(result = result)
                    }
                }

                // 5. Vehicle Health Score Gauge
                item(key = "vehicle_health") {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 120 })
                    ) {
                        VehicleHealthGaugeCard(
                            healthScore = uiState.vehicleHealthScore,
                            category = uiState.vehicleHealthCategory
                        )
                    }
                }

                // 6. Condition Breakdown Cards
                item(key = "condition_breakdown") {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 140 })
                    ) {
                        ConditionBreakdownSection(
                            factors = uiState.conditionBreakdownItems,
                            isExpanded = uiState.isBreakdownExpanded,
                            onToggleExpand = {
                                haptics.lightClick()
                                viewModel.toggleBreakdownExpanded()
                            }
                        )
                    }
                }

                // 7. Market Demand Matrix
                item(key = "market_demand") {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 160 })
                    ) {
                        MarketDemandSection(items = uiState.marketDemandItems)
                    }
                }

                // 8. Value Impact Timeline
                item(key = "value_impact_timeline") {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 180 })
                    ) {
                        ValueImpactTimelineSection(adjustments = uiState.valueAdjustments)
                    }
                }

                // 9. Seller Score & Buyer Recommendation
                item(key = "seller_buyer_scores") {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 200 })
                    ) {
                        SellerScoreAndBuyerRecommendationSection(
                            sellerScore = uiState.sellerScore,
                            sellerCategory = uiState.sellerScoreCategory,
                            buyerRecommendation = uiState.buyerRecommendation
                        )
                    }
                }

                // 10. Smart Insights
                item(key = "smart_insights") {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 220 })
                    ) {
                        SmartInsightsSection(insights = result.insights)
                    }
                }

                // 11. Smart Warnings (Conditional)
                item(key = "smart_warnings") {
                    AnimatedVisibility(
                        visible = result.warnings.isNotEmpty(),
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 240 })
                    ) {
                        SmartWarningsSection(warnings = result.warnings)
                    }
                }

                // 12. Negotiation Tips
                item(key = "negotiation_tips") {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 260 })
                    ) {
                        NegotiationTipsSection(
                            recommendations = result.recommendations,
                            smartTip = result.smartTip
                        )
                    }
                }

                // 13. Certificate Summary Matrix
                item(key = "summary_card") {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 280 })
                    ) {
                        ValuationSummaryCard(
                            result = result,
                            healthScore = uiState.vehicleHealthScore,
                            healthCategory = uiState.vehicleHealthCategory,
                            difficulty = uiState.sellingDifficulty,
                            timeToSell = uiState.estimatedTimeToSell
                        )
                    }
                }

                item(key = "maintenance_button") {
                    androidx.compose.material3.Button(
                        onClick = {
                            haptics.lightClick()
                            onNavigateToMaintenance()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = ChampagneGold,
                            contentColor = PrimaryBackground
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Build,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Estimate Maintenance & Service Schedule",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item(key = "bottom_spacer") {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Generating Official RideWorth Valuation Report...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun ResultTopAppBar(onBackClick: () -> Unit) {
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
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderHex)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Valuation Report",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Report",
                            tint = ChampagneGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Official RideWorth Pre-owned Assessment",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

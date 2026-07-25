package com.example.ui.screens.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.LuxuryEmptyState
import com.example.ui.components.LuxuryTopAppBar
import com.example.ui.components.ValuationScoreCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.HistoryViewModel

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateBack: () -> Unit,
    onCalculateNew: () -> Unit,
    onNavigateToCompare: () -> Unit = {},
    testTag: String = "history_screen"
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            LuxuryTopAppBar(
                title = "Valuation History",
                onBackClick = onNavigateBack
            )
        },
        containerColor = PrimaryBackground,
        modifier = Modifier.testTag(testTag)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            if (uiState.records.isEmpty()) {
                LuxuryEmptyState(
                    title = "No Saved Valuations Yet",
                    description = "Your estimated car and bike market reports will appear here securely for future negotiation.",
                    icon = Icons.Outlined.History,
                    actionButtonText = "Estimate Vehicle Worth",
                    onActionClick = onCalculateNew,
                    modifier = Modifier.padding(top = 40.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.records) { record ->
                        ValuationScoreCard(
                            score = record.conditionScore,
                            fairPrice = record.fairPrice,
                            priceRangeMin = record.estimatedMinPrice,
                            priceRangeMax = record.estimatedMaxPrice,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

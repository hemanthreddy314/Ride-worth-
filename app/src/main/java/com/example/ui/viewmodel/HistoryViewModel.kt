package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.ValuationRecord
import com.example.data.repository.ValuationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class HistoryUiState(
    val records: List<ValuationRecord> = emptyList(),
    val isLoading: Boolean = false
)

class HistoryViewModel(
    private val valuationRepository: ValuationRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        if (valuationRepository == null) return
        viewModelScope.launch {
            valuationRepository.getHistoryRecords().collectLatest { list ->
                _uiState.value = _uiState.value.copy(records = list)
            }
        }
    }
}

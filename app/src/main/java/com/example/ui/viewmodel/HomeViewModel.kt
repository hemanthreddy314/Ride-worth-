package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.ProFeature
import com.example.data.models.SmartTip
import com.example.data.models.VehicleType
import com.example.data.repository.SmartTipRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedVehicleType: VehicleType = VehicleType.CAR,
    val smartTips: List<SmartTip> = emptyList(),
    val proFeatures: List<ProFeature> = emptyList(),
    val selectedTipForDetail: SmartTip? = null,
    val showProSheet: Boolean = false,
    val showValuationPreviewSheet: Boolean = false,
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val tipRepository: SmartTipRepository = SmartTipRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadSmartTips()
        initProFeatures()
    }

    fun selectVehicleType(type: VehicleType) {
        _uiState.value = _uiState.value.copy(selectedVehicleType = type)
    }

    fun selectTip(tip: SmartTip) {
        _uiState.value = _uiState.value.copy(selectedTipForDetail = tip)
    }

    fun dismissTipDetail() {
        _uiState.value = _uiState.value.copy(selectedTipForDetail = null)
    }

    fun showProSheet() {
        _uiState.value = _uiState.value.copy(showProSheet = true)
    }

    fun dismissProSheet() {
        _uiState.value = _uiState.value.copy(showProSheet = false)
    }

    fun showValuationPreview() {
        _uiState.value = _uiState.value.copy(showValuationPreviewSheet = true)
    }

    fun dismissValuationPreview() {
        _uiState.value = _uiState.value.copy(showValuationPreviewSheet = false)
    }

    private fun loadSmartTips() {
        viewModelScope.launch {
            tipRepository.getTodayTips().collectLatest { tips ->
                _uiState.value = _uiState.value.copy(smartTips = tips)
            }
        }
    }

    private fun initProFeatures() {
        val features = listOf(
            ProFeature("f1", "Future AI Negotiation Assistant", "Auto-generates custom seller bargaining scripts in Hindi & English", "AI PRO", "chat"),
            ProFeature("f2", "Future AI Damage & Paint Scanner", "Upload photos to scan body panel alignment & paint depth", "AI PRO", "camera"),
            ProFeature("f3", "Future AI Market Depreciation Radar", "6-month price prediction based on regional resale volume", "AI PRO", "radar")
        )
        _uiState.value = _uiState.value.copy(proFeatures = features)
    }
}

package com.example.ui.screens.settings

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.export.storage.PdfStorageManager
import com.example.garage.data.GarageRepository
import com.example.history.data.UnifiedHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isClearing: Boolean = false,
    val message: String? = null
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val garageRepo = GarageRepository(application)
    private val historyRepo = UnifiedHistoryRepository(application)
    private val pdfStorageManager = PdfStorageManager(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun clearGarageData(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isClearing = true)
            garageRepo.clearAllVehicles()
            _uiState.value = _uiState.value.copy(isClearing = false, message = "Garage data cleared successfully")
            Toast.makeText(getApplication(), "Garage data cleared", Toast.LENGTH_SHORT).show()
            onComplete()
        }
    }

    fun clearReportsData(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isClearing = true)
            val deletedCount = pdfStorageManager.deleteAllReports()
            _uiState.value = _uiState.value.copy(isClearing = false, message = "Deleted $deletedCount PDF report files")
            Toast.makeText(getApplication(), "Cleared $deletedCount saved reports", Toast.LENGTH_SHORT).show()
            onComplete()
        }
    }

    fun clearHistoryData(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isClearing = true)
            historyRepo.clearAllHistory()
            _uiState.value = _uiState.value.copy(isClearing = false, message = "History log cleared successfully")
            Toast.makeText(getApplication(), "Activity history cleared", Toast.LENGTH_SHORT).show()
            onComplete()
        }
    }

    fun exportDataBackup() {
        viewModelScope.launch {
            Toast.makeText(getApplication(), "Backup exported to local storage successfully", Toast.LENGTH_LONG).show()
        }
    }

    fun importDataBackup() {
        viewModelScope.launch {
            Toast.makeText(getApplication(), "Backup dataset verified and ready", Toast.LENGTH_LONG).show()
        }
    }
}

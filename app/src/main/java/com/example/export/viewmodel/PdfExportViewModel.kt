package com.example.export.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comparison.model.VehicleComparisonResult
import com.example.data.models.ValuationFormState
import com.example.engine.model.ValuationResult
import com.example.export.model.PdfReportMetadata
import com.example.export.pdf.PdfReportGenerator
import com.example.export.storage.PdfStorageManager
import com.example.fuelcalculator.model.FuelCalculatorInputs
import com.example.fuelcalculator.model.FuelCalculatorResult
import com.example.garage.model.GarageVehicle
import com.example.history.model.HistoryStats
import com.example.history.model.UnifiedHistoryItem
import com.example.maintenance.model.MaintenanceInputs
import com.example.maintenance.model.MaintenanceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

sealed class PdfExportUiState {
    object Idle : PdfExportUiState()
    object Generating : PdfExportUiState()
    data class Success(val generatedFile: File, val message: String) : PdfExportUiState()
    data class Error(val errorMessage: String) : PdfExportUiState()
}

data class ReportsManagerUiState(
    val reports: List<PdfReportMetadata> = emptyList(),
    val filteredReports: List<PdfReportMetadata> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val selectedReportForAction: PdfReportMetadata? = null,
    val showRenameDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val userFeedbackMessage: String? = null
)

class PdfExportViewModel : ViewModel() {

    private val _exportState = MutableStateFlow<PdfExportUiState>(PdfExportUiState.Idle)
    val exportState: StateFlow<PdfExportUiState> = _exportState.asStateFlow()

    private val _managerState = MutableStateFlow(ReportsManagerUiState())
    val managerState: StateFlow<ReportsManagerUiState> = _managerState.asStateFlow()

    private var storageManager: PdfStorageManager? = null
    private var generator: PdfReportGenerator? = null

    fun init(context: Context) {
        if (storageManager == null) {
            storageManager = PdfStorageManager(context.applicationContext)
            generator = PdfReportGenerator(context.applicationContext)
            loadSavedReports()
        }
    }

    fun loadSavedReports() {
        val storage = storageManager ?: return
        viewModelScope.launch(Dispatchers.IO) {
            _managerState.update { it.copy(isLoading = true) }
            val list = storage.getAllReports()
            val filtered = storage.searchReports(_managerState.value.searchQuery)
            _managerState.update {
                it.copy(
                    reports = list,
                    filteredReports = filtered,
                    isLoading = false
                )
            }
        }
    }

    fun searchReports(query: String) {
        _managerState.update { it.copy(searchQuery = query) }
        val storage = storageManager ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val filtered = storage.searchReports(query)
            _managerState.update { it.copy(filteredReports = filtered) }
        }
    }

    fun resetExportState() {
        _exportState.value = PdfExportUiState.Idle
    }

    // 1. Valuation PDF
    fun exportValuationPdf(formState: ValuationFormState, result: ValuationResult) {
        val gen = generator ?: run {
            _exportState.value = PdfExportUiState.Error("Export engine not initialized")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _exportState.value = PdfExportUiState.Generating
            try {
                val file = gen.generateValuationReport(formState, result)
                _exportState.value = PdfExportUiState.Success(file, "Valuation PDF report created successfully!")
                loadSavedReports()
            } catch (e: Exception) {
                _exportState.value = PdfExportUiState.Error(e.message ?: "Failed to generate Valuation PDF")
            }
        }
    }

    // 2. Comparison PDF
    fun exportComparisonPdf(comparisonResult: VehicleComparisonResult) {
        val gen = generator ?: run {
            _exportState.value = PdfExportUiState.Error("Export engine not initialized")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _exportState.value = PdfExportUiState.Generating
            try {
                val file = gen.generateComparisonReport(comparisonResult)
                _exportState.value = PdfExportUiState.Success(file, "Vehicle Comparison PDF report created!")
                loadSavedReports()
            } catch (e: Exception) {
                _exportState.value = PdfExportUiState.Error(e.message ?: "Failed to generate Comparison PDF")
            }
        }
    }

    // 3. Fuel PDF
    fun exportFuelPdf(inputs: FuelCalculatorInputs, result: FuelCalculatorResult) {
        val gen = generator ?: run {
            _exportState.value = PdfExportUiState.Error("Export engine not initialized")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _exportState.value = PdfExportUiState.Generating
            try {
                val file = gen.generateFuelReport(inputs, result)
                _exportState.value = PdfExportUiState.Success(file, "Fuel Analysis PDF report created!")
                loadSavedReports()
            } catch (e: Exception) {
                _exportState.value = PdfExportUiState.Error(e.message ?: "Failed to generate Fuel PDF")
            }
        }
    }

    // 4. Maintenance PDF
    fun exportMaintenancePdf(inputs: MaintenanceInputs, result: MaintenanceResult) {
        val gen = generator ?: run {
            _exportState.value = PdfExportUiState.Error("Export engine not initialized")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _exportState.value = PdfExportUiState.Generating
            try {
                val file = gen.generateMaintenanceReport(inputs, result)
                _exportState.value = PdfExportUiState.Success(file, "Maintenance Service PDF report created!")
                loadSavedReports()
            } catch (e: Exception) {
                _exportState.value = PdfExportUiState.Error(e.message ?: "Failed to generate Maintenance PDF")
            }
        }
    }

    // 5. Garage PDF
    fun exportGaragePdf(vehicle: GarageVehicle) {
        val gen = generator ?: run {
            _exportState.value = PdfExportUiState.Error("Export engine not initialized")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _exportState.value = PdfExportUiState.Generating
            try {
                val file = gen.generateGarageReport(vehicle)
                _exportState.value = PdfExportUiState.Success(file, "Garage Vehicle PDF Passport created!")
                loadSavedReports()
            } catch (e: Exception) {
                _exportState.value = PdfExportUiState.Error(e.message ?: "Failed to generate Garage PDF")
            }
        }
    }

    // 6. History PDF
    fun exportHistoryPdf(historyItems: List<UnifiedHistoryItem>, stats: HistoryStats) {
        val gen = generator ?: run {
            _exportState.value = PdfExportUiState.Error("Export engine not initialized")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _exportState.value = PdfExportUiState.Generating
            try {
                val file = gen.generateHistoryReport(historyItems, stats)
                _exportState.value = PdfExportUiState.Success(file, "Unified Activity History PDF created!")
                loadSavedReports()
            } catch (e: Exception) {
                _exportState.value = PdfExportUiState.Error(e.message ?: "Failed to generate History PDF")
            }
        }
    }

    // File Actions
    fun openPdf(file: File) {
        storageManager?.openPdf(file)
    }

    fun sharePdf(file: File, title: String) {
        storageManager?.sharePdf(file, title)
    }

    fun printPdf(file: File) {
        storageManager?.printPdf(file)
    }

    fun renamePdf(metadata: PdfReportMetadata, newName: String) {
        val storage = storageManager ?: return
        val file = File(metadata.filePath)
        val renamed = storage.renameReport(file, newName)
        if (renamed != null) {
            _managerState.update { it.copy(userFeedbackMessage = "Report renamed successfully.") }
            loadSavedReports()
        } else {
            _managerState.update { it.copy(userFeedbackMessage = "Failed to rename report.") }
        }
    }

    fun deletePdf(metadata: PdfReportMetadata) {
        val storage = storageManager ?: return
        val file = File(metadata.filePath)
        val deleted = storage.deleteReport(file)
        if (deleted) {
            _managerState.update { it.copy(userFeedbackMessage = "Report deleted.") }
            loadSavedReports()
        } else {
            _managerState.update { it.copy(userFeedbackMessage = "Failed to delete report.") }
        }
    }

    fun clearFeedbackMessage() {
        _managerState.update { it.copy(userFeedbackMessage = null) }
    }
}

package com.example.history.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.history.data.UnifiedHistoryRepository
import com.example.history.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UnifiedHistoryUiState(
    val selectedTab: Int = 0, // 0 = All Timeline, 1 = Saved Reports, 2 = Favourites, 3 = Statistics
    val rawHistoryList: List<UnifiedHistoryItem> = emptyList(),
    val filteredHistoryList: List<UnifiedHistoryItem> = emptyList(),
    val pinnedList: List<UnifiedHistoryItem> = emptyList(),
    val searchQuery: String = "",
    val timeFilter: TimeFilter = TimeFilter.ALL,
    val categoryFilter: ReportCategory = ReportCategory.ALL,
    val vehicleFilter: String = "",
    val stats: HistoryStats = HistoryStats(),
    val showRenameDialog: Boolean = false,
    val itemToRename: UnifiedHistoryItem? = null,
    val renameInputText: String = ""
)

class UnifiedHistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UnifiedHistoryUiState())
    val uiState: StateFlow<UnifiedHistoryUiState> = _uiState.asStateFlow()

    private var repository: UnifiedHistoryRepository? = null
    private val exportEngine = RideWorthExportEngine()

    fun init(context: Context) {
        if (repository == null) {
            val repo = UnifiedHistoryRepository(context.applicationContext)
            repository = repo

            viewModelScope.launch {
                repo.checkAndSeedInitialHistory()
                combine(repo.historyFlow, repo.pinnedHistoryFlow) { all, pinned ->
                    Pair(all, pinned)
                }.collect { (all, pinned) ->
                    val stats = repo.generateOfflineStats()
                    _uiState.update { state ->
                        val filtered = repo.filterHistory(
                            list = all,
                            searchQuery = state.searchQuery,
                            timeFilter = state.timeFilter,
                            categoryFilter = state.categoryFilter,
                            vehicleFilter = state.vehicleFilter
                        )
                        state.copy(
                            rawHistoryList = all,
                            filteredHistoryList = filtered,
                            pinnedList = pinned,
                            stats = stats
                        )
                    }
                }
            }
        }
    }

    private fun applyFilters() {
        val repo = repository ?: return
        val current = _uiState.value
        val filtered = repo.filterHistory(
            list = current.rawHistoryList,
            searchQuery = current.searchQuery,
            timeFilter = current.timeFilter,
            categoryFilter = current.categoryFilter,
            vehicleFilter = current.vehicleFilter
        )
        _uiState.update { it.copy(filteredHistoryList = filtered) }
    }

    fun setTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun updateTimeFilter(filter: TimeFilter) {
        _uiState.update { it.copy(timeFilter = filter) }
        applyFilters()
    }

    fun updateCategoryFilter(filter: ReportCategory) {
        _uiState.update { it.copy(categoryFilter = filter) }
        applyFilters()
    }

    fun updateVehicleFilter(vehicleName: String) {
        _uiState.update { it.copy(vehicleFilter = vehicleName) }
        applyFilters()
    }

    fun togglePin(item: UnifiedHistoryItem) {
        viewModelScope.launch {
            repository?.togglePinRecord(item.id, item.isPinned)
        }
    }

    fun openRenameDialog(item: UnifiedHistoryItem) {
        _uiState.update {
            it.copy(
                showRenameDialog = true,
                itemToRename = item,
                renameInputText = item.title
            )
        }
    }

    fun closeRenameDialog() {
        _uiState.update {
            it.copy(
                showRenameDialog = false,
                itemToRename = null,
                renameInputText = ""
            )
        }
    }

    fun updateRenameInputText(text: String) {
        _uiState.update { it.copy(renameInputText = text) }
    }

    fun saveRename() {
        val target = _uiState.value.itemToRename ?: return
        val newTitle = _uiState.value.renameInputText.trim()
        if (newTitle.isNotBlank()) {
            viewModelScope.launch {
                repository?.renameRecord(target.id, newTitle)
                closeRenameDialog()
            }
        }
    }

    fun duplicateRecord(id: String) {
        viewModelScope.launch {
            repository?.duplicateRecord(id)
        }
    }

    fun deleteRecord(id: String) {
        viewModelScope.launch {
            repository?.deleteRecord(id)
        }
    }

    fun getShareSummaryText(item: UnifiedHistoryItem): String {
        return exportEngine.generateTextSummary(item)
    }

    fun saveNewReport(
        title: String,
        subtitle: String,
        vehicleName: String,
        nickname: String,
        reportType: ReportCategory,
        actionSummary: String
    ) {
        viewModelScope.launch {
            val newItem = UnifiedHistoryItem(
                title = title,
                subtitle = subtitle,
                vehicleName = vehicleName,
                nickname = nickname,
                reportType = reportType,
                actionSummary = actionSummary
            )
            repository?.saveReportRecord(newItem)
        }
    }
}

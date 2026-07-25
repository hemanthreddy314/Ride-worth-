package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.RideWorthDatabase
import com.example.data.database.entities.CatalogSearchResult
import com.example.data.database.entities.FuturePriceHistoryEntity
import com.example.data.database.entities.ManufacturerEntity
import com.example.data.database.entities.ModelEntity
import com.example.data.database.entities.VariantEntity
import com.example.data.database.entities.VehiclePricingEntity
import com.example.data.database.entities.VehicleSpecificationsEntity
import com.example.data.repository.VehicleCatalogRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface CatalogUiState<out T> {
    data object Loading : CatalogUiState<Nothing>
    data class Success<out T>(val data: T) : CatalogUiState<T>
    data class Error(val message: String) : CatalogUiState<Nothing>
}

data class SelectedVehicleState(
    val manufacturer: ManufacturerEntity? = null,
    val model: ModelEntity? = null,
    val variant: VariantEntity? = null,
    val specifications: VehicleSpecificationsEntity? = null,
    val pricing: VehiclePricingEntity? = null,
    val priceHistory: List<FuturePriceHistoryEntity> = emptyList()
)

class VehicleCatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VehicleCatalogRepository

    init {
        val db = RideWorthDatabase.getDatabase(application)
        repository = VehicleCatalogRepository(
            manufacturerDao = db.manufacturerDao(),
            modelDao = db.vehicleModelDao(),
            variantDao = db.vehicleVariantDao(),
            catalogDao = db.vehicleCatalogDao()
        )
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty(application)
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>("CAR") // CAR or BIKE or null
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedVehicle = MutableStateFlow(SelectedVehicleState())
    val selectedVehicle: StateFlow<SelectedVehicleState> = _selectedVehicle.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val manufacturersState: StateFlow<CatalogUiState<List<ManufacturerEntity>>> = combine(
        _searchQuery,
        _selectedCategory
    ) { query, category ->
        Pair(query, category)
    }.flatMapLatest { (query, category) ->
        if (query.isBlank()) {
            repository.getAllManufacturers(category)
        } else {
            repository.searchManufacturers(query, category)
        }
    }.map { list ->
        CatalogUiState.Success(list) as CatalogUiState<List<ManufacturerEntity>>
    }.catch { e ->
        emit(CatalogUiState.Error(e.localizedMessage ?: "Failed to load manufacturers"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CatalogUiState.Loading
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val modelsState: StateFlow<CatalogUiState<List<ModelEntity>>> = combine(
        _searchQuery,
        _selectedVehicle
    ) { query, selected ->
        Pair(query, selected.manufacturer?.id)
    }.flatMapLatest { (query, manufacturerId) ->
        if (manufacturerId != null) {
            if (query.isBlank()) {
                repository.getModelsByManufacturer(manufacturerId)
            } else {
                repository.searchModels(query, manufacturerId)
            }
        } else {
            repository.searchModels(query)
        }
    }.map { list ->
        CatalogUiState.Success(list) as CatalogUiState<List<ModelEntity>>
    }.catch { e ->
        emit(CatalogUiState.Error(e.localizedMessage ?: "Failed to load vehicle models"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CatalogUiState.Loading
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResultsState: StateFlow<CatalogUiState<List<CatalogSearchResult>>> = combine(
        _searchQuery,
        _selectedCategory
    ) { query, category ->
        Pair(query, category)
    }.flatMapLatest { (query, category) ->
        repository.searchCatalog(query, category)
    }.map { list ->
        CatalogUiState.Success(list) as CatalogUiState<List<CatalogSearchResult>>
    }.catch { e ->
        emit(CatalogUiState.Error(e.localizedMessage ?: "Search failed"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CatalogUiState.Loading
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
        _selectedVehicle.value = SelectedVehicleState()
    }

    fun selectManufacturer(manufacturer: ManufacturerEntity?) {
        _selectedVehicle.value = _selectedVehicle.value.copy(
            manufacturer = manufacturer,
            model = null,
            variant = null,
            specifications = null,
            pricing = null,
            priceHistory = emptyList()
        )
    }

    fun selectModel(model: ModelEntity?) {
        _selectedVehicle.value = _selectedVehicle.value.copy(
            model = model,
            variant = null,
            specifications = null,
            pricing = null,
            priceHistory = emptyList()
        )
    }

    fun selectVariant(variant: VariantEntity?) {
        _selectedVehicle.value = _selectedVehicle.value.copy(
            variant = variant
        )
        if (variant != null) {
            observeVariantDetails(variant.id)
        }
    }

    private fun observeVariantDetails(variantId: Long) {
        viewModelScope.launch {
            repository.getSpecifications(variantId).collect { specs ->
                _selectedVehicle.value = _selectedVehicle.value.copy(specifications = specs)
            }
        }
        viewModelScope.launch {
            repository.getPricing(variantId).collect { pricing ->
                _selectedVehicle.value = _selectedVehicle.value.copy(pricing = pricing)
            }
        }
        viewModelScope.launch {
            repository.getPriceHistory(variantId).collect { history ->
                _selectedVehicle.value = _selectedVehicle.value.copy(priceHistory = history)
            }
        }
    }
}

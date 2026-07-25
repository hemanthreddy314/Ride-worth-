package com.example.garage.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.garage.data.GarageRepository
import com.example.garage.model.GarageVehicle
import com.example.garage.notifications.OfflineNotificationReminderService
import com.example.garage.notifications.VehicleReminder
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

data class GarageUiState(
    val vehicles: List<GarageVehicle> = emptyList(),
    val selectedVehicle: GarageVehicle? = null,
    val reminders: List<VehicleReminder> = emptyList(),
    val showAddVehicleSheet: Boolean = false,
    val showOdometerDialog: Boolean = false,
    val editingVehicle: GarageVehicle? = null,
    val isLoading: Boolean = false
)

class GarageViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GarageUiState())
    val uiState: StateFlow<GarageUiState> = _uiState.asStateFlow()

    private var repository: GarageRepository? = null
    private val reminderService = OfflineNotificationReminderService()

    fun init(context: Context) {
        if (repository == null) {
            val repo = GarageRepository(context.applicationContext)
            repository = repo

            viewModelScope.launch {
                repo.checkAndSeedInitialVehicles()
                repo.vehiclesFlow.collect { list ->
                    val selected = _uiState.value.selectedVehicle?.let { curr ->
                        list.find { it.id == curr.id } ?: list.firstOrNull()
                    } ?: list.firstOrNull()

                    val allReminders = list.flatMap { reminderService.generateRemindersForVehicle(it) }

                    _uiState.update {
                        it.copy(
                            vehicles = list,
                            selectedVehicle = selected,
                            reminders = allReminders
                        )
                    }
                }
            }
        }
    }

    fun selectVehicle(vehicle: GarageVehicle) {
        val vehicleReminders = reminderService.generateRemindersForVehicle(vehicle)
        _uiState.update {
            it.copy(
                selectedVehicle = vehicle,
                reminders = vehicleReminders
            )
        }
    }

    fun openAddVehicleSheet(vehicleToEdit: GarageVehicle? = null) {
        _uiState.update {
            it.copy(
                showAddVehicleSheet = true,
                editingVehicle = vehicleToEdit
            )
        }
    }

    fun closeAddVehicleSheet() {
        _uiState.update {
            it.copy(
                showAddVehicleSheet = false,
                editingVehicle = null
            )
        }
    }

    fun saveVehicle(vehicle: GarageVehicle) {
        viewModelScope.launch {
            repository?.saveVehicle(vehicle)
            closeAddVehicleSheet()
        }
    }

    fun deleteVehicle(id: String) {
        viewModelScope.launch {
            repository?.deleteVehicle(id)
        }
    }

    fun toggleFavourite(vehicle: GarageVehicle) {
        viewModelScope.launch {
            repository?.toggleFavourite(vehicle.id, vehicle.isFavourite)
        }
    }

    fun duplicateVehicle(id: String) {
        viewModelScope.launch {
            repository?.duplicateVehicle(id)
        }
    }

    fun updateOdometer(id: String, newOdometerKm: Int) {
        viewModelScope.launch {
            repository?.updateOdometer(id, newOdometerKm)
        }
    }

    fun getShareSummaryText(vehicle: GarageVehicle): String {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
            maximumFractionDigits = 0
        }
        val estVal = currencyFormat.format(vehicle.estimatedValue)

        return """
            🚘 *My Garage Vehicle Summary*
            
            *Nickname:* ${vehicle.fullDisplayName}
            *Specs:* ${vehicle.manufacturer} ${vehicle.model} ${vehicle.variant} (${vehicle.year})
            *Fuel/Transmission:* ${vehicle.fuelType} • ${vehicle.transmission}
            *Current Odometer:* ${vehicle.currentOdometerKm} km
            
            ⭐ *Health Score:* ${vehicle.healthScore}/100
            💰 *Estimated Market Value:* $estVal
            
            Managed securely offline with RideWorth Automotive.
        """.trimIndent()
    }
}

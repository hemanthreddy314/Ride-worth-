package com.example.garage.notifications

import com.example.garage.model.GarageVehicle

enum class ReminderType(val title: String, val severity: String) {
    INSURANCE_EXPIRY("Insurance Expiry", "URGENT"),
    SERVICE_DUE("Service Due", "HIGH"),
    BATTERY_REPLACEMENT("Battery Check", "MEDIUM"),
    TYRE_REPLACEMENT("Tyre Inspection", "MEDIUM")
}

data class VehicleReminder(
    val id: String,
    val vehicleId: String,
    val vehicleName: String,
    val type: ReminderType,
    val title: String,
    val message: String,
    val dueInDays: Long,
    val isActionable: Boolean = true
)

interface NotificationReminderService {
    fun generateRemindersForVehicle(vehicle: GarageVehicle): List<VehicleReminder>
    fun scheduleLocalNotification(reminder: VehicleReminder)
    fun cancelNotification(reminderId: String)
}

class OfflineNotificationReminderService : NotificationReminderService {

    override fun generateRemindersForVehicle(vehicle: GarageVehicle): List<VehicleReminder> {
        val reminders = mutableListOf<VehicleReminder>()

        // 1. Insurance Expiry
        val daysInsurance = vehicle.daysUntilInsuranceExpiry
        if (daysInsurance in 0..30) {
            reminders.add(
                VehicleReminder(
                    id = "ins_${vehicle.id}",
                    vehicleId = vehicle.id,
                    vehicleName = vehicle.fullDisplayName,
                    type = ReminderType.INSURANCE_EXPIRY,
                    title = "Insurance Expiring Soon",
                    message = "${vehicle.fullDisplayName} insurance expires in $daysInsurance days. Renew early to avoid penalties.",
                    dueInDays = daysInsurance
                )
            )
        }

        // 2. Service Due (every 10,000 km)
        val kmToNextService = 10000 - (vehicle.currentOdometerKm % 10000)
        if (kmToNextService <= 1500) {
            val estimatedDays = (kmToNextService / 30).toLong().coerceAtLeast(3)
            reminders.add(
                VehicleReminder(
                    id = "service_${vehicle.id}",
                    vehicleId = vehicle.id,
                    vehicleName = vehicle.fullDisplayName,
                    type = ReminderType.SERVICE_DUE,
                    title = "Scheduled Maintenance Due",
                    message = "${vehicle.fullDisplayName} is due for routine service in ~$kmToNextService km.",
                    dueInDays = estimatedDays
                )
            )
        }

        // 3. Tyre Age (> 36 months)
        if (vehicle.tyreAgeMonths >= 36) {
            reminders.add(
                VehicleReminder(
                    id = "tyre_${vehicle.id}",
                    vehicleId = vehicle.id,
                    vehicleName = vehicle.fullDisplayName,
                    type = ReminderType.TYRE_REPLACEMENT,
                    title = "Tyre Wear Inspection",
                    message = "Tyres on ${vehicle.fullDisplayName} are ${vehicle.tyreAgeMonths} months old. Inspect tread depth.",
                    dueInDays = 14
                )
            )
        }

        // 4. Battery Check (vehicle year > 3 years old)
        val ageYears = 2026 - vehicle.year
        if (ageYears >= 3) {
            reminders.add(
                VehicleReminder(
                    id = "bat_${vehicle.id}",
                    vehicleId = vehicle.id,
                    vehicleName = vehicle.fullDisplayName,
                    type = ReminderType.BATTERY_REPLACEMENT,
                    title = "Battery Health Check",
                    message = "12V battery health check recommended for ${vehicle.year} ${vehicle.fullDisplayName}.",
                    dueInDays = 30
                )
            )
        }

        return reminders
    }

    override fun scheduleLocalNotification(reminder: VehicleReminder) {
        // Prepared architecture interface for system AlarmManager / NotificationManager
    }

    override fun cancelNotification(reminderId: String) {
        // Prepared architecture interface
    }
}

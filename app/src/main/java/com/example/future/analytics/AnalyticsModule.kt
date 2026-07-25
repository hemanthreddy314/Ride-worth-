package com.example.future.analytics

interface AppAnalytics {
    fun logEvent(eventName: String, params: Map<String, String> = emptyMap())
    fun logVehicleSelected(vehicleType: String)
}

class AnalyticsModulePlaceholder : AppAnalytics {
    override fun logEvent(eventName: String, params: Map<String, String>) {}
    override fun logVehicleSelected(vehicleType: String) {}
}

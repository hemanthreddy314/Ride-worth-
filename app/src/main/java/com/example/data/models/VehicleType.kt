package com.example.data.models

enum class VehicleType(val title: String, val subtitle: String, val iconResName: String) {
    CAR(
        title = "Car",
        subtitle = "Hatchback, Sedan, SUV, Luxury",
        iconResName = "directions_car"
    ),
    BIKE(
        title = "Bike",
        subtitle = "Motorcycle, Scooter, Superbike",
        iconResName = "two_wheeler"
    )
}

package com.example.data.database

import com.example.data.database.entities.FuturePriceHistoryEntity
import com.example.data.database.entities.ManufacturerEntity
import com.example.data.database.entities.ModelEntity
import com.example.data.database.entities.VariantEntity
import com.example.data.database.entities.VehiclePricingEntity
import com.example.data.database.entities.VehicleSpecificationsEntity

object InitialCatalogData {

    val manufacturers = listOf(
        // CAR BRANDS
        ManufacturerEntity(id = 1, name = "Maruti Suzuki", code = "MSIL", country = "India / Japan", category = "CAR", isPopular = true, displayOrder = 1),
        ManufacturerEntity(id = 2, name = "Hyundai", code = "HYU", country = "South Korea", category = "CAR", isPopular = true, displayOrder = 2),
        ManufacturerEntity(id = 3, name = "Tata Motors", code = "TATA", country = "India", category = "CAR", isPopular = true, displayOrder = 3),
        ManufacturerEntity(id = 4, name = "Mahindra", code = "M&M", country = "India", category = "CAR", isPopular = true, displayOrder = 4),
        ManufacturerEntity(id = 5, name = "Toyota", code = "TOY", country = "Japan", category = "CAR", isPopular = true, displayOrder = 5),
        ManufacturerEntity(id = 6, name = "Honda", code = "HON", country = "Japan", category = "CAR", isPopular = true, displayOrder = 6),
        ManufacturerEntity(id = 7, name = "Kia", code = "KIA", country = "South Korea", category = "CAR", isPopular = true, displayOrder = 7),
        ManufacturerEntity(id = 8, name = "MG Motor", code = "MG", country = "UK / China", category = "CAR", isPopular = true, displayOrder = 8),
        ManufacturerEntity(id = 9, name = "Volkswagen", code = "VW", country = "Germany", category = "CAR", isPopular = true, displayOrder = 9),
        ManufacturerEntity(id = 10, name = "Skoda", code = "SKO", country = "Czech Republic", category = "CAR", isPopular = true, displayOrder = 10),
        ManufacturerEntity(id = 11, name = "Renault", code = "REN", country = "France", category = "CAR", isPopular = false, displayOrder = 11),
        ManufacturerEntity(id = 12, name = "Nissan", code = "NIS", country = "Japan", category = "CAR", isPopular = false, displayOrder = 12),
        ManufacturerEntity(id = 13, name = "Jeep", code = "JEP", country = "USA", category = "CAR", isPopular = false, displayOrder = 13),
        ManufacturerEntity(id = 14, name = "Citroen", code = "CIT", country = "France", category = "CAR", isPopular = false, displayOrder = 14),
        ManufacturerEntity(id = 15, name = "BYD", code = "BYD", country = "China", category = "CAR", isPopular = false, displayOrder = 15),
        ManufacturerEntity(id = 16, name = "BMW", code = "BMW", country = "Germany", category = "CAR", isPopular = true, displayOrder = 16),
        ManufacturerEntity(id = 17, name = "Mercedes-Benz", code = "BENZ", country = "Germany", category = "CAR", isPopular = true, displayOrder = 17),
        ManufacturerEntity(id = 18, name = "Audi", code = "AUDI", country = "Germany", category = "CAR", isPopular = true, displayOrder = 18),
        ManufacturerEntity(id = 19, name = "Volvo", code = "VOL", country = "Sweden", category = "CAR", isPopular = false, displayOrder = 19),
        ManufacturerEntity(id = 20, name = "Lexus", code = "LEX", country = "Japan", category = "CAR", isPopular = false, displayOrder = 20),
        ManufacturerEntity(id = 21, name = "Mini", code = "MINI", country = "UK / Germany", category = "CAR", isPopular = false, displayOrder = 21),
        ManufacturerEntity(id = 22, name = "Land Rover", code = "LR", country = "UK / India", category = "CAR", isPopular = true, displayOrder = 22),
        ManufacturerEntity(id = 23, name = "Jaguar", code = "JAG", country = "UK / India", category = "CAR", isPopular = false, displayOrder = 23),
        ManufacturerEntity(id = 24, name = "Porsche", code = "POR", country = "Germany", category = "CAR", isPopular = false, displayOrder = 24),
        ManufacturerEntity(id = 25, name = "Ferrari", code = "FER", country = "Italy", category = "CAR", isPopular = false, displayOrder = 25),
        ManufacturerEntity(id = 26, name = "Lamborghini", code = "LAMB", country = "Italy", category = "CAR", isPopular = false, displayOrder = 26),
        ManufacturerEntity(id = 27, name = "Rolls-Royce", code = "RR", country = "UK", category = "CAR", isPopular = false, displayOrder = 27),
        ManufacturerEntity(id = 28, name = "Bentley", code = "BEN", country = "UK", category = "CAR", isPopular = false, displayOrder = 28),
        ManufacturerEntity(id = 29, name = "Aston Martin", code = "AM", country = "UK", category = "CAR", isPopular = false, displayOrder = 29),
        ManufacturerEntity(id = 30, name = "Maserati", code = "MAS", country = "Italy", category = "CAR", isPopular = false, displayOrder = 30),
        ManufacturerEntity(id = 31, name = "Force Motors", code = "FRC", country = "India", category = "CAR", isPopular = false, displayOrder = 31),
        ManufacturerEntity(id = 32, name = "Isuzu", code = "ISU", country = "Japan", category = "CAR", isPopular = false, displayOrder = 32),

        // BIKE BRANDS
        ManufacturerEntity(id = 101, name = "Hero MotoCorp", code = "HERO", country = "India", category = "BIKE", isPopular = true, displayOrder = 101),
        ManufacturerEntity(id = 102, name = "Honda 2Wheelers", code = "HMSI", country = "Japan", category = "BIKE", isPopular = true, displayOrder = 102),
        ManufacturerEntity(id = 103, name = "TVS Motors", code = "TVS", country = "India", category = "BIKE", isPopular = true, displayOrder = 103),
        ManufacturerEntity(id = 104, name = "Bajaj Auto", code = "BAJ", country = "India", category = "BIKE", isPopular = true, displayOrder = 104),
        ManufacturerEntity(id = 105, name = "Royal Enfield", code = "RE", country = "India / UK", category = "BIKE", isPopular = true, displayOrder = 105),
        ManufacturerEntity(id = 106, name = "Yamaha", code = "YAM", country = "Japan", category = "BIKE", isPopular = true, displayOrder = 106),
        ManufacturerEntity(id = 107, name = "Suzuki 2Wheelers", code = "SUZ2W", country = "Japan", category = "BIKE", isPopular = true, displayOrder = 107),
        ManufacturerEntity(id = 108, name = "KTM", code = "KTM", country = "Austria", category = "BIKE", isPopular = true, displayOrder = 108),
        ManufacturerEntity(id = 109, name = "Kawasaki", code = "KAW", country = "Japan", category = "BIKE", isPopular = false, displayOrder = 109),
        ManufacturerEntity(id = 110, name = "Triumph", code = "TRI", country = "UK", category = "BIKE", isPopular = false, displayOrder = 110),
        ManufacturerEntity(id = 111, name = "Harley-Davidson", code = "HD", country = "USA", category = "BIKE", isPopular = false, displayOrder = 111),
        ManufacturerEntity(id = 112, name = "BMW Motorrad", code = "BMWM", country = "Germany", category = "BIKE", isPopular = false, displayOrder = 112),
        ManufacturerEntity(id = 113, name = "Ducati", code = "DUC", country = "Italy", category = "BIKE", isPopular = false, displayOrder = 113),
        ManufacturerEntity(id = 114, name = "Aprilia", code = "APR", country = "Italy", category = "BIKE", isPopular = false, displayOrder = 114),
        ManufacturerEntity(id = 115, name = "Benelli", code = "BEN", country = "Italy / China", category = "BIKE", isPopular = false, displayOrder = 115),
        ManufacturerEntity(id = 116, name = "Jawa", code = "JAWA", country = "India / Czech", category = "BIKE", isPopular = false, displayOrder = 116),
        ManufacturerEntity(id = 117, name = "Yezdi", code = "YEZ", country = "India", category = "BIKE", isPopular = false, displayOrder = 117),
        ManufacturerEntity(id = 118, name = "Keeway", code = "KEE", country = "Hungary / China", category = "BIKE", isPopular = false, displayOrder = 118),
        ManufacturerEntity(id = 119, name = "Moto Guzzi", code = "MGZ", country = "Italy", category = "BIKE", isPopular = false, displayOrder = 119),
        ManufacturerEntity(id = 120, name = "CFMoto", code = "CFM", country = "China", category = "BIKE", isPopular = false, displayOrder = 120)
    )

    val models = listOf(
        // Honda Cars
        ModelEntity(id = 1, manufacturerId = 6, name = "City", category = "CAR", bodyType = "Sedan", launchYear = 1998, popularityScore = 95, resaleScore = 92, reliabilityScore = 96, maintenanceCategory = "Low"),
        ModelEntity(id = 2, manufacturerId = 6, name = "Amaze", category = "CAR", bodyType = "Sedan", launchYear = 2013, popularityScore = 88, resaleScore = 86, reliabilityScore = 94, maintenanceCategory = "Low"),
        ModelEntity(id = 3, manufacturerId = 6, name = "Elevate", category = "CAR", bodyType = "SUV", launchYear = 2023, popularityScore = 90, resaleScore = 88, reliabilityScore = 95, maintenanceCategory = "Low"),

        // Hyundai Cars
        ModelEntity(id = 4, manufacturerId = 2, name = "Creta", category = "CAR", bodyType = "SUV", launchYear = 2015, popularityScore = 98, resaleScore = 95, reliabilityScore = 92, maintenanceCategory = "Moderate"),
        ModelEntity(id = 5, manufacturerId = 2, name = "i20", category = "CAR", bodyType = "Hatchback", launchYear = 2008, popularityScore = 90, resaleScore = 88, reliabilityScore = 90, maintenanceCategory = "Low"),
        ModelEntity(id = 6, manufacturerId = 2, name = "Verna", category = "CAR", bodyType = "Sedan", launchYear = 2006, popularityScore = 87, resaleScore = 84, reliabilityScore = 91, maintenanceCategory = "Moderate"),

        // Maruti Suzuki
        ModelEntity(id = 7, manufacturerId = 1, name = "Swift", category = "CAR", bodyType = "Hatchback", launchYear = 2005, popularityScore = 99, resaleScore = 97, reliabilityScore = 95, maintenanceCategory = "Low"),
        ModelEntity(id = 8, manufacturerId = 1, name = "Baleno", category = "CAR", bodyType = "Hatchback", launchYear = 2015, popularityScore = 96, resaleScore = 94, reliabilityScore = 93, maintenanceCategory = "Low"),
        ModelEntity(id = 9, manufacturerId = 1, name = "Brezza", category = "CAR", bodyType = "SUV", launchYear = 2016, popularityScore = 95, resaleScore = 93, reliabilityScore = 94, maintenanceCategory = "Low"),

        // Tata
        ModelEntity(id = 10, manufacturerId = 3, name = "Nexon", category = "CAR", bodyType = "SUV", launchYear = 2017, popularityScore = 97, resaleScore = 90, reliabilityScore = 89, maintenanceCategory = "Low"),
        ModelEntity(id = 11, manufacturerId = 3, name = "Harrier", category = "CAR", bodyType = "SUV", launchYear = 2019, popularityScore = 89, resaleScore = 85, reliabilityScore = 87, maintenanceCategory = "Moderate"),

        // Mahindra
        ModelEntity(id = 12, manufacturerId = 4, name = "Thar", category = "CAR", bodyType = "SUV", launchYear = 2010, popularityScore = 96, resaleScore = 95, reliabilityScore = 88, maintenanceCategory = "Moderate"),
        ModelEntity(id = 13, manufacturerId = 4, name = "XUV700", category = "CAR", bodyType = "SUV", launchYear = 2021, popularityScore = 96, resaleScore = 91, reliabilityScore = 89, maintenanceCategory = "Moderate"),

        // Royal Enfield Bikes
        ModelEntity(id = 101, manufacturerId = 105, name = "Classic 350", category = "BIKE", bodyType = "Cruiser", launchYear = 2009, popularityScore = 99, resaleScore = 96, reliabilityScore = 90, maintenanceCategory = "Low"),
        ModelEntity(id = 102, manufacturerId = 105, name = "Hunter 350", category = "BIKE", bodyType = "Roadster", launchYear = 2022, popularityScore = 94, resaleScore = 91, reliabilityScore = 92, maintenanceCategory = "Low"),
        ModelEntity(id = 103, manufacturerId = 105, name = "Himalayan 450", category = "BIKE", bodyType = "Tourer", launchYear = 2023, popularityScore = 92, resaleScore = 89, reliabilityScore = 88, maintenanceCategory = "Moderate"),

        // Honda Bikes
        ModelEntity(id = 104, manufacturerId = 102, name = "Activa 6G", category = "BIKE", bodyType = "Scooter", launchYear = 2001, popularityScore = 100, resaleScore = 98, reliabilityScore = 98, maintenanceCategory = "Low"),
        ModelEntity(id = 105, manufacturerId = 102, name = "Shine 125", category = "BIKE", bodyType = "Commuter", launchYear = 2006, popularityScore = 95, resaleScore = 94, reliabilityScore = 97, maintenanceCategory = "Low"),

        // Yamaha Bikes
        ModelEntity(id = 106, manufacturerId = 106, name = "R15 V4", category = "BIKE", bodyType = "Sports", launchYear = 2008, popularityScore = 97, resaleScore = 93, reliabilityScore = 94, maintenanceCategory = "Moderate"),

        // TVS Bikes
        ModelEntity(id = 107, manufacturerId = 103, name = "Apache RTR 160", category = "BIKE", bodyType = "Sports", launchYear = 2006, popularityScore = 95, resaleScore = 90, reliabilityScore = 91, maintenanceCategory = "Low")
    )

    val variants = listOf(
        // Honda City
        VariantEntity(id = 1, modelId = 1, name = "V MT Petrol", trimLevel = "Base", fuelType = "Petrol", transmission = "Manual", engineCapacityCc = 1498, mileageKmpl = 17.8f, approxExShowroomPrice = 1180000, approxOnRoadPrice = 1380000, fuelTankCapacityL = 40f, launchYear = 2020),
        VariantEntity(id = 2, modelId = 1, name = "VX CVT Petrol", trimLevel = "Mid", fuelType = "Petrol", transmission = "Automatic", engineCapacityCc = 1498, mileageKmpl = 18.4f, approxExShowroomPrice = 1360000, approxOnRoadPrice = 1580000, fuelTankCapacityL = 40f, launchYear = 2020),
        VariantEntity(id = 3, modelId = 1, name = "ZX CVT Petrol", trimLevel = "Top", fuelType = "Petrol", transmission = "Automatic", engineCapacityCc = 1498, mileageKmpl = 18.4f, approxExShowroomPrice = 1490000, approxOnRoadPrice = 1720000, fuelTankCapacityL = 40f, launchYear = 2020),

        // Hyundai Creta
        VariantEntity(id = 4, modelId = 4, name = "EX 1.5 Petrol", trimLevel = "Base", fuelType = "Petrol", transmission = "Manual", engineCapacityCc = 1497, mileageKmpl = 17.4f, approxExShowroomPrice = 1100000, approxOnRoadPrice = 1290000, fuelTankCapacityL = 50f, launchYear = 2024),
        VariantEntity(id = 5, modelId = 4, name = "SX (O) 1.5 Diesel AT", trimLevel = "Top", fuelType = "Diesel", transmission = "Automatic", engineCapacityCc = 1493, mileageKmpl = 19.1f, approxExShowroomPrice = 2010000, approxOnRoadPrice = 2360000, fuelTankCapacityL = 50f, launchYear = 2024),

        // Maruti Swift
        VariantEntity(id = 6, modelId = 7, name = "LXi 1.2 Petrol", trimLevel = "Base", fuelType = "Petrol", transmission = "Manual", engineCapacityCc = 1197, mileageKmpl = 24.8f, approxExShowroomPrice = 649000, approxOnRoadPrice = 740000, fuelTankCapacityL = 37f, launchYear = 2024),
        VariantEntity(id = 7, modelId = 7, name = "ZXi+ AMT Petrol", trimLevel = "Top", fuelType = "Petrol", transmission = "Automatic", engineCapacityCc = 1197, mileageKmpl = 25.7f, approxExShowroomPrice = 950000, approxOnRoadPrice = 1080000, fuelTankCapacityL = 37f, launchYear = 2024),

        // Royal Enfield Classic 350
        VariantEntity(id = 101, modelId = 101, name = "Halcyon Single Channel ABS", trimLevel = "Base", fuelType = "Petrol", transmission = "Manual 5-Speed", engineCapacityCc = 349, mileageKmpl = 36.2f, approxExShowroomPrice = 193000, approxOnRoadPrice = 220000, fuelTankCapacityL = 13f, launchYear = 2021),
        VariantEntity(id = 102, modelId = 101, name = "Dark Stealth Black Dual ABS", trimLevel = "Top", fuelType = "Petrol", transmission = "Manual 5-Speed", engineCapacityCc = 349, mileageKmpl = 36.2f, approxExShowroomPrice = 225000, approxOnRoadPrice = 258000, fuelTankCapacityL = 13f, launchYear = 2021)
    )

    val specifications = listOf(
        VehicleSpecificationsEntity(id = 1, variantId = 1, seatingCapacity = 5, displacementCc = 1498, maxPowerBhp = 119.35f, maxTorqueNm = 145f, transmissionType = "6-Speed Manual", driveType = "FWD", emissionNorms = "BS6 Phase 2", bootSpaceL = 506, groundClearanceMm = 165),
        VehicleSpecificationsEntity(id = 2, variantId = 3, seatingCapacity = 5, displacementCc = 1498, maxPowerBhp = 119.35f, maxTorqueNm = 145f, transmissionType = "7-Speed CVT", driveType = "FWD", emissionNorms = "BS6 Phase 2", bootSpaceL = 506, groundClearanceMm = 165),
        VehicleSpecificationsEntity(id = 3, variantId = 5, seatingCapacity = 5, displacementCc = 1493, maxPowerBhp = 114f, maxTorqueNm = 250f, transmissionType = "6-Speed Torque Converter", driveType = "FWD", emissionNorms = "BS6 Phase 2", bootSpaceL = 433, groundClearanceMm = 190),
        VehicleSpecificationsEntity(id = 101, variantId = 101, seatingCapacity = 2, displacementCc = 349, maxPowerBhp = 20.2f, maxTorqueNm = 27f, transmissionType = "5-Speed Constant Mesh", driveType = "Chain Drive", emissionNorms = "BS6 Phase 2", bootSpaceL = 0, groundClearanceMm = 170)
    )

    val pricings = listOf(
        VehiclePricingEntity(id = 1, variantId = 1, stateCode = "MH", exShowroomPrice = 1180000, onRoadPrice = 1380000, baseDepreciationRate = 0.09f),
        VehiclePricingEntity(id = 2, variantId = 3, stateCode = "MH", exShowroomPrice = 1490000, onRoadPrice = 1720000, baseDepreciationRate = 0.09f),
        VehiclePricingEntity(id = 3, variantId = 5, stateCode = "MH", exShowroomPrice = 2010000, onRoadPrice = 2360000, baseDepreciationRate = 0.08f),
        VehiclePricingEntity(id = 101, variantId = 101, stateCode = "MH", exShowroomPrice = 193000, onRoadPrice = 220000, baseDepreciationRate = 0.07f)
    )

    val priceHistories = listOf(
        FuturePriceHistoryEntity(id = 1, variantId = 3, year = 2024, month = 1, averageResaleValue = 1380000, marketDemandScore = 92),
        FuturePriceHistoryEntity(id = 2, variantId = 3, year = 2025, month = 1, averageResaleValue = 1250000, marketDemandScore = 90),
        FuturePriceHistoryEntity(id = 3, variantId = 3, year = 2026, month = 1, averageResaleValue = 1120000, marketDemandScore = 88),
        FuturePriceHistoryEntity(id = 101, variantId = 101, year = 2024, month = 1, averageResaleValue = 185000, marketDemandScore = 95),
        FuturePriceHistoryEntity(id = 102, variantId = 101, year = 2025, month = 1, averageResaleValue = 170000, marketDemandScore = 93)
    )
}

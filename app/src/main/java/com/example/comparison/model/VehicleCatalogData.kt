package com.example.comparison.model

object VehicleCatalogData {

    val carBrands = listOf(
        "Honda", "Hyundai", "Maruti Suzuki", "Toyota", "Tata Motors",
        "Mahindra", "Kia", "Volkswagen", "Skoda", "BMW", "Mercedes-Benz",
        "Audi", "Volvo", "Jeep", "MG Motor"
    )

    val bikeBrands = listOf(
        "Honda", "Royal Enfield", "Yamaha", "TVS", "Bajaj", "Hero",
        "KTM", "Suzuki", "Kawasaki", "BMW Motorrad", "Triumph",
        "Ather", "Ola Electric"
    )

    private val carModels = mapOf(
        "Honda" to listOf("City", "Amaze", "Elevate", "Civic", "CR-V", "WR-V"),
        "Hyundai" to listOf("Creta", "Venue", "i20", "Verna", "Tucson", "Alcazar", "Aura"),
        "Maruti Suzuki" to listOf("Swift", "Baleno", "Brezza", "Dzire", "Ertiga", "WagonR", "Grand Vitara"),
        "Toyota" to listOf("Innova Crysta", "Fortuner", "Urban Cruiser Hyryder", "Glanza", "Hilux", "Camry"),
        "Tata Motors" to listOf("Nexon", "Punch", "Harrier", "Safari", "Tiago", "Altroz"),
        "Mahindra" to listOf("Thar", "XUV700", "Scorpio-N", "Bolero", "XUV300"),
        "Kia" to listOf("Seltos", "Sonet", "Carens", "EV6")
    )

    private val bikeModels = mapOf(
        "Honda" to listOf("Activa 6G", "Shine 125", "Unicorn", "Hornet 2.0", "CB350", "Dio"),
        "Royal Enfield" to listOf("Classic 350", "Hunter 350", "Bullet 350", "Meteor 350", "Himalayan 450", "Continental GT 650"),
        "Yamaha" to listOf("R15 V4", "MT-15 V2", "FZ-S", "RayZR 125", "Aerox 155"),
        "TVS" to listOf("Apache RTR 160", "Jupiter 125", "Ntorq 125", "Rider 125", "Ronin"),
        "Bajaj" to listOf("Pulsar N160", "Pulsar 150", "Dominar 400", "Chetak Electric", "Avenger 220"),
        "Hero" to listOf("Splendor Plus", "HF Deluxe", "Xpulse 200 4V", "Xtreme 160R", "Mavrick 440"),
        "KTM" to listOf("Duke 200", "Duke 390", "RC 200", "Adventure 390")
    )

    val years = (2024 downTo 2012).toList()
    val fuelTypes = listOf("Petrol", "Diesel", "CNG", "Electric", "Hybrid")
    val transmissions = listOf("Manual", "Automatic CVT", "Automatic Torque Converter", "AMT", "DCT")

    fun getBrands(isBike: Boolean): List<String> {
        return if (isBike) bikeBrands else carBrands
    }

    fun getModels(brand: String, isBike: Boolean): List<String> {
        val map = if (isBike) bikeModels else carModels
        return map[brand] ?: listOf("Standard Model", "Executive", "Sport")
    }

    fun getVariants(model: String, isBike: Boolean): List<String> {
        return if (isBike) {
            listOf("Standard", "Disc", "Alloy", "ABS", "Special Edition")
        } else {
            listOf("Base LXi", "Mid VXi", "Top ZXi", "ZXi Plus", "Automatic ZX")
        }
    }
}

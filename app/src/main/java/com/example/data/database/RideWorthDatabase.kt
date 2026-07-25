package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.database.dao.ManufacturerDao
import com.example.data.database.dao.VehicleCatalogDao
import com.example.data.database.dao.VehicleModelDao
import com.example.data.database.dao.VehicleVariantDao
import com.example.data.database.entities.FuturePriceHistoryEntity
import com.example.data.database.entities.ManufacturerEntity
import com.example.data.database.entities.ModelEntity
import com.example.data.database.entities.VariantEntity
import com.example.data.database.entities.VehiclePricingEntity
import com.example.data.database.entities.VehicleSpecificationsEntity
import com.example.data.database.service.AssetCatalogImporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.example.maintenance.data.MaintenanceDao
import com.example.maintenance.data.MaintenanceEntity
import com.example.garage.data.GarageDao
import com.example.garage.data.GarageVehicleEntity
import com.example.history.data.UnifiedHistoryDao
import com.example.history.data.UnifiedHistoryEntity

@Database(
    entities = [
        ValuationEntity::class,
        ManufacturerEntity::class,
        ModelEntity::class,
        VariantEntity::class,
        VehiclePricingEntity::class,
        VehicleSpecificationsEntity::class,
        FuturePriceHistoryEntity::class,
        MaintenanceEntity::class,
        GarageVehicleEntity::class,
        UnifiedHistoryEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class RideWorthDatabase : RoomDatabase() {
    abstract fun valuationDao(): ValuationDao
    abstract fun manufacturerDao(): ManufacturerDao
    abstract fun vehicleModelDao(): VehicleModelDao
    abstract fun vehicleVariantDao(): VehicleVariantDao
    abstract fun vehicleCatalogDao(): VehicleCatalogDao
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun garageDao(): GarageDao
    abstract fun unifiedHistoryDao(): UnifiedHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: RideWorthDatabase? = null

        fun getDatabase(context: Context): RideWorthDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RideWorthDatabase::class.java,
                    "rideworth_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(context, database)
                    }
                }
            }

            suspend fun populateDatabase(context: Context, db: RideWorthDatabase) {
                AssetCatalogImporter.importCatalogIfNeeded(context, db)
            }
        }
    }
}


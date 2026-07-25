package com.example.garage.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GarageDao {
    @Query("SELECT * FROM garage_vehicles ORDER BY isFavourite DESC, createdAtMillis DESC")
    fun getAllVehicles(): Flow<List<GarageVehicleEntity>>

    @Query("SELECT * FROM garage_vehicles WHERE id = :id LIMIT 1")
    fun getVehicleById(id: String): Flow<GarageVehicleEntity?>

    @Query("SELECT * FROM garage_vehicles WHERE id = :id LIMIT 1")
    suspend fun getVehicleByIdOnce(id: String): GarageVehicleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: GarageVehicleEntity)

    @Update
    suspend fun updateVehicle(vehicle: GarageVehicleEntity)

    @Query("DELETE FROM garage_vehicles WHERE id = :id")
    suspend fun deleteVehicleById(id: String)

    @Query("DELETE FROM garage_vehicles")
    suspend fun deleteAllVehicles()

    @Query("UPDATE garage_vehicles SET isFavourite = :isFav WHERE id = :id")
    suspend fun setFavourite(id: String, isFav: Boolean)

    @Query("UPDATE garage_vehicles SET currentOdometerKm = :odometerKm WHERE id = :id")
    suspend fun updateOdometer(id: String, odometerKm: Int)
}

package com.example

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.example.data.database.RideWorthDatabase
import com.example.data.repository.ValuationRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RideWorthApplication : Application() {
    companion object {
        @Volatile
        private var INSTANCE: RideWorthApplication? = null
        
        var database: RideWorthDatabase? = null
        var valuationRepository: ValuationRepositoryImpl? = null
        var initializationError: Exception? = null

        fun getInstance(): RideWorthApplication {
            return INSTANCE ?: throw RuntimeException("RideWorthApplication not initialized")
        }

        fun getDatabase(): RideWorthDatabase? = database
        fun getRepository(): ValuationRepositoryImpl? = valuationRepository
        fun hasInitializationError(): Boolean = initializationError != null
        fun getInitializationError(): Exception? = initializationError
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        initializeApplication()
    }

    private fun initializeApplication() {
        applicationScope.launch(Dispatchers.IO) {
            try {
                Log.d("RideWorthApp", "Initializing RideWorth Application...")
                
                // Initialize database
                database = try {
                    RideWorthDatabase.getDatabase(applicationContext)
                } catch (e: Exception) {
                    Log.e("RideWorthApp", "Database initialization failed: ${e.message}", e)
                    initializationError = e
                    null
                }

                // Initialize repository
                database?.let {
                    valuationRepository = try {
                        ValuationRepositoryImpl(it.valuationDao())
                    } catch (e: Exception) {
                        Log.e("RideWorthApp", "Repository initialization failed: ${e.message}", e)
                        initializationError = e
                        null
                    }
                }

                if (hasInitializationError()) {
                    Log.e("RideWorthApp", "Application initialization completed with errors")
                } else {
                    Log.d("RideWorthApp", "Application initialized successfully")
                }
            } catch (e: Exception) {
                Log.e("RideWorthApp", "Fatal application initialization error: ${e.message}", e)
                initializationError = e
            }
        }
    }
}

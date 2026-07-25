package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.RideWorthDatabase
import com.example.data.database.service.AssetCatalogImporter
import com.example.data.database.service.CatalogValidationService
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CatalogImportTest {

    private lateinit var context: Context
    private lateinit var database: RideWorthDatabase

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, RideWorthDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testAssetCatalogImportAndValidation() = runBlocking {
        // Import assets into in-memory database
        AssetCatalogImporter.importCatalogIfNeeded(context, database, forceImport = true)

        val mfcCount = database.manufacturerDao().getCount()
        val modelCount = database.vehicleModelDao().getCount()
        val variantCount = database.vehicleVariantDao().getCount()

        assertTrue("Manufacturers count should be >= 40", mfcCount >= 40)
        assertTrue("Models count should be >= 50", modelCount >= 50)
        assertTrue("Variants count should be >= 10", variantCount >= 10)

        // Verify specific manufacturer (e.g. Suzuki 2Wheelers) and its models
        val suzuki2w = database.manufacturerDao().getManufacturerByName("Suzuki 2Wheelers")
        assertNotNull("Suzuki 2Wheelers manufacturer should exist", suzuki2w)

        // Validate complete catalog structure
        val report = CatalogValidationService.validateCatalog(database)
        assertTrue("Catalog validation report should be valid", report.isValid)
        assertEquals(0, report.emptyManufacturers.size)
        assertEquals(0, report.orphanModels)
        assertEquals(0, report.orphanVariants)
    }
}

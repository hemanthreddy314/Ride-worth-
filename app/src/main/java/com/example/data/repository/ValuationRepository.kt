package com.example.data.repository

import com.example.data.database.ValuationDao
import com.example.data.database.toEntity
import com.example.data.database.toRecord
import com.example.data.models.ValuationRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ValuationRepository {
    fun getHistoryRecords(): Flow<List<ValuationRecord>>
    suspend fun saveRecord(record: ValuationRecord)
    suspend fun deleteRecord(id: String)
}

class ValuationRepositoryImpl(
    private val valuationDao: ValuationDao
) : ValuationRepository {

    override fun getHistoryRecords(): Flow<List<ValuationRecord>> {
        return valuationDao.getAllRecords().map { entities ->
            entities.map { it.toRecord() }
        }
    }

    override suspend fun saveRecord(record: ValuationRecord) {
        valuationDao.insertRecord(record.toEntity())
    }

    override suspend fun deleteRecord(id: String) {
        valuationDao.deleteById(id)
    }
}

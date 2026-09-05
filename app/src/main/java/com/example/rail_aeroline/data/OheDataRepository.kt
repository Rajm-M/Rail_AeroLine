package com.example.rail_aeroline.data

import com.example.rail_aeroline.data.local.OheDao
import com.example.rail_aeroline.data.model.OheData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OheDataRepository @Inject constructor(
    private val oheDao: OheDao
) {
    fun getAllData(): Flow<List<OheData>> = oheDao.getAllData()

    suspend fun insertData(data: OheData) = oheDao.insert(data)

    suspend fun getUnsyncedData() = oheDao.getUnsyncedData()

    suspend fun markAsSynced(data: OheData) {
        oheDao.update(data.copy(isSynced = true))
    }
}

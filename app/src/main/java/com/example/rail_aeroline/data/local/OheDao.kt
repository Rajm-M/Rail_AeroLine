package com.example.rail_aeroline.data.local

import androidx.room.*
import com.example.rail_aeroline.data.model.OheData
import kotlinx.coroutines.flow.Flow

@Dao
interface OheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: OheData)

    @Query("SELECT * FROM ohe_data ORDER BY timestamp DESC")
    fun getAllData(): Flow<List<OheData>>

    @Query("SELECT * FROM ohe_data WHERE isSynced = 0")
    suspend fun getUnsyncedData(): List<OheData>

    @Update
    suspend fun update(data: OheData)
}

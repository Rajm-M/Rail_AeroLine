package com.example.rail_aeroline.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ohe_data")
data class OheData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val height: Double,
    val stagger: Double,
    val implantation: Double,
    val cant: Double,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

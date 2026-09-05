package com.example.rail_aeroline.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rail_aeroline.data.model.OheData

@Database(entities = [OheData::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun oheDao(): OheDao
}

package com.example.rail_aeroline.di

import android.content.Context
import androidx.room.Room
import com.example.rail_aeroline.data.local.AppDatabase
import com.example.rail_aeroline.data.local.OheDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "rail_aeroline_db"
        ).build()
    }

    @Provides
    fun provideOheDao(database: AppDatabase): OheDao {
        return database.oheDao()
    }
}

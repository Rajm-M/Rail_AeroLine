package com.example.rail_aeroline.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rail_aeroline.data.OheDataRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: OheDataRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val unsyncedData = repository.getUnsyncedData()
        if (unsyncedData.isEmpty()) return Result.success()

        return try {
            // Placeholder for actual network sync logic
            // unsyncedData.forEach { repository.markAsSynced(it) }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

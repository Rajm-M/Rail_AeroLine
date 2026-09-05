package com.example.rail_aeroline.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rail_aeroline.connectivity.BluetoothPairHelper
import com.example.rail_aeroline.connectivity.WifiLinkManager
import com.example.rail_aeroline.data.OheDataRepository
import com.example.rail_aeroline.data.model.OheData
import com.example.rail_aeroline.location.GpsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: OheDataRepository,
    private val bluetoothPairHelper: BluetoothPairHelper,
    private val wifiLinkManager: WifiLinkManager,
    private val gpsHelper: GpsHelper
) : ViewModel() {

    val syncStats: StateFlow<SyncStats> = repository.getAllData().map { data ->
        SyncStats(
            totalRecords = data.size,
            unsyncedCount = data.count { !it.isSynced }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SyncStats(0, 0))

    val bluetoothStatus: StateFlow<Boolean> = bluetoothPairHelper.pairingState.map {
        it is BluetoothPairHelper.PairingState.PairingSuccess
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val wifiStatus: StateFlow<Boolean> = flow {
        while (true) {
            emit(wifiLinkManager.isConnected())
            delay(5000)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val allData: StateFlow<List<OheData>> = repository.getAllData()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

data class SyncStats(
    val totalRecords: Int,
    val unsyncedCount: Int
)

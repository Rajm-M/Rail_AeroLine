package com.example.rail_aeroline.ui.admin

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import com.example.rail_aeroline.connectivity.BluetoothPairHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothPairHelper: BluetoothPairHelper
) : ViewModel() {

    val pairingState: StateFlow<BluetoothPairHelper.PairingState> = bluetoothPairHelper.pairingState
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = bluetoothPairHelper.discoveredDevices

    fun startDiscovery() {
        bluetoothPairHelper.startDiscovery()
    }

    fun pairDevice(device: BluetoothDevice) {
        bluetoothPairHelper.initiatePairing(device)
    }
}

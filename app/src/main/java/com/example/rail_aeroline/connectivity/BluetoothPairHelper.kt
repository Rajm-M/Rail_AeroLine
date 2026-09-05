package com.example.rail_aeroline.connectivity

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.rail_aeroline.security.SecurityManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothPairHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val securityManager: SecurityManager
) {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    // Seal pairing states to cleanly update your DeviceSetupFragment UI
    sealed class PairingState {
        object Idle : PairingState()
        object Discovering : PairingState()
        object BondingInProgress : PairingState()
        object PairingSuccess : PairingState()
        data class Error(val message: String) : PairingState()
    }

    private val _pairingState = MutableStateFlow<PairingState>(PairingState.Idle)
    val pairingState: StateFlow<PairingState> = _pairingState.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices.asStateFlow()

    // Receiver to monitor live hardware bonding status and discovery
    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if (device != null) {
                        val currentList = _discoveredDevices.value
                        if (!currentList.contains(device)) {
                            _discoveredDevices.value = currentList + device
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    if (_pairingState.value is PairingState.Discovering) {
                        _pairingState.value = PairingState.Idle
                    }
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)

                    if (device != null && isLlomgDevice(device)) {
                        when (bondState) {
                            BluetoothDevice.BOND_BONDING -> {
                                _pairingState.value = PairingState.BondingInProgress
                            }
                            BluetoothDevice.BOND_BONDED -> {
                                storeDeviceIdentifier(device)
                                _pairingState.value = PairingState.PairingSuccess
                                unregisterReceiverSafe()
                            }
                            BluetoothDevice.BOND_NONE -> {
                                _pairingState.value = PairingState.Error("Pairing rejected or failed.")
                                unregisterReceiverSafe()
                            }
                        }
                    }
                }
            }
        }
    }

    private var isReceiverRegistered = false

    fun startDiscovery() {
        if (!hasBluetoothPermissions()) {
            _pairingState.value = PairingState.Error("Missing required Bluetooth runtime permissions.")
            return
        }

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            _pairingState.value = PairingState.Error("Bluetooth is turned off or unsupported.")
            return
        }

        try {
            _discoveredDevices.value = emptyList()
            _pairingState.value = PairingState.Discovering
            registerReceiverSafe()
            bluetoothAdapter.startDiscovery()
        } catch (e: SecurityException) {
            _pairingState.value = PairingState.Error("Security Exception: Permission missing at runtime.")
        }
    }

    /**
     * Triggers active hardware pairing with the target LLOMG device
     */
    fun initiatePairing(device: BluetoothDevice) {
        if (!hasBluetoothPermissions()) {
            _pairingState.value = PairingState.Error("Missing Bluetooth Connect permission.")
            return
        }

        if (!isLlomgDevice(device)) {
            _pairingState.value = PairingState.Error("Invalid device. Not an official LLOMG unit.")
            return
        }

        try {
            // Stop scanning to preserve signal and processing budget for pairing
            if (bluetoothAdapter?.isDiscovering == true) {
                bluetoothAdapter.cancelDiscovery()
            }

            registerReceiverSafe()

            // Trigger OS pairing dialog / background handshake
            val success = device.createBond()
            if (!success) {
                _pairingState.value = PairingState.Error("Could not initiate pairing handshake.")
            }
        } catch (e: SecurityException) {
            _pairingState.value = PairingState.Error("Security Exception during bonding initialization.")
        }
    }

    fun storeDeviceIdentifier(device: BluetoothDevice) {
        securityManager.saveSecureString("paired_device_id", device.address)
    }

    fun getPairedDeviceIdentifier(): String? {
        return securityManager.getSecureString("paired_device_id")
    }

    /**
     * Filters for specific hardware signatures to ignore unrelated surrounding devices
     */
    @SuppressLint("MissingPermission") // Guarded by hasBluetoothPermissions runtime checks
    fun isLlomgDevice(device: BluetoothDevice): Boolean {
        val name = device.name ?: return false
        return name.startsWith("LLOMG-", ignoreCase = true)
    }

    private fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun registerReceiverSafe() {
        if (!isReceiverRegistered) {
            val filter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_FOUND)
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            }
            context.registerReceiver(bluetoothReceiver, filter)
            isReceiverRegistered = true
        }
    }

    private fun unregisterReceiverSafe() {
        if (isReceiverRegistered) {
            try {
                context.unregisterReceiver(bluetoothReceiver)
            } catch (e: IllegalArgumentException) {
                // Already unregistered safely
            }
            isReceiverRegistered = false
        }
    }
}

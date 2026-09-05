package com.example.rail_aeroline.ui.admin

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rail_aeroline.connectivity.BluetoothPairHelper

@SuppressLint("MissingPermission")
@Composable
fun DeviceSetupScreen(viewModel: BluetoothViewModel = hiltViewModel()) {
    val pairingState by viewModel.pairingState.collectAsState()
    val discoveredDevices by viewModel.discoveredDevices.collectAsState()
    val context = LocalContext.current

    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            viewModel.startDiscovery()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "One-Time Secure Pairing",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        when (pairingState) {
            is BluetoothPairHelper.PairingState.Idle -> {
                Button(onClick = { launcher.launch(permissionsToRequest) }) {
                    Text("Scan for LLOMG Unit")
                }
            }
            is BluetoothPairHelper.PairingState.Discovering -> {
                CircularProgressIndicator()
                Text("Scanning for LLOMG units...")
            }
            is BluetoothPairHelper.PairingState.BondingInProgress -> {
                CircularProgressIndicator()
                Text("Pairing in progress...")
            }
            is BluetoothPairHelper.PairingState.PairingSuccess -> {
                Text("✅ Successfully Paired!", color = MaterialTheme.colorScheme.primary)
            }
            is BluetoothPairHelper.PairingState.Error -> {
                Text("❌ Error: ${(pairingState as BluetoothPairHelper.PairingState.Error).message}", color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.startDiscovery() }) {
                    Text("Retry Scan")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(discoveredDevices) { device ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.pairDevice(device) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = device.name ?: "Unknown Device", style = MaterialTheme.typography.titleMedium)
                        Text(text = device.address, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

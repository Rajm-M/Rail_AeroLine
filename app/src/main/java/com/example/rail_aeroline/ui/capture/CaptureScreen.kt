package com.example.rail_aeroline.ui.capture

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CaptureScreen(viewModel: CaptureViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Real-Time OHE Capture", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        
        InfoRow("Height", "${state.currentHeight} m")
        InfoRow("Stagger", "${state.currentStagger} mm")
        InfoRow("Implantation", "${state.currentImplantation} m")
        InfoRow("Cant", "${state.currentCant} mm")
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (state.isLogging) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.logDataPoint() },
                modifier = Modifier.height(56.dp).fillMaxWidth(0.6f)
            ) {
                Text("Log Data Point")
            }
        }

        state.lastLoggedId?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text("✅ Point Logged Successfully", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
    }
}

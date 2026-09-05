package com.example.rail_aeroline.ui.report

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ReportScreen(viewModel: ReportViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Generate TDMS Reports", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        
        when (uiState) {
            is ReportUiState.Loading -> CircularProgressIndicator()
            is ReportUiState.Success -> {
                Text("Report Generated!", color = MaterialTheme.colorScheme.primary)
                Text((uiState as ReportUiState.Success).file.absolutePath, style = MaterialTheme.typography.bodySmall)
                Button(onClick = { viewModel.resetState() }) { Text("OK") }
            }
            is ReportUiState.Error -> {
                Text("Error: ${(uiState as ReportUiState.Error).message}", color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.resetState() }) { Text("Retry") }
            }
            else -> {
                Button(
                    onClick = { viewModel.exportPdf() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export PDF Report")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { viewModel.exportExcel() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export Excel (CSV) Report")
                }
            }
        }
    }
}

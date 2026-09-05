package com.example.rail_aeroline.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rail_aeroline.data.model.OheData
import com.example.rail_aeroline.ui.home.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LogHistoryScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val dataList by viewModel.allData.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Logged Data History",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        if (dataList.isEmpty()) {
            Text("No data logged yet.", style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(dataList) { data ->
                    LogItem(data)
                }
            }
        }
    }
}

@Composable
fun LogItem(data: OheData) {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    val dateString = sdf.format(Date(data.timestamp))

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = dateString, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                if (data.isSynced) {
                    Text("Synced", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("H: ${data.height}m | S: ${data.stagger}mm", style = MaterialTheme.typography.bodyMedium)
                Text("I: ${data.implantation}m | C: ${data.cant}mm", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "GPS: ${String.format("%.5f", data.latitude)}, ${String.format("%.5f", data.longitude)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

package com.example.rail_aeroline.ui.capture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rail_aeroline.data.OheDataRepository
import com.example.rail_aeroline.data.model.OheData
import com.example.rail_aeroline.location.GpsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaptureViewModel @Inject constructor(
    private val repository: OheDataRepository,
    private val gpsHelper: GpsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(CaptureUiState())
    val uiState: StateFlow<CaptureUiState> = _uiState.asStateFlow()

    fun logDataPoint() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLogging = true)
            
            val location = gpsHelper.getCurrentLocation()
            
            val newData = OheData(
                height = _uiState.value.currentHeight,
                stagger = _uiState.value.currentStagger,
                implantation = _uiState.value.currentImplantation,
                cant = _uiState.value.currentCant,
                latitude = location?.latitude ?: 0.0,
                longitude = location?.longitude ?: 0.0
            )
            
            repository.insertData(newData)
            _uiState.value = _uiState.value.copy(isLogging = false, lastLoggedId = newData.id)
        }
    }
}

data class CaptureUiState(
    val currentHeight: Double = 5.50,
    val currentStagger: Double = 200.0,
    val currentImplantation: Double = 3.00,
    val currentCant: Double = 50.0,
    val isLogging: Boolean = false,
    val lastLoggedId: Long? = null
)

package com.example.rail_aeroline.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rail_aeroline.data.OheDataRepository
import com.example.rail_aeroline.report.ReportGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: OheDataRepository,
    private val reportGenerator: ReportGenerator
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Idle)
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    fun exportPdf() {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading
            val data = repository.getAllData().first()
            val file = reportGenerator.generatePdfReport(data)
            _uiState.value = if (file != null) ReportUiState.Success(file) else ReportUiState.Error("Failed to generate PDF")
        }
    }

    fun exportExcel() {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading
            val data = repository.getAllData().first()
            val file = reportGenerator.generateExcelReport(data)
            _uiState.value = if (file != null) ReportUiState.Success(file) else ReportUiState.Error("Failed to generate Excel/CSV")
        }
    }

    fun resetState() {
        _uiState.value = ReportUiState.Idle
    }
}

sealed class ReportUiState {
    object Idle : ReportUiState()
    object Loading : ReportUiState()
    data class Success(val file: File) : ReportUiState()
    data class Error(val message: String) : ReportUiState()
}

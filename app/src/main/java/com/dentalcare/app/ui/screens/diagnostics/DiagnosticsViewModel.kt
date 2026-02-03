package com.dentalcare.app.ui.screens.diagnostics

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dentalcare.app.data.model.Diagnostic
import com.dentalcare.app.data.repository.DiagnosticRepository
import com.dentalcare.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiagnosticsState(
    val isLoading: Boolean = false,
    val diagnostics: List<Diagnostic> = emptyList(),
    val error: String = ""
)

data class UploadState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = "",
    val progress: Int = 0
)

@HiltViewModel
class DiagnosticsViewModel @Inject constructor(
    private val diagnosticRepository: DiagnosticRepository
) : ViewModel() {
    
    private val _diagnosticsState = MutableStateFlow(DiagnosticsState())
    val diagnosticsState: StateFlow<DiagnosticsState> = _diagnosticsState.asStateFlow()
    
    private val _uploadState = MutableStateFlow(UploadState())
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()
    
    private val _currentDiagnostic = MutableStateFlow<Diagnostic?>(null)
    val currentDiagnostic: StateFlow<Diagnostic?> = _currentDiagnostic.asStateFlow()
    
    init {
        loadDiagnostics()
    }
    
    private fun loadDiagnostics() {
        viewModelScope.launch {
            diagnosticRepository.getDiagnostics().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _diagnosticsState.value = DiagnosticsState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _diagnosticsState.value = DiagnosticsState(diagnostics = result.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        _diagnosticsState.value = DiagnosticsState(error = result.message ?: "An error occurred")
                    }
                }
            }
        }
    }
    
    fun loadDiagnosticsByPatient(patientId: String) {
        viewModelScope.launch {
            diagnosticRepository.getDiagnosticsByPatient(patientId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _diagnosticsState.value = DiagnosticsState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _diagnosticsState.value = DiagnosticsState(diagnostics = result.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        _diagnosticsState.value = DiagnosticsState(error = result.message ?: "An error occurred")
                    }
                }
            }
        }
    }
    
    fun loadDiagnostic(diagnosticId: String) {
        viewModelScope.launch {
            diagnosticRepository.getDiagnostic(diagnosticId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _currentDiagnostic.value = result.data
                    }
                    is Resource.Error -> {
                        _uploadState.value = UploadState(error = result.message ?: "Failed to load diagnostic")
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }
    
    suspend fun uploadDiagnostic(
        diagnostic: Diagnostic,
        imageUri: Uri?
    ) {
        _uploadState.value = UploadState(isLoading = true, progress = 0)
        
        val imageUrls = mutableListOf<String>()
        
        if (imageUri != null) {
            _uploadState.value = UploadState(isLoading = true, progress = 30)
            
            val uploadResult = diagnosticRepository.uploadImage(imageUri, diagnostic.id)
            if (uploadResult.isSuccess) {
                imageUrls.add(uploadResult.getOrNull() ?: "")
                _uploadState.value = UploadState(isLoading = true, progress = 60)
            } else {
                _uploadState.value = UploadState(
                    error = "Failed to upload image: ${uploadResult.exceptionOrNull()?.message}"
                )
                return
            }
        }
        
        _uploadState.value = UploadState(isLoading = true, progress = 80)
        
        viewModelScope.launch {
            val updatedDiagnostic = diagnostic.copy(images = imageUrls)
            diagnosticRepository.addDiagnostic(updatedDiagnostic).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uploadState.value = UploadState(isLoading = true, progress = 90)
                    }
                    is Resource.Success -> {
                        _uploadState.value = UploadState(isSuccess = true, progress = 100)
                    }
                    is Resource.Error -> {
                        _uploadState.value = UploadState(
                            error = result.message ?: "Failed to save diagnostic"
                        )
                    }
                }
            }
        }
    }
    
    fun deleteDiagnostic(diagnosticId: String) {
        viewModelScope.launch {
            diagnosticRepository.deleteDiagnostic(diagnosticId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uploadState.value = UploadState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _uploadState.value = UploadState(isSuccess = true)
                    }
                    is Resource.Error -> {
                        _uploadState.value = UploadState(
                            error = result.message ?: "Failed to delete diagnostic"
                        )
                    }
                }
            }
        }
    }
}

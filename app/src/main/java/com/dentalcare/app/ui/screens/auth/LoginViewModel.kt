package com.dentalcare.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dentalcare.app.data.model.User
import com.dentalcare.app.data.repository.AuthRepository
import com.dentalcare.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = ""
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<Resource<User?>>(Resource.Loading())
    val currentUser: StateFlow<Resource<User?>> = _currentUser.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.signIn(email, password).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _loginState.value = LoginState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _loginState.value = LoginState(isSuccess = true)
                    }
                    is Resource.Error -> {
                        _loginState.value = LoginState(error = result.message ?: "Login failed")
                    }
                }
            }
        }
    }
    
    fun checkCurrentUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { result ->
                _currentUser.value = result
            }
        }
    }
}

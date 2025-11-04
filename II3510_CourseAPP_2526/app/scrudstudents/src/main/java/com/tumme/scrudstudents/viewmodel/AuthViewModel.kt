package com.tumme.scrudstudents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumme.scrudstudents.data.local.model.Role
import com.tumme.scrudstudents.data.local.model.UserEntity
import com.tumme.scrudstudents.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * Authentication state sealed class.
 */
sealed class AuthState {
    object Loading : AuthState()
    data class LoggedIn(val user: UserEntity, val role: Role) : AuthState()
    object LoggedOut : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * ViewModel for authentication operations.
 * 
 * Manages login, registration, and session state.
 * 
 * Data Flow: UI → AuthViewModel → AuthRepository → DAO → DB
 * 
 * StateFlow is collected in UI via collectAsState() for reactive updates.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Check if user is already logged in - use suspend function for initial check
        viewModelScope.launch {
            // Check session expiration first
            if (authRepository.isSessionExpired()) {
                authRepository.logout()
                _authState.value = AuthState.LoggedOut
            } else {
                // Get initial user state synchronously (not observe forever)
                val user = authRepository.getCurrentUserSync()
                if (user != null) {
                    // Check expiration on initial load
                    if (authRepository.isSessionExpired()) {
                        authRepository.logout()
                        _authState.value = AuthState.LoggedOut
                    } else {
                        val role = Role.from(user.role)
                        _authState.value = AuthState.LoggedIn(user, role)
                        authRepository.updateLastActivity() // Update on successful login
                    }
                } else {
                    _authState.value = AuthState.LoggedOut
                }
            }
        }
        
        // Observe currentUserIdFlow for changes (e.g., logout from another part of app)
        viewModelScope.launch {
            authRepository.currentUserId.collect { userId ->
                if (userId == null) {
                    // User logged out
                    if (_authState.value !is AuthState.LoggedOut) {
                        _authState.value = AuthState.LoggedOut
                    }
                } else {
                    // User logged in - get user data synchronously
                    val user = authRepository.getCurrentUserSync()
                    if (user != null && !authRepository.isSessionExpired()) {
                        val role = Role.from(user.role)
                        if (_authState.value !is AuthState.LoggedIn || 
                            ((_authState.value as? AuthState.LoggedIn)?.user?.id != user.id)) {
                            _authState.value = AuthState.LoggedIn(user, role)
                        }
                    } else {
                        _authState.value = AuthState.LoggedOut
                    }
                }
            }
        }
        
        // Periodic session expiration check (every minute)
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(60000) // Check every minute
                if (_authState.value is AuthState.LoggedIn && authRepository.isSessionExpired()) {
                    authRepository.logout()
                    _authState.value = AuthState.LoggedOut
                }
            }
        }
    }

    /**
     * Logs in a user with email and password.
     * 
     * @param email User email
     * @param password Plain text password
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = authRepository.login(email, password)
                if (user != null) {
                    val role = Role.from(user.role)
                    authRepository.updateLastActivity() // Update activity on login
                    _authState.value = AuthState.LoggedIn(user, role)
                } else {
                    _authState.value = AuthState.Error("Invalid email or password")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Login failed: ${e.message}")
            }
        }
    }

    /**
     * Registers a new user.
     * 
     * @param email User email
     * @param password Plain text password
     * @param role User role (STUDENT or TEACHER)
     * @param firstName First name
     * @param lastName Last name
     * @param studentLevel Student level (only for STUDENT role)
     */
    fun register(
        email: String,
        password: String,
        role: Role,
        firstName: String,
        lastName: String,
        studentLevel: com.tumme.scrudstudents.data.local.model.LevelCourse? = null,
        dateOfBirth: Date? = null,
        gender: com.tumme.scrudstudents.data.local.model.Gender? = null
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = authRepository.register(
                    email = email,
                    password = password,
                    role = role,
                    firstName = firstName,
                    lastName = lastName,
                    studentLevel = studentLevel,
                    dateOfBirth = dateOfBirth,
                    gender = gender
                )
                if (user != null) {
                    val userRole = Role.from(user.role)
                    authRepository.updateLastActivity() // Update activity on register
                    _authState.value = AuthState.LoggedIn(user, userRole)
                } else {
                    _authState.value = AuthState.Error("Email already exists")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Registration failed: ${e.message}")
            }
        }
    }

    /**
     * Logs out the current user.
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState.LoggedOut
        }
    }
    
    /**
     * Updates last activity timestamp to keep session alive.
     * Call this on user interactions.
     */
    fun updateLastActivity() {
        viewModelScope.launch {
            authRepository.updateLastActivity()
        }
    }
}

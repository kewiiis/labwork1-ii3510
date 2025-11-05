package com.tumme.scrudstudents.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tumme.scrudstudents.data.local.model.Role
import com.tumme.scrudstudents.viewmodel.AuthViewModel
import com.tumme.scrudstudents.viewmodel.AuthState

/**
 * Splash screen that checks authentication state and redirects accordingly.
 * 
 * If user is logged in, redirects to StudentHome or TeacherHome based on role.
 * Otherwise, redirects to LoginScreen.
 */
@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    var hasNavigated by remember { mutableStateOf(false) }
    
    // Timeout fallback: if still loading after 2 seconds, navigate to login
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        if (!hasNavigated) {
            val currentState = authState
            if (currentState is AuthState.Loading) {
                hasNavigated = true
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }
    
    LaunchedEffect(authState) {
        if (hasNavigated) return@LaunchedEffect
        
        when (val currentState = authState) {
            is AuthState.LoggedIn -> {
                val route = when (currentState.role) {
                    Role.STUDENT -> "student_home"
                    Role.TEACHER -> "teacher_home"
                }
                hasNavigated = true
                navController.navigate(route) {
                    popUpTo("splash") { inclusive = true }
                    launchSingleTop = true
                }
            }
            is AuthState.LoggedOut -> {
                hasNavigated = true
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                    launchSingleTop = true
                }
            }
            else -> { /* Loading or Error - wait for state change or timeout */ }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

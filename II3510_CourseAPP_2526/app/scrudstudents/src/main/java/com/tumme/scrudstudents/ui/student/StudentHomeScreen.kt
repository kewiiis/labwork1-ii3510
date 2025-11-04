package com.tumme.scrudstudents.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tumme.scrudstudents.viewmodel.AuthViewModel
import com.tumme.scrudstudents.viewmodel.StudentViewModel

/**
 * Student home screen (dashboard).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(
    navController: NavController,
    studentViewModel: StudentViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        studentViewModel.loadCurrentStudent()
    }

    val currentStudent by studentViewModel.currentStudent.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    // Update activity on screen visibility
    LaunchedEffect(Unit) {
        authViewModel.updateLastActivity()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Home") },
                actions = {
                    TextButton(
                        onClick = {
                            authViewModel.logout()
                        }
                    ) {
                        Text("Logout")
                    }
                }
            )
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "student_home",
                    onClick = { },
                    icon = { Text("🏠") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = currentRoute == "student_courses",
                    onClick = { 
                        navController.navigate("student_courses") {
                            popUpTo("student_home") { inclusive = false }
                        }
                    },
                    icon = { Text("📚") },
                    label = { Text("Courses") }
                )
                NavigationBarItem(
                    selected = currentRoute == "student_subscribes",
                    onClick = { 
                        navController.navigate("student_subscribes") {
                            popUpTo("student_home") { inclusive = false }
                        }
                    },
                    icon = { Text("📝") },
                    label = { Text("Subscriptions") }
                )
                NavigationBarItem(
                    selected = currentRoute == "student_grades",
                    onClick = { 
                        navController.navigate("student_grades") {
                            popUpTo("student_home") { inclusive = false }
                        }
                    },
                    icon = { Text("📊") },
                    label = { Text("Grades") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Welcome, ${currentStudent?.firstName ?: "Student"}!",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Your level: ${currentStudent?.level?.value ?: "N/A"}")
        }
    }
}

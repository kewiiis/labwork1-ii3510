package com.tumme.scrudstudents.ui.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tumme.scrudstudents.viewmodel.AuthViewModel
import com.tumme.scrudstudents.viewmodel.TeacherViewModel

/**
 * Teacher home screen (dashboard).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherHomeScreen(
    navController: NavController,
    teacherViewModel: TeacherViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        teacherViewModel.loadCurrentTeacher()
    }

    val currentTeacher by teacherViewModel.currentTeacher.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    // Update activity on screen visibility
    LaunchedEffect(Unit) {
        authViewModel.updateLastActivity()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teacher Home") },
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
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Text("🏠") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("teacher_courses") },
                    icon = { Text("📚") },
                    label = { Text("Courses") }
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
                text = "Welcome, ${currentTeacher?.firstName ?: "Teacher"}!",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

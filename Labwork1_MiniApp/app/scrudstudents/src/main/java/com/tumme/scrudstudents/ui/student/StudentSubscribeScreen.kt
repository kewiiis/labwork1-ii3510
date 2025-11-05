package com.tumme.scrudstudents.ui.student

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tumme.scrudstudents.viewmodel.StudentViewModel
import com.tumme.scrudstudents.viewmodel.SubscribeViewModel

/**
 * Student subscriptions screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentSubscribeScreen(
    navController: NavController,
    studentViewModel: StudentViewModel = hiltViewModel(),
    subscribeViewModel: SubscribeViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        studentViewModel.loadCurrentStudent()
    }

    val currentStudent by studentViewModel.currentStudent.collectAsState()
    val studentId = currentStudent?.idStudent ?: 0
    val subscribes by subscribeViewModel.getSubscribesByStudent(studentId).collectAsState(initial = emptyList())
    val courses by subscribeViewModel.courses.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Subscriptions") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("subscribe_form") }) {
                Text("+")
            }
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "student_home",
                    onClick = { 
                        navController.navigate("student_home") {
                            popUpTo("student_home") { inclusive = true }
                        }
                    },
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
                    onClick = { },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(subscribes) { subscribe ->
                val course = courses.find { it.idCourse == subscribe.courseId }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            course?.nameCourse ?: "Unknown Course",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("Score: ${subscribe.score}/20", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

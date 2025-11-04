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
 * Student grades screen with weighted grade calculation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentGradesScreen(
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

    var weightedGrade by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(subscribes, courses) {
        if (subscribes.isNotEmpty() && courses.isNotEmpty()) {
            weightedGrade = subscribeViewModel.calculateWeightedGrade(subscribes)
        } else {
            weightedGrade = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Grades") }
            )
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
                    onClick = { },
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
            if (weightedGrade != null) {
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Weighted Average",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "${String.format("%.2f", weightedGrade)}/20",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }

            LazyColumn {
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
                            Text("ECTS: ${course?.ectsCourse ?: 0f}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

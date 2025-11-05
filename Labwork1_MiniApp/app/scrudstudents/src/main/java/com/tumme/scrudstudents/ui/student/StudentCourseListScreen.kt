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
import com.tumme.scrudstudents.data.local.model.CourseEntity
import com.tumme.scrudstudents.viewmodel.CourseViewModel
import com.tumme.scrudstudents.viewmodel.StudentViewModel

/**
 * Student course list screen filtered by student's level.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentCourseListScreen(
    navController: NavController,
    studentViewModel: StudentViewModel = hiltViewModel(),
    courseViewModel: CourseViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        studentViewModel.loadCurrentStudent()
    }

    val currentStudent by studentViewModel.currentStudent.collectAsState()
    val level = currentStudent?.level?.value ?: "P1"
    val courses by courseViewModel.getCoursesByLevel(level).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Courses (Level: $level)") }
            )
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "student_home",
                    onClick = { 
                        if (currentRoute != "student_home") {
                            navController.navigate("student_home") {
                                popUpTo("student_home") { inclusive = true }
                            }
                        }
                    },
                    icon = { Text("🏠") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = currentRoute == "student_courses",
                    onClick = { 
                        if (currentRoute != "student_courses") {
                            navController.navigate("student_courses") {
                                popUpTo("student_home") { inclusive = false }
                            }
                        }
                    },
                    icon = { Text("📚") },
                    label = { Text("Courses") }
                )
                NavigationBarItem(
                    selected = currentRoute == "student_subscribes",
                    onClick = { 
                        if (currentRoute != "student_subscribes") {
                            navController.navigate("student_subscribes") {
                                popUpTo("student_home") { inclusive = false }
                            }
                        }
                    },
                    icon = { Text("📝") },
                    label = { Text("Subscriptions") }
                )
                NavigationBarItem(
                    selected = currentRoute == "student_grades",
                    onClick = { 
                        if (currentRoute != "student_grades") {
                            navController.navigate("student_grades") {
                                popUpTo("student_home") { inclusive = false }
                            }
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
            items(courses) { course ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(course.nameCourse, style = MaterialTheme.typography.titleMedium)
                        Text("ECTS: ${course.ectsCourse}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

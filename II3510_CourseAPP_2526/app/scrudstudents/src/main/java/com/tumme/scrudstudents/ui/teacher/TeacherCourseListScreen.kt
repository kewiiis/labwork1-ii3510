package com.tumme.scrudstudents.ui.teacher

import androidx.compose.foundation.clickable
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
import com.tumme.scrudstudents.viewmodel.CourseViewModel
import com.tumme.scrudstudents.viewmodel.TeacherViewModel

/**
 * Teacher course list screen showing courses declared by the teacher.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherCourseListScreen(
    navController: NavController,
    teacherViewModel: TeacherViewModel = hiltViewModel(),
    courseViewModel: CourseViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        teacherViewModel.loadCurrentTeacher()
    }

    val currentTeacher by teacherViewModel.currentTeacher.collectAsState()
    val teacherId = currentTeacher?.idTeacher ?: 0
    val courses by courseViewModel.getCoursesByTeacher(teacherId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Courses") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←")
                    }
                }
            )
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "teacher_home",
                    onClick = { navController.navigate("teacher_home") },
                    icon = { Text("🏠") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = currentRoute == "teacher_courses",
                    onClick = { },
                    icon = { Text("📚") },
                    label = { Text("Courses") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("course_form") }) {
                Text("+")
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
                        .clickable { navController.navigate("teacher_students/${course.idCourse}") }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(course.nameCourse, style = MaterialTheme.typography.titleMedium)
                        Text("ECTS: ${course.ectsCourse}", style = MaterialTheme.typography.bodySmall)
                        Text("Level: ${course.levelCourse.value}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

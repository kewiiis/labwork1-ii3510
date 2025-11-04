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
import com.tumme.scrudstudents.data.local.model.StudentEntity
import com.tumme.scrudstudents.data.local.model.SubscribeEntity
import com.tumme.scrudstudents.viewmodel.StudentViewModel
import com.tumme.scrudstudents.viewmodel.SubscribeViewModel

/**
 * Teacher student list screen for a specific course.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherStudentListScreen(
    courseId: Int,
    navController: NavController,
    subscribeViewModel: SubscribeViewModel = hiltViewModel(),
    studentViewModel: StudentViewModel = hiltViewModel()
) {
    val subscribes by subscribeViewModel.getSubscribesByCourse(courseId).collectAsState(initial = emptyList())
    val students by studentViewModel.allStudents.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enrolled Students") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(subscribes) { subscribe ->
                val student = students.find { it.idStudent == subscribe.studentId }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            navController.navigate("teacher_grade_entry/${courseId}/${subscribe.studentId}")
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "${student?.firstName ?: ""} ${student?.lastName ?: ""}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("Score: ${subscribe.score}/20", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

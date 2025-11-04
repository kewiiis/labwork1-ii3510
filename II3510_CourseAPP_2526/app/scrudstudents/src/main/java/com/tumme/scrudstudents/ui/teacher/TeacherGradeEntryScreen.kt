package com.tumme.scrudstudents.ui.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tumme.scrudstudents.data.local.model.SubscribeEntity
import com.tumme.scrudstudents.viewmodel.SubscribeViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

/**
 * Teacher grade entry screen for a specific student and course.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherGradeEntryScreen(
    courseId: Int,
    studentId: Int,
    navController: NavController,
    subscribeViewModel: SubscribeViewModel = hiltViewModel()
) {
    var score by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(courseId, studentId) {
        val subscribes = subscribeViewModel.getSubscribesByStudent(studentId).first()
        subscribes.find { it.courseId == courseId }?.let {
            score = it.score.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enter Grade") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = score,
                onValueChange = {
                    score = it
                    error = ""
                },
                label = { Text("Score (0-20)") },
                modifier = Modifier.fillMaxWidth(),
                isError = error.isNotEmpty(),
                supportingText = if (error.isNotEmpty()) { { Text(error) } } else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val scoreValue = score.toFloatOrNull()
                    if (scoreValue == null || scoreValue < 0 || scoreValue > 20) {
                        error = "Score must be between 0 and 20"
                    } else {
                        coroutineScope.launch {
                            val subscribes = subscribeViewModel.getSubscribesByStudent(studentId).first()
                            val existing = subscribes.find { it.courseId == courseId }
                            
                            if (existing != null) {
                                // Update existing
                                subscribeViewModel.updateSubscribe(
                                    existing.copy(score = scoreValue)
                                )
                            } else {
                                // Create new
                                subscribeViewModel.insertSubscribe(
                                    SubscribeEntity(
                                        studentId = studentId,
                                        courseId = courseId,
                                        score = scoreValue
                                    )
                                )
                            }
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}

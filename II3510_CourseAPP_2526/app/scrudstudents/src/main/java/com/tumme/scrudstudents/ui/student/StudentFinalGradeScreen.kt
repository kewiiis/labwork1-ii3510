package com.tumme.scrudstudents.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tumme.scrudstudents.viewmodel.StudentViewModel
import com.tumme.scrudstudents.viewmodel.SubscribeViewModel

/**
 * Student final grade summary screen by level.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentFinalGradeScreen(
    navController: NavController,
    studentViewModel: StudentViewModel = hiltViewModel(),
    subscribeViewModel: SubscribeViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        studentViewModel.loadCurrentStudent()
    }

    val currentStudent by studentViewModel.currentStudent.collectAsState()
    val studentId = currentStudent?.idStudent ?: 0
    val level = currentStudent?.level?.value ?: "P1"
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
                title = { Text("Final Grade - Level $level") },
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
                .padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (weightedGrade != null) {
                Text(
                    "Final Grade",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "${String.format("%.2f", weightedGrade)}/20",
                    style = MaterialTheme.typography.displayMedium
                )
            } else {
                Text("No grades available")
            }
        }
    }
}

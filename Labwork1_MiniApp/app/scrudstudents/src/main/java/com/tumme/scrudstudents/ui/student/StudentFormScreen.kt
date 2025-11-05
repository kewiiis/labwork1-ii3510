package com.tumme.scrudstudents.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.hilt.navigation.compose.hiltViewModel
import com.tumme.scrudstudents.data.local.model.Gender
import com.tumme.scrudstudents.data.local.model.LevelCourse
import com.tumme.scrudstudents.data.local.model.StudentEntity

/**
 * Écran de formulaire pour créer un nouvel étudiant.
 * Gère l'état local du formulaire et la validation.
 */
@Composable
fun StudentFormScreen(
    viewModel: StudentListViewModel = hiltViewModel(),
    onSaved: ()->Unit = {}
) {
    var id by remember { mutableStateOf((0..10000).random()) }
    var lastName by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var dobText by remember { mutableStateOf("2000-01-01") }
    var gender by remember { mutableStateOf(Gender.NotConcerned) }
    var level by remember { mutableStateOf(LevelCourse.P1) }
    var userId by remember { mutableStateOf(0) } // Default, should be set from auth context

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name") })
        Spacer(Modifier.height(8.dp))
        TextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name") })
        Spacer(Modifier.height(8.dp))
        TextField(value = dobText, onValueChange = { dobText = it }, label = { Text("Date of birth (yyyy-MM-dd)") })
        Spacer(Modifier.height(8.dp))

        Row {
            listOf(Gender.Male, Gender.Female, Gender.NotConcerned).forEach { g->
                Button(onClick = { gender = g }, modifier = Modifier.padding(end = 8.dp)) {
                    Text(g.value)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        
        Button(onClick = {
            val dob = dateFormat.parse(dobText) ?: Date()
            val student = StudentEntity(
                idStudent = id,
                userId = userId,
                lastName = lastName,
                firstName = firstName,
                dateOfBirth = dob,
                gender = gender,
                level = level
            )
            viewModel.insertStudent(student)
            onSaved()
        }) {
            Text("Save")
        }
    }
}

package com.tumme.scrudstudents.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tumme.scrudstudents.data.local.model.Gender
import com.tumme.scrudstudents.data.local.model.LevelCourse
import com.tumme.scrudstudents.data.local.model.Role
import com.tumme.scrudstudents.viewmodel.AuthState
import com.tumme.scrudstudents.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var role by remember { mutableStateOf<Role?>(null) }
    var studentLevel by remember { mutableStateOf<LevelCourse?>(null) }
    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf<Gender?>(null) }

    var roleExpanded by remember { mutableStateOf(false) }
    var levelExpanded by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }
    
    // Error state for date parsing
    var dateError by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.LoggedIn) {
            val route = when ((authState as AuthState.LoggedIn).role) {
                Role.STUDENT -> "student_home"
                Role.TEACHER -> "teacher_home"
            }
            navController.navigate(route) {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = MaterialTheme.colorScheme.onSurface) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = MaterialTheme.colorScheme.onSurface) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name", color = MaterialTheme.colorScheme.onSurface) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name", color = MaterialTheme.colorScheme.onSurface) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Role selection
        ExposedDropdownMenuBox(
            expanded = roleExpanded,
            onExpandedChange = { roleExpanded = !roleExpanded }
        ) {
            OutlinedTextField(
                value = role?.value ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Role", color = MaterialTheme.colorScheme.onSurface) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            ExposedDropdownMenu(
                expanded = roleExpanded,
                onDismissRequest = { roleExpanded = false }
            ) {
                Role.entries.forEach { r ->
                    DropdownMenuItem(
                        text = { Text(r.value) },
                        onClick = {
                            role = r
                            roleExpanded = false
                        }
                    )
                }
            }
        }

        // Student-specific fields
        if (role == Role.STUDENT) {
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = levelExpanded,
                onExpandedChange = { levelExpanded = !levelExpanded }
            ) {
                OutlinedTextField(
                    value = studentLevel?.value ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Study Level", color = MaterialTheme.colorScheme.onSurface) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = levelExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                ExposedDropdownMenu(
                    expanded = levelExpanded,
                    onDismissRequest = { levelExpanded = false }
                ) {
                    LevelCourse.entries.forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level.value) },
                            onClick = {
                                studentLevel = level
                                levelExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { 
                    dateOfBirth = it
                    dateError = "" // Clear error when user types
                },
                label = { Text("Date of Birth (yyyy-MM-dd)", color = MaterialTheme.colorScheme.onSurface) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = dateError.isNotEmpty(),
                supportingText = if (dateError.isNotEmpty()) { { Text(dateError) } } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = genderExpanded,
                onExpandedChange = { genderExpanded = !genderExpanded }
            ) {
                OutlinedTextField(
                    value = gender?.value ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gender", color = MaterialTheme.colorScheme.onSurface) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                ExposedDropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false }
                ) {
                    Gender.entries.forEach { g ->
                        DropdownMenuItem(
                            text = { Text(g.value) },
                            onClick = {
                                gender = g
                                genderExpanded = false
                            }
                        )
                    }
                }
            }
        }

        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (role == Role.STUDENT) {
                    // Validate and parse date with error handling
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dateFormat.isLenient = false // Strict parsing
                    try {
                        val dob = dateFormat.parse(dateOfBirth)
                        if (dob == null) {
                            dateError = "Invalid date format. Use yyyy-MM-dd"
                            return@Button
                        }
                        authViewModel.register(
                            email = email,
                            password = password,
                            role = role!!,
                            firstName = firstName,
                            lastName = lastName,
                            studentLevel = studentLevel,
                            dateOfBirth = dob,
                            gender = gender
                        )
                    } catch (_: Exception) {
                        dateError = "Invalid date format. Use yyyy-MM-dd (e.g., 2000-01-15)"
                    }
                } else {
                    authViewModel.register(
                        email = email,
                        password = password,
                        role = role!!,
                        firstName = firstName,
                        lastName = lastName
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading && role != null &&
                    (role != Role.STUDENT || (studentLevel != null && dateOfBirth.isNotBlank() && gender != null && dateError.isEmpty()))
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Register")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text(
                "Already have an account? Login",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

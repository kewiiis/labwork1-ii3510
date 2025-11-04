package com.tumme.scrudstudents.ui.student

import com.tumme.scrudstudents.ui.components.TableHeader
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Écran de liste des étudiants utilisant Jetpack Compose.
 * 
 * Cet écran fait partie de la couche UI de l'architecture MVVM.
 * Il affiche la liste des étudiants dans une interface Material Design 3.
 * 
 * Responsabilités :
 * - Afficher la liste des étudiants via LazyColumn
 * - Gérer les interactions utilisateur (ajout, suppression, navigation)
 * - Collecter l'état du ViewModel via collectAsState()
 * - Déclencher des recompositions quand les données changent
 * 
 * Liaisons :
 * - Amont : StudentListViewModel (injection Hilt)
 * - Aval : Navigation vers StudentFormScreen et StudentDetailScreen
 * 
 * Note pédagogique : collectAsState() collecte le StateFlow du ViewModel
 * et déclenche une recomposition automatique quand les données changent.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(
    viewModel: StudentListViewModel = hiltViewModel(),
    onNavigateToForm: () -> Unit = {},
    onNavigateToDetail: (Int) -> Unit = {}
) {
    /**
     * Collecte de l'état des étudiants depuis le ViewModel.
     * 
     * collectAsState() convertit le StateFlow<List<StudentEntity>> en State<List<StudentEntity>>.
     * Chaque fois que le StateFlow émet une nouvelle valeur (changement en base),
     * cette ligne déclenche une recomposition de tout l'écran.
     * 
     * Note pédagogique : C'est le point clé de la réactivité Compose + StateFlow.
     */
    val students by viewModel.students.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Students") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToForm) {
                Text("+")
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
        ) {
            TableHeader(cells = listOf("DOB","Last", "First", "Gender", "Actions"),
                weights = listOf(0.25f, 0.25f, 0.25f, 0.15f, 0.10f))

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(students) { student ->
                    StudentRow(
                        student = student,
                        onEdit = { /* navigate to form prefilled (not implemented here) */ },
                        onDelete = { viewModel.deleteStudent(student) },
                        onView = { onNavigateToDetail(student.idStudent) },
                        onShare = { /* share intent */ }
                    )
                }
            }
        }
    }
}

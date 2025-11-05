package com.tumme.scrudstudents.ui.course

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tumme.scrudstudents.data.local.model.CourseEntity
import kotlinx.coroutines.launch

/**
 * Écran de détail d'un cours.
 * 
 * Cet écran fait partie de la couche UI de l'architecture MVVM.
 * Il affiche les informations détaillées d'un cours spécifique.
 * 
 * Responsabilités :
 * - Afficher les informations complètes du cours
 * - Gérer les interactions utilisateur (retour, édition)
 * - Charger les données du cours depuis le ViewModel
 * 
 * Liaisons :
 * - Amont : CourseListViewModel (injection Hilt)
 * - Aval : Navigation de retour vers la liste
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: Int,
    viewModel: CourseListViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    /**
     * État local pour stocker les données du cours.
     * 
     * L'état est chargé de manière asynchrone depuis le ViewModel.
     * Un indicateur de chargement est affiché pendant la récupération.
     */
    var course by remember { mutableStateOf<CourseEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    val coroutineScope = rememberCoroutineScope()

    /**
     * Chargement des données du cours au montage du composable.
     * 
     * LaunchedEffect lance une coroutine qui :
     * 1. Appelle le ViewModel pour récupérer le cours
     * 2. Met à jour l'état local avec les données
     * 3. Gère les erreurs potentielles
     */
    LaunchedEffect(courseId) {
        coroutineScope.launch {
            try {
                course = viewModel.findCourse(courseId)
                isLoading = false
            } catch (e: Exception) {
                error = e.message
                isLoading = false
            }
        }
    }

    /**
     * Scaffold Material 3 avec TopAppBar.
     * 
     * La structure suit les guidelines Material Design 3 :
     * - TopAppBar avec titre et bouton retour
     * - Content area pour les détails du cours
     */
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            when {
                isLoading -> {
                    /**
                     * Indicateur de chargement.
                     * 
                     * Affiché pendant la récupération des données du cours.
                     */
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                error != null -> {
                    /**
                     * Affichage d'erreur.
                     * 
                     * Affiché si une erreur survient lors du chargement.
                     */
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Error loading course",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
                
                course != null -> {
                    /**
                     * Affichage des détails du cours.
                     * 
                     * Card Material 3 avec les informations du cours :
                     * - Nom du cours
                     * - Crédits ECTS
                     * - Niveau du cours
                     */
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = course!!.nameCourse,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "ECTS Credits",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = course!!.ectsCourse.toString(),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                
                                Column {
                                    Text(
                                        text = "Level",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = course!!.levelCourse.value,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
                
                else -> {
                    /**
                     * Cours non trouvé.
                     * 
                     * Affiché si le cours n'existe pas dans la base de données.
                     */
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Course not found",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "The course with ID $courseId does not exist.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

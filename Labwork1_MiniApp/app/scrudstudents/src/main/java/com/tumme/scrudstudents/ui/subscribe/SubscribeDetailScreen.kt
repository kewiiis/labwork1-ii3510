package com.tumme.scrudstudents.ui.subscribe

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tumme.scrudstudents.data.local.model.SubscribeEntity
import kotlinx.coroutines.launch

/**
 * Écran de détail d'une inscription.
 * 
 * Cet écran fait partie de la couche UI de l'architecture MVVM.
 * Il affiche les informations détaillées d'une inscription spécifique.
 * 
 * Responsabilités :
 * - Afficher les informations complètes de l'inscription
 * - Gérer les interactions utilisateur (retour, édition)
 * - Charger les données de l'inscription depuis le ViewModel
 * 
 * Liaisons :
 * - Amont : SubscribeListViewModel (injection Hilt)
 * - Aval : Navigation de retour vers la liste
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscribeDetailScreen(
    studentId: Int,
    courseId: Int,
    viewModel: SubscribeListViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    /**
     * Collecte des données nécessaires depuis le ViewModel.
     * 
     * Les listes d'étudiants et de cours sont nécessaires pour afficher les noms.
     */
    val students by viewModel.students.collectAsState()
    val courses by viewModel.courses.collectAsState()
    
    /**
     * État local pour stocker les données de l'inscription.
     * 
     * L'état est chargé de manière asynchrone depuis le ViewModel.
     * Un indicateur de chargement est affiché pendant la récupération.
     */
    var subscribe by remember { mutableStateOf<SubscribeEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    val coroutineScope = rememberCoroutineScope()

    /**
     * Chargement des données de l'inscription au montage du composable.
     * 
     * LaunchedEffect lance une coroutine qui :
     * 1. Appelle le ViewModel pour récupérer l'inscription
     * 2. Met à jour l'état local avec les données
     * 3. Gère les erreurs potentielles
     */
    LaunchedEffect(studentId, courseId) {
        coroutineScope.launch {
            try {
                subscribe = viewModel.findSubscribe(studentId, courseId)
                isLoading = false
            } catch (e: Exception) {
                error = e.message
                isLoading = false
            }
        }
    }

    /**
     * Jointure côté UI pour récupérer les noms.
     * 
     * Cette approche est simple et efficace pour de petites listes.
     * Pour de grandes listes, il serait préférable de faire la jointure
     * côté DAO avec des requêtes SQL JOIN.
     */
    val student = students.find { it.idStudent == studentId }
    val course = courses.find { it.idCourse == courseId }
    
    val studentName = student?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown Student"
    val courseName = course?.nameCourse ?: "Unknown Course"

    /**
     * Scaffold Material 3 avec TopAppBar.
     * 
     * La structure suit les guidelines Material Design 3 :
     * - TopAppBar avec titre et bouton retour
     * - Content area pour les détails de l'inscription
     */
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscription Details") },
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
                     * Affiché pendant la récupération des données de l'inscription.
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
                                text = "Error loading subscription",
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
                
                subscribe != null -> {
                    /**
                     * Affichage des détails de l'inscription.
                     * 
                     * Card Material 3 avec les informations de l'inscription :
                     * - Nom de l'étudiant
                     * - Nom du cours
                     * - Score
                     */
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Subscription Details",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Student",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = studentName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                
                                Column {
                                    Text(
                                        text = "Course",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = courseName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Score",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = subscribe!!.score.toString(),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                
                                Column {
                                    Text(
                                        text = "Student ID",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = subscribe!!.studentId.toString(),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                
                                Column {
                                    Text(
                                        text = "Course ID",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = subscribe!!.courseId.toString(),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
                
                else -> {
                    /**
                     * Inscription non trouvée.
                     * 
                     * Affiché si l'inscription n'existe pas dans la base de données.
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
                                text = "Subscription not found",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "The subscription with Student ID $studentId and Course ID $courseId does not exist.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}


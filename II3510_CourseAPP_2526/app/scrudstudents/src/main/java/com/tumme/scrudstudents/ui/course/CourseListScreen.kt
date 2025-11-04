package com.tumme.scrudstudents.ui.course

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
 * Écran de liste des cours utilisant Jetpack Compose.
 * 
 * Cet écran fait partie de la couche UI de l'architecture MVVM.
 * Il affiche la liste des cours dans une interface Material Design 3.
 * 
 * Responsabilités :
 * - Afficher la liste des cours via LazyColumn
 * - Gérer les interactions utilisateur (ajout, suppression, navigation)
 * - Collecter l'état du ViewModel via collectAsState()
 * - Déclencher des recompositions quand les données changent
 * 
 * Liaisons :
 * - Amont : CourseListViewModel (injection Hilt)
 * - Aval : Navigation vers CourseFormScreen et CourseDetailScreen
 * 
 * Note pédagogique : collectAsState() collecte le StateFlow du ViewModel
 * et déclenche une recomposition automatique quand les données changent.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(
    viewModel: CourseListViewModel = hiltViewModel(),
    onNavigateToForm: () -> Unit = {},
    onNavigateToDetail: (Int) -> Unit = {}
) {
    /**
     * Collecte de l'état des cours depuis le ViewModel.
     * 
     * collectAsState() convertit le StateFlow<List<CourseEntity>> en State<List<CourseEntity>>.
     * Chaque fois que le StateFlow émet une nouvelle valeur (changement en base),
     * cette ligne déclenche une recomposition de tout l'écran.
     * 
     * Note pédagogique : C'est le point clé de la réactivité Compose + StateFlow.
     */
    val courses by viewModel.courses.collectAsState()
    
    val coroutineScope = rememberCoroutineScope()

    /**
     * Scaffold Material 3 avec TopAppBar et FloatingActionButton.
     * 
     * La structure suit les guidelines Material Design 3 :
     * - TopAppBar pour le titre et les actions
     * - FloatingActionButton pour l'action principale (ajouter)
     * - Content area pour la liste des cours
     */
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Courses") })
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
            /**
             * En-tête de tableau avec colonnes définies.
             * 
             * TableHeader est un composant personnalisé qui affiche
             * les titres des colonnes avec des poids proportionnels.
             */
            TableHeader(cells = listOf("Name", "ECTS", "Level", "Actions"),
                weights = listOf(0.4f, 0.2f, 0.2f, 0.2f))

            Spacer(modifier = Modifier.height(8.dp))

            /**
             * LazyColumn pour afficher la liste des cours.
             * 
             * LazyColumn est optimisé pour les listes longues :
             * - Seuls les éléments visibles sont composés
             * - items() itère sur la liste et compose chaque CourseRow
             * - Chaque CourseRow reçoit le cours et les callbacks d'action
             * 
             * Note pédagogique : Quand la liste "courses" change (via collectAsState),
             * LazyColumn se recompose automatiquement et affiche les nouvelles données.
             */
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(courses) { course ->
                    CourseRow(
                        course = course,
                        onEdit = { /* navigate to form prefilled (not implemented here) */ },
                        onDelete = { viewModel.deleteCourse(course) }, // Appel direct au ViewModel
                        onView = { onNavigateToDetail(course.idCourse) }, // Navigation vers détail
                        onShare = { /* share intent */ }
                    )
                }
            }
        }
    }
}

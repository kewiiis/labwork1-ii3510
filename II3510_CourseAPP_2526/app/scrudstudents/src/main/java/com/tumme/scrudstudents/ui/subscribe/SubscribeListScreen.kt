package com.tumme.scrudstudents.ui.subscribe

import com.tumme.scrudstudents.ui.components.TableHeader
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tumme.scrudstudents.data.local.model.StudentEntity
import com.tumme.scrudstudents.data.local.model.CourseEntity
import com.tumme.scrudstudents.data.local.model.SubscribeEntity

/**
 * Écran de liste des inscriptions utilisant Jetpack Compose.
 * 
 * Cet écran fait partie de la couche UI de l'architecture MVVM.
 * Il affiche la liste des inscriptions avec les noms des étudiants et des cours.
 * 
 * Responsabilités :
 * - Afficher la liste des inscriptions via LazyColumn
 * - Gérer les interactions utilisateur (ajout, suppression, navigation)
 * - Collecter l'état du ViewModel via collectAsState()
 * - Déclencher des recompositions quand les données changent
 * 
 * Liaisons :
 * - Amont : SubscribeListViewModel (injection Hilt)
 * - Aval : Navigation vers SubscribeFormScreen et SubscribeDetailScreen
 * 
 * Note pédagogique : collectAsState() collecte le StateFlow du ViewModel
 * et déclenche une recomposition automatique quand les données changent.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscribeListScreen(
    viewModel: SubscribeListViewModel = hiltViewModel(),
    onNavigateToForm: () -> Unit = {},
    onNavigateToDetail: (Int, Int) -> Unit = { _, _ -> }
) {
    /**
     * Collecte de l'état des inscriptions depuis le ViewModel.
     * 
     * collectAsState() convertit le StateFlow<List<SubscribeEntity>> en State<List<SubscribeEntity>>.
     * Chaque fois que le StateFlow émet une nouvelle valeur (changement en base),
     * cette ligne déclenche une recomposition de tout l'écran.
     * 
     * Note pédagogique : C'est le point clé de la réactivité Compose + StateFlow.
     */
    val subscribes by viewModel.subscribes.collectAsState()
    val students by viewModel.students.collectAsState()
    val courses by viewModel.courses.collectAsState()
    
    val coroutineScope = rememberCoroutineScope()

    /**
     * Scaffold Material 3 avec TopAppBar et FloatingActionButton.
     * 
     * La structure suit les guidelines Material Design 3 :
     * - TopAppBar pour le titre et les actions
     * - FloatingActionButton pour l'action principale (ajouter)
     * - Content area pour la liste des inscriptions
     */
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Subscriptions") })
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
            TableHeader(cells = listOf("Student", "Course", "Score", "Actions"),
                weights = listOf(0.35f, 0.35f, 0.15f, 0.15f))

            Spacer(modifier = Modifier.height(8.dp))

            /**
             * LazyColumn pour afficher la liste des inscriptions.
             * 
             * LazyColumn est optimisé pour les listes longues :
             * - Seuls les éléments visibles sont composés
             * - items() itère sur la liste et compose chaque SubscribeRow
             * - Chaque SubscribeRow reçoit l'inscription et les callbacks d'action
             * 
             * Note pédagogique : Quand la liste "subscribes" change (via collectAsState),
             * LazyColumn se recompose automatiquement et affiche les nouvelles données.
             */
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(subscribes) { subscribe ->
                    SubscribeRow(
                        subscribe = subscribe,
                        students = students,
                        courses = courses,
                        onEdit = { /* navigate to form prefilled (not implemented here) */ },
                        onDelete = { viewModel.deleteSubscribe(subscribe) }, // Appel direct au ViewModel
                        onView = onNavigateToDetail, // Navigation vers détail
                        onShare = { /* share intent */ }
                    )
                }
            }
        }
    }
}
package com.tumme.scrudstudents.ui.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumme.scrudstudents.data.local.model.CourseEntity
import com.tumme.scrudstudents.data.repository.SCRUDRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel pour la gestion de la liste des cours.
 * 
 * Cette classe fait partie de la couche Presentation de l'architecture MVVM.
 * Elle gère l'état de l'écran de liste des cours et les interactions utilisateur.
 * 
 * Responsabilités :
 * - Exposer l'état de l'écran via StateFlow (liste des cours)
 * - Gérer les événements utilisateur (suppression, insertion)
 * - Coordonner les opérations avec le Repository
 * - Gérer les coroutines pour les opérations asynchrones
 * 
 * Liaisons :
 * - Amont : SCRUDRepository (injection Hilt)
 * - Aval : CourseListScreen (UI Compose)
 * 
 * Note pédagogique : Le StateFlow est collecté dans l'UI via collectAsState(),
 * ce qui déclenche une recomposition automatique quand les données changent.
 */
@HiltViewModel
class CourseListViewModel @Inject constructor(
    private val repo: SCRUDRepository
) : ViewModel() {

    /**
     * StateFlow contenant la liste des cours.
     * 
     * Ce StateFlow est créé en convertissant le Flow du Repository via stateIn().
     * Il émet automatiquement les changements de la base de données vers l'UI.
     * 
     * Note pédagogique : 
     * - stateIn() convertit un Flow en StateFlow avec une valeur initiale
     * - SharingStarted.Lazily démarre la collecte seulement quand il y a des collecteurs
     * - L'UI collecte ce StateFlow via collectAsState() dans Compose
     * - Chaque changement déclenche une recomposition de l'UI
     */
    private val _courses: StateFlow<List<CourseEntity>> =
        repo.getAllCourses().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val courses: StateFlow<List<CourseEntity>> = _courses

    /**
     * Flow pour les événements UI (messages de succès/erreur).
     * 
     * MutableSharedFlow permet d'émettre des événements depuis le ViewModel
     * vers l'UI (ex: "Course deleted", "Course inserted").
     * 
     * Note : L'UI peut collecter ce Flow pour afficher des SnackBar ou Toast.
     */
    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    /**
     * Supprime un cours de la base de données.
     * 
     * @param course Le cours à supprimer
     * 
     * Note pédagogique :
     * - viewModelScope.launch lance une coroutine liée au cycle de vie du ViewModel
     * - La coroutine est automatiquement annulée si le ViewModel est détruit
     * - Les opérations suspendues (repo.deleteCourse) sont appelées dans la coroutine
     * - L'émission d'événement se fait après la suppression réussie
     */
    fun deleteCourse(course: CourseEntity) = viewModelScope.launch {
        repo.deleteCourse(course)
        _events.emit("Course deleted")
    }

    /**
     * Insère un nouveau cours dans la base de données.
     * 
     * @param course Le cours à insérer
     * 
     * Note pédagogique :
     * - Même principe que deleteCourse avec viewModelScope.launch
     * - L'insertion déclenche automatiquement une mise à jour du StateFlow courses
     * - L'UI se recompose automatiquement grâce à collectAsState()
     */
    fun insertCourse(course: CourseEntity) = viewModelScope.launch {
        repo.insertCourse(course)
        _events.emit("Course inserted")
    }

    /**
     * Recherche un cours par son ID.
     * 
     * @param id L'ID du cours recherché
     * @return CourseEntity? - Le cours trouvé ou null
     * 
     * Note : Fonction suspendue, peut être appelée depuis l'UI dans une coroutine.
     */
    suspend fun findCourse(id: Int) = repo.getCourseById(id)

}

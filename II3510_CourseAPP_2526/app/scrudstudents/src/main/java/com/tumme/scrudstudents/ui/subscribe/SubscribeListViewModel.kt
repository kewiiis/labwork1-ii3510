package com.tumme.scrudstudents.ui.subscribe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumme.scrudstudents.data.local.model.StudentEntity
import com.tumme.scrudstudents.data.local.model.CourseEntity
import com.tumme.scrudstudents.data.local.model.SubscribeEntity
import com.tumme.scrudstudents.data.repository.SCRUDRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel pour la gestion de la liste des inscriptions.
 * 
 * Cette classe fait partie de la couche Presentation de l'architecture MVVM.
 * Elle gère l'état de l'écran de liste des inscriptions et les interactions utilisateur.
 * 
 * Responsabilités :
 * - Exposer l'état de l'écran via StateFlow (liste des inscriptions)
 * - Gérer les événements utilisateur (suppression, insertion)
 * - Coordonner les opérations avec le Repository
 * - Gérer les coroutines pour les opérations asynchrones
 * 
 * Liaisons :
 * - Amont : SCRUDRepository (injection Hilt)
 * - Aval : SubscribeListScreen (UI Compose)
 * 
 * Note pédagogique : Le StateFlow est collecté dans l'UI via collectAsState(),
 * ce qui déclenche une recomposition automatique quand les données changent.
 */
@HiltViewModel
class SubscribeListViewModel @Inject constructor(
    private val repo: SCRUDRepository
) : ViewModel() {

    /**
     * StateFlow contenant la liste des inscriptions.
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
    private val _subscribes: StateFlow<List<SubscribeEntity>> =
        repo.getAllSubscribes().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val subscribes: StateFlow<List<SubscribeEntity>> = _subscribes

    /**
     * StateFlow contenant la liste des étudiants pour les dropdowns.
     * 
     * Utilisé dans le formulaire pour sélectionner un étudiant.
     */
    private val _students: StateFlow<List<StudentEntity>> =
        repo.getAllStudents().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val students: StateFlow<List<StudentEntity>> = _students

    /**
     * StateFlow contenant la liste des cours pour les dropdowns.
     * 
     * Utilisé dans le formulaire pour sélectionner un cours.
     */
    private val _courses: StateFlow<List<CourseEntity>> =
        repo.getAllCourses().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val courses: StateFlow<List<CourseEntity>> = _courses

    /**
     * Flow pour les événements UI (messages de succès/erreur).
     * 
     * MutableSharedFlow permet d'émettre des événements depuis le ViewModel
     * vers l'UI (ex: "Subscribe deleted", "Subscribe inserted").
     * 
     * Note : L'UI peut collecter ce Flow pour afficher des SnackBar ou Toast.
     */
    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    /**
     * Supprime une inscription de la base de données.
     * 
     * @param subscribe L'inscription à supprimer
     * 
     * Note pédagogique :
     * - viewModelScope.launch lance une coroutine liée au cycle de vie du ViewModel
     * - La coroutine est automatiquement annulée si le ViewModel est détruit
     * - Les opérations suspendues (repo.deleteSubscribe) sont appelées dans la coroutine
     * - L'émission d'événement se fait après la suppression réussie
     */
    fun deleteSubscribe(subscribe: SubscribeEntity) = viewModelScope.launch {
        repo.deleteSubscribe(subscribe)
        _events.emit("Subscribe deleted")
    }

    /**
     * Insère une nouvelle inscription dans la base de données.
     * 
     * @param subscribe L'inscription à insérer
     * 
     * Note pédagogique :
     * - Même principe que deleteSubscribe avec viewModelScope.launch
     * - L'insertion déclenche automatiquement une mise à jour du StateFlow subscribes
     * - L'UI se recompose automatiquement grâce à collectAsState()
     */
    fun insertSubscribe(subscribe: SubscribeEntity) = viewModelScope.launch {
        repo.insertSubscribe(subscribe)
        _events.emit("Subscribe inserted")
    }

    /**
     * Recherche une inscription par ses IDs.
     * 
     * @param studentId L'ID de l'étudiant
     * @param courseId L'ID du cours
     * @return SubscribeEntity? - L'inscription trouvée ou null
     * 
     * Note : Fonction suspendue, peut être appelée depuis l'UI dans une coroutine.
     */
    suspend fun findSubscribe(studentId: Int, courseId: Int): SubscribeEntity? {
        val subscribes = subscribes.value
        return subscribes.find { it.studentId == studentId && it.courseId == courseId }
    }
}


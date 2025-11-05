package com.tumme.scrudstudents.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumme.scrudstudents.data.local.model.StudentEntity
import com.tumme.scrudstudents.data.repository.SCRUDRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel pour la gestion de la liste des étudiants.
 * 
 * Cette classe fait partie de la couche Presentation de l'architecture MVVM.
 * Elle gère l'état de l'écran de liste des étudiants et les interactions utilisateur.
 * 
 * Responsabilités :
 * - Exposer l'état de l'écran via StateFlow (liste des étudiants)
 * - Gérer les événements utilisateur (suppression, insertion)
 * - Coordonner les opérations avec le Repository
 * - Gérer les coroutines pour les opérations asynchrones
 * 
 * Liaisons :
 * - Amont : SCRUDRepository (injection Hilt)
 * - Aval : StudentListScreen (UI Compose)
 * 
 * Note pédagogique : Le StateFlow est collecté dans l'UI via collectAsState(),
 * ce qui déclenche une recomposition automatique quand les données changent.
 */
@HiltViewModel
class StudentListViewModel @Inject constructor(
    private val repo: SCRUDRepository
) : ViewModel() {

    /**
     * StateFlow contenant la liste des étudiants.
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
    private val _students: StateFlow<List<StudentEntity>> =
        repo.getAllStudents().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val students: StateFlow<List<StudentEntity>> = _students

    /**
     * Flow pour les événements UI (messages de succès/erreur).
     * 
     * MutableSharedFlow permet d'émettre des événements depuis le ViewModel
     * vers l'UI (ex: "Student deleted", "Student inserted").
     * 
     * Note : L'UI peut collecter ce Flow pour afficher des SnackBar ou Toast.
     */
    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    /**
     * Supprime un étudiant de la base de données.
     * 
     * @param student L'étudiant à supprimer
     * 
     * Note pédagogique :
     * - viewModelScope.launch lance une coroutine liée au cycle de vie du ViewModel
     * - La coroutine est automatiquement annulée si le ViewModel est détruit
     * - Les opérations suspendues (repo.deleteStudent) sont appelées dans la coroutine
     * - L'émission d'événement se fait après la suppression réussie
     */
    fun deleteStudent(student: StudentEntity) = viewModelScope.launch {
        repo.deleteStudent(student)
        _events.emit("Student deleted")
    }

    /**
     * Insère un nouvel étudiant dans la base de données.
     * 
     * @param student L'étudiant à insérer
     * 
     * Note pédagogique :
     * - Même principe que deleteStudent avec viewModelScope.launch
     * - L'insertion déclenche automatiquement une mise à jour du StateFlow students
     * - L'UI se recompose automatiquement grâce à collectAsState()
     */
    fun insertStudent(student: StudentEntity) = viewModelScope.launch {
        repo.insertStudent(student)
        _events.emit("Student inserted")
    }

    /**
     * Recherche un étudiant par son ID.
     * 
     * @param id L'ID de l'étudiant recherché
     * @return StudentEntity? - L'étudiant trouvé ou null
     * 
     * Note : Fonction suspendue, peut être appelée depuis l'UI dans une coroutine.
     */
    suspend fun findStudent(id: Int) = repo.getStudentById(id)

}
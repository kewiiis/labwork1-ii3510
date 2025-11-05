package com.tumme.scrudstudents.ui.subscribe

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tumme.scrudstudents.data.local.model.StudentEntity
import com.tumme.scrudstudents.data.local.model.CourseEntity
import com.tumme.scrudstudents.data.local.model.SubscribeEntity
import com.tumme.scrudstudents.data.local.model.Role
import com.tumme.scrudstudents.viewmodel.AuthViewModel
import com.tumme.scrudstudents.viewmodel.StudentViewModel

/**
 * Écran de formulaire pour créer une nouvelle inscription.
 * 
 * Cet écran fait partie de la couche UI de l'architecture MVVM.
 * Il permet la saisie des informations d'une inscription avec validation.
 * 
 * Responsabilités :
 * - Gérer l'état local du formulaire via remember/mutableStateOf
 * - Valider les données saisies (étudiant et cours sélectionnés, score valide)
 * - Appeler le ViewModel pour insérer l'inscription
 * - Gérer la navigation après sauvegarde
 * 
 * Liaisons :
 * - Amont : SubscribeListViewModel (injection Hilt)
 * - Aval : Navigation de retour vers la liste
 * 
 * Note pédagogique : L'état local du formulaire est géré avec remember/mutableStateOf,
 * ce qui déclenche des recompositions locales quand l'utilisateur tape.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
@Composable
fun SubscribeFormScreen(
    viewModel: SubscribeListViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    studentViewModel: StudentViewModel = hiltViewModel(),
    onSaved: ()->Unit = {}
) {
    /**
     * Collecte des données nécessaires depuis le ViewModel.
     * 
     * Les listes d'étudiants et de cours sont nécessaires pour les dropdowns.
     */
    val students by viewModel.students.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val currentStudent by studentViewModel.currentStudent.collectAsState()
    
    // Déterminer le rôle de l'utilisateur
    val isTeacher = authState is com.tumme.scrudstudents.viewmodel.AuthState.LoggedIn && 
                    (authState as com.tumme.scrudstudents.viewmodel.AuthState.LoggedIn).role == Role.TEACHER
    val isStudent = authState is com.tumme.scrudstudents.viewmodel.AuthState.LoggedIn && 
                    (authState as com.tumme.scrudstudents.viewmodel.AuthState.LoggedIn).role == Role.STUDENT
    
    // Variable locale pour éviter les problèmes de smart cast
    val student = currentStudent
    
    // Charger l'étudiant actuel si c'est un étudiant
    LaunchedEffect(isStudent) {
        if (isStudent) {
            studentViewModel.loadCurrentStudent()
        }
    }
    
    // État de chargement pour les étudiants
    val isLoadingStudent = isStudent && student == null
    
    /**
     * États locaux du formulaire gérés avec remember/mutableStateOf.
     * 
     * Chaque variable d'état déclenche une recomposition locale quand elle change.
     * remember() assure que l'état persiste pendant les recompositions.
     * 
     * Note pédagogique : Ces états sont locaux à ce composable et ne sont pas
     * partagés avec le ViewModel jusqu'à la sauvegarde.
     * 
     * Pour les étudiants : l'étudiant est pré-sélectionné (lui-même) et le score est fixé à 0.
     * Pour les enseignants : ils peuvent sélectionner n'importe quel étudiant et entrer un score.
     */
    var selectedStudent by remember(isStudent, student) { 
        mutableStateOf(if (isStudent && student != null) student else null)
    }
    var selectedCourse by remember { mutableStateOf<CourseEntity?>(null) }
    var score by remember { mutableStateOf("") }
    
    // États pour la validation et les erreurs
    var studentError by remember { mutableStateOf("") }
    var courseError by remember { mutableStateOf("") }
    var scoreError by remember { mutableStateOf("") }

    /**
     * Layout du formulaire avec DropdownMenus Material 3.
     * 
     * Chaque DropdownMenu est lié à un état local via value/onValueChange.
     * Quand l'utilisateur sélectionne, onValueChange met à jour l'état,
     * ce qui déclenche une recomposition et met à jour l'affichage.
     */
    if (isLoadingStudent) {
        // Afficher un indicateur de chargement si l'étudiant est en cours de chargement
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        
        // Sélecteur d'étudiant (seulement pour les enseignants)
        if (isTeacher) {
            var studentExpanded by remember { mutableStateOf(false) }
            
            ExposedDropdownMenuBox(
                expanded = studentExpanded,
                onExpandedChange = { studentExpanded = !studentExpanded }
            ) {
                TextField(
                    value = selectedStudent?.let { "${it.firstName} ${it.lastName}" } ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Select Student") },
                    isError = studentError.isNotEmpty(),
                    supportingText = if (studentError.isNotEmpty()) { { Text(studentError) } } else null,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = studentExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = studentExpanded,
                    onDismissRequest = { studentExpanded = false }
                ) {
                    students.forEach { student ->
                        DropdownMenuItem(
                            text = { Text("${student.firstName} ${student.lastName}") },
                            onClick = {
                                selectedStudent = student
                                studentError = "" // Efface l'erreur quand l'utilisateur sélectionne
                                studentExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        } else if (isStudent && student != null) {
            // Pour les étudiants, afficher leur nom (lecture seule)
            TextField(
                value = "${student.firstName} ${student.lastName}",
                onValueChange = { },
                readOnly = true,
                label = { Text("Student") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }
        
        // Sélecteur de cours
        var courseExpanded by remember { mutableStateOf(false) }
        
        ExposedDropdownMenuBox(
            expanded = courseExpanded,
            onExpandedChange = { courseExpanded = !courseExpanded }
        ) {
            TextField(
                value = selectedCourse?.nameCourse ?: "",
                onValueChange = { },
                readOnly = true,
                label = { Text("Select Course") },
                isError = courseError.isNotEmpty(),
                supportingText = if (courseError.isNotEmpty()) { { Text(courseError) } } else null,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseExpanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = courseExpanded,
                onDismissRequest = { courseExpanded = false }
            ) {
                courses.forEach { course ->
                    DropdownMenuItem(
                        text = { Text(course.nameCourse) },
                        onClick = {
                            selectedCourse = course
                            courseError = "" // Efface l'erreur quand l'utilisateur sélectionne
                            courseExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        
        // Champ score avec validation (seulement pour les enseignants)
        // Les étudiants s'inscrivent sans score (score = 0 par défaut)
        if (isTeacher) {
            TextField(
                value = score, 
                onValueChange = { 
                    score = it
                    scoreError = "" // Efface l'erreur quand l'utilisateur tape
                }, 
                label = { Text("Score (0-20)") },
                isError = scoreError.isNotEmpty(),
                supportingText = if (scoreError.isNotEmpty()) { { Text(scoreError) } } else null
            )
            Spacer(Modifier.height(16.dp))
        } else {
            // Pour les étudiants, le score est fixé à 0 (inscription sans note)
            Text(
                text = "Note: Vous vous inscrivez à ce cours. La note sera attribuée plus tard par l'enseignant.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
        }
        
        /**
         * Bouton de sauvegarde avec validation.
         * 
         * Quand cliqué :
         * 1. Valide les champs (étudiant et cours sélectionnés, score valide)
         * 2. Affiche les erreurs de validation si nécessaire
         * 3. Si valide, crée un SubscribeEntity et l'insère
         * 4. Navigue vers la liste
         * 
         * Note pédagogique : La validation se fait côté UI pour un feedback immédiat.
         * Le ViewModel gère l'insertion et met à jour automatiquement la liste.
         */
        Button(
            onClick = {
                // Validation des champs
                var isValid = true
                
                if (isTeacher && selectedStudent == null) {
                    studentError = "Please select a student"
                    isValid = false
                }
                
                if (selectedCourse == null) {
                    courseError = "Please select a course"
                    isValid = false
                }
                
                // Pour les enseignants, validation du score
                // Pour les étudiants, score = 0 par défaut (inscription sans note)
                val scoreValue = if (isTeacher) {
                    val scoreParsed = score.toFloatOrNull()
                    if (scoreParsed == null || scoreParsed < 0 || scoreParsed > 20) {
                        scoreError = "Score must be between 0 and 20"
                        isValid = false
                        null
                    } else {
                        scoreParsed
                    }
                } else {
                    0f // Étudiants s'inscrivent sans note (score = 0)
                }
                
                if (isValid && scoreValue != null) {
                    val studentId = if (isStudent) {
                        // Dans le bloc else, si isStudent est true, student est toujours non-null
                        student!!.idStudent
                    } else {
                        selectedStudent!!.idStudent // Safe car on a vérifié que selectedStudent != null
                    }
                    
                    val subscribe = SubscribeEntity(
                        studentId = studentId,
                        courseId = selectedCourse!!.idCourse,
                        score = scoreValue
                    )
                    viewModel.insertSubscribe(subscribe)
                    onSaved()
                }
            },
            enabled = !isLoadingStudent && (isTeacher || isStudent)
        ) {
            Text(if (isStudent) "Subscribe to Course" else "Save Subscription")
        }
        }
    }
}


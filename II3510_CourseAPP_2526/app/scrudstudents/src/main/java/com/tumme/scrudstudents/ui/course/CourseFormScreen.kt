package com.tumme.scrudstudents.ui.course

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tumme.scrudstudents.data.local.model.CourseEntity
import com.tumme.scrudstudents.data.local.model.LevelCourse

/**
 * Écran de formulaire pour créer un nouveau cours.
 * 
 * Cet écran fait partie de la couche UI de l'architecture MVVM.
 * Il permet la saisie des informations d'un cours avec validation.
 * 
 * Responsabilités :
 * - Gérer l'état local du formulaire via remember/mutableStateOf
 * - Valider les données saisies (ECTS > 0, niveau valide)
 * - Appeler le ViewModel pour insérer le cours
 * - Gérer la navigation après sauvegarde
 * 
 * Liaisons :
 * - Amont : CourseListViewModel (injection Hilt)
 * - Aval : Navigation de retour vers la liste
 * 
 * Note pédagogique : L'état local du formulaire est géré avec remember/mutableStateOf,
 * ce qui déclenche des recompositions locales quand l'utilisateur tape.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseFormScreen(
    viewModel: CourseListViewModel = hiltViewModel(),
    teacherViewModel: com.tumme.scrudstudents.viewmodel.TeacherViewModel = hiltViewModel(),
    onSaved: ()->Unit = {}
) {
    /**
     * Load current teacher if accessed from teacher context.
     */
    LaunchedEffect(Unit) {
        teacherViewModel.loadCurrentTeacher()
    }
    val currentTeacher by teacherViewModel.currentTeacher.collectAsState()
    
    /**
     * États locaux du formulaire gérés avec remember/mutableStateOf.
     * 
     * Chaque variable d'état déclenche une recomposition locale quand elle change.
     * remember() assure que l'état persiste pendant les recompositions.
     * 
     * Note pédagogique : Ces états sont locaux à ce composable et ne sont pas
     * partagés avec le ViewModel jusqu'à la sauvegarde.
     */
    var nameCourse by remember { mutableStateOf("") }
    var ectsCourse by remember { mutableStateOf("") }
    var levelCourse by remember { mutableStateOf(LevelCourse.P1) }
    
    // États pour la validation et les erreurs
    var nameError by remember { mutableStateOf("") }
    var ectsError by remember { mutableStateOf("") }

    /**
     * Layout du formulaire avec TextFields Material 3.
     * 
     * Chaque TextField est lié à un état local via value/onValueChange.
     * Quand l'utilisateur tape, onValueChange met à jour l'état,
     * ce qui déclenche une recomposition et met à jour l'affichage.
     */
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Champ nom du cours
        TextField(
            value = nameCourse, 
            onValueChange = { 
                nameCourse = it
                nameError = "" // Efface l'erreur quand l'utilisateur tape
            }, 
            label = { Text("Course Name") },
            isError = nameError.isNotEmpty(),
            supportingText = if (nameError.isNotEmpty()) { { Text(nameError) } } else null
        )
        Spacer(Modifier.height(8.dp))
        
        // Champ ECTS avec validation
        TextField(
            value = ectsCourse, 
            onValueChange = { 
                ectsCourse = it
                ectsError = "" // Efface l'erreur quand l'utilisateur tape
            }, 
            label = { Text("ECTS Credits") },
            isError = ectsError.isNotEmpty(),
            supportingText = if (ectsError.isNotEmpty()) { { Text(ectsError) } } else null
        )
        Spacer(Modifier.height(8.dp))

        /**
         * Sélecteur de niveau avec DropdownMenu Material 3.
         * 
         * Interface moderne avec dropdown pour sélectionner le niveau du cours.
         * Le dropdown affiche toutes les options de LevelCourse.
         */
        var expanded by remember { mutableStateOf(false) }
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = levelCourse.value,
                onValueChange = { },
                readOnly = true,
                label = { Text("Course Level") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                LevelCourse.entries.forEach { level ->
                    DropdownMenuItem(
                        text = { Text(level.value) },
                        onClick = {
                            levelCourse = level
                            expanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        /**
         * Bouton de sauvegarde avec validation.
         * 
         * Quand cliqué :
         * 1. Valide les champs (nom non vide, ECTS > 0)
         * 2. Affiche les erreurs de validation si nécessaire
         * 3. Si valide, crée un CourseEntity et l'insère
         * 4. Navigue vers la liste
         * 
         * Note pédagogique : La validation se fait côté UI pour un feedback immédiat.
         * Le ViewModel gère l'insertion et met à jour automatiquement la liste.
         */
        Button(onClick = {
            // Validation des champs
            var isValid = true
            
            if (nameCourse.isBlank()) {
                nameError = "Course name is required"
                isValid = false
            }
            
            val ectsValue = ectsCourse.toFloatOrNull()
            if (ectsValue == null || ectsValue <= 0) {
                ectsError = "ECTS must be a positive number"
                isValid = false
            }
            
            if (isValid) {
                val course = CourseEntity(
                    nameCourse = nameCourse.trim(),
                    ectsCourse = ectsValue!!,
                    levelCourse = levelCourse,
                    teacherId = currentTeacher?.idTeacher ?: 0 // Teacher ID (0 if not from teacher context)
                )
                viewModel.insertCourse(course)
                onSaved()
            }
        }) {
            Text("Save Course")
        }
    }
}


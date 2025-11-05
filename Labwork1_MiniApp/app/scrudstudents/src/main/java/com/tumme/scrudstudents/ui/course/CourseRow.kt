package com.tumme.scrudstudents.ui.course

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tumme.scrudstudents.data.local.model.CourseEntity

/**
 * Composant pour afficher une ligne de cours dans la liste.
 * 
 * Ce composant fait partie de la couche UI de l'architecture MVVM.
 * Il affiche les informations d'un cours avec des actions (éditer, supprimer, voir, partager).
 * 
 * Responsabilités :
 * - Afficher les informations du cours (nom, ECTS, niveau)
 * - Gérer les interactions utilisateur (boutons d'action)
 * - Déclencher les callbacks appropriés
 * 
 * Liaisons :
 * - Amont : CourseEntity (données du cours)
 * - Aval : Callbacks vers le parent (CourseListScreen)
 */
@Composable
fun CourseRow(
    course: CourseEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onView: () -> Unit,
    onShare: () -> Unit
) {
    /**
     * Layout horizontal avec les informations du cours et les actions.
     * 
     * La structure suit le même pattern que StudentRow :
     * - Informations du cours sur la gauche
     * - Boutons d'action sur la droite
     * - Utilisation de Material 3 pour la cohérence visuelle
     */
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Informations du cours
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = course.nameCourse,
                modifier = Modifier.weight(0.4f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = course.ectsCourse.toString(),
                modifier = Modifier.weight(0.2f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = course.levelCourse.value,
                modifier = Modifier.weight(0.2f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        // Boutons d'action
        Row(modifier = Modifier.weight(0.2f)) {
            IconButton(onClick = onView) {
                Text("👁", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onEdit) {
                Text("✏", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDelete) {
                Text("🗑", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onShare) {
                Text("📤", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

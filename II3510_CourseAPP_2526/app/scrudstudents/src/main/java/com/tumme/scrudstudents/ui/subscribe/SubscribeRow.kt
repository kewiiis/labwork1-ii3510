package com.tumme.scrudstudents.ui.subscribe

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tumme.scrudstudents.data.local.model.StudentEntity
import com.tumme.scrudstudents.data.local.model.CourseEntity
import com.tumme.scrudstudents.data.local.model.SubscribeEntity

/**
 * Composant pour afficher une ligne d'inscription dans la liste.
 * 
 * Ce composant fait partie de la couche UI de l'architecture MVVM.
 * Il affiche les informations d'une inscription avec les noms des étudiants et des cours.
 * 
 * Responsabilités :
 * - Afficher les informations de l'inscription (étudiant, cours, score)
 * - Gérer les interactions utilisateur (boutons d'action)
 * - Déclencher les callbacks appropriés
 * - Effectuer la jointure pour afficher les noms au lieu des IDs
 * 
 * Liaisons :
 * - Amont : SubscribeEntity, List<StudentEntity>, List<CourseEntity> (données)
 * - Aval : Callbacks vers le parent (SubscribeListScreen)
 * 
 * Note pédagogique : Ce composant effectue la jointure côté UI pour afficher
 * les noms des étudiants et des cours au lieu des IDs. C'est une approche simple
 * mais efficace pour de petites listes.
 */
@Composable
fun SubscribeRow(
    subscribe: SubscribeEntity,
    students: List<StudentEntity>,
    courses: List<CourseEntity>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onView: (Int, Int) -> Unit,
    onShare: () -> Unit
) {
    /**
     * Jointure côté UI pour récupérer les noms.
     * 
     * Cette approche est simple et efficace pour de petites listes.
     * Pour de grandes listes, il serait préférable de faire la jointure
     * côté DAO avec des requêtes SQL JOIN.
     * 
     * Note pédagogique : La jointure se fait en cherchant l'entité correspondante
     * dans les listes d'étudiants et de cours par ID.
     */
    val student = students.find { it.idStudent == subscribe.studentId }
    val course = courses.find { it.idCourse == subscribe.courseId }
    
    val studentName = student?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown Student"
    val courseName = course?.nameCourse ?: "Unknown Course"

    /**
     * Layout horizontal avec les informations de l'inscription et les actions.
     * 
     * La structure suit le même pattern que StudentRow et CourseRow :
     * - Informations de l'inscription sur la gauche
     * - Boutons d'action sur la droite
     * - Utilisation de Material 3 pour la cohérence visuelle
     */
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Informations de l'inscription
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = studentName,
                modifier = Modifier.weight(0.35f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = courseName,
                modifier = Modifier.weight(0.35f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = subscribe.score.toString(),
                modifier = Modifier.weight(0.15f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        // Boutons d'action
        Row(modifier = Modifier.weight(0.15f)) {
            IconButton(onClick = { onView(subscribe.studentId, subscribe.courseId) }) {
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


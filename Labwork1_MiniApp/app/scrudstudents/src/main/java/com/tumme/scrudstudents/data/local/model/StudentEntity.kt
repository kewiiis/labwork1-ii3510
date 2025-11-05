package com.tumme.scrudstudents.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entité Room pour la table students.
 * Définit la structure des données d'un étudiant.
 * 
 * Links to UserEntity via userId foreign key for authentication.
 * 
 * Data Flow: DB → DAO → Repository → ViewModel → UI
 */
@Entity(
    tableName = "students",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val idStudent: Int = 0,
    val userId: Int, // FK to UserEntity
    val lastName: String,
    val firstName: String,
    val dateOfBirth: Date,
    val gender: Gender,
    val level: LevelCourse // Student's study level (P1, P2, B1, etc.)
)
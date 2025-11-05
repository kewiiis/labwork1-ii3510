package com.tumme.scrudstudents.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a teacher.
 * 
 * Similar structure to StudentEntity but for teachers.
 * Links to UserEntity via userId foreign key.
 * 
 * Data Flow: DB → DAO → Repository → ViewModel → UI
 */
@Entity(
    tableName = "teachers",
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
data class TeacherEntity(
    @PrimaryKey(autoGenerate = true)
    val idTeacher: Int = 0,
    val userId: Int, // FK to UserEntity
    val lastName: String,
    val firstName: String
)

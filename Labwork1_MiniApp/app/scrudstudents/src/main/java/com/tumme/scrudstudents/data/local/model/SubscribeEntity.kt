package com.tumme.scrudstudents.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a student's subscription to a course with a score.
 * 
 * Links Student and Course entities via foreign keys.
 * Score is on a 0..20 scale (validated in ViewModel).
 * 
 * Data Flow: DB → DAO → Repository → ViewModel → UI
 * 
 * Note: ON DELETE CASCADE ensures that when a student or course is deleted,
 * all related subscriptions are automatically removed.
 */
@Entity(
    tableName = "subscribes",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["idStudent"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["idCourse"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("studentId"), Index("courseId")]
)
data class SubscribeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val studentId: Int, // FK to StudentEntity
    val courseId: Int, // FK to CourseEntity
    val score: Float // Score on 0..20 scale (validated in ViewModel)
)
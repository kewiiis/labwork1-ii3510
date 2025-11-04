package com.tumme.scrudstudents.data.local.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "courses",
    indices = [Index("teacherId")]
)
data class CourseEntity(
    @PrimaryKey(autoGenerate = true)
    val idCourse: Int = 0,
    val nameCourse: String,
    val ectsCourse: Float,
    val levelCourse: LevelCourse,
    val teacherId: Int // FK to TeacherEntity (who teaches this course)
)
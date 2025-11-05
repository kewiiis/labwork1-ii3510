package com.tumme.scrudstudents.data.local.dao

import androidx.room.*
import com.tumme.scrudstudents.data.local.model.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY nameCourse")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE levelCourse = :level ORDER BY nameCourse")
    fun getCoursesByLevel(level: String): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE teacherId = :teacherId ORDER BY nameCourse")
    fun getCoursesByTeacher(teacherId: Int): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: CourseEntity)

    @Delete
    suspend fun delete(course: CourseEntity)

    @Query("SELECT * FROM courses WHERE idCourse = :id LIMIT 1")
    suspend fun getCourseById(id: Int): CourseEntity?
}
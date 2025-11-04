package com.tumme.scrudstudents.data.repository

import com.tumme.scrudstudents.data.local.dao.CourseDao
import com.tumme.scrudstudents.data.local.model.CourseEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Course operations.
 * 
 * Abstracts data access for courses.
 * 
 * Data Flow: DB → CourseDao → CourseRepository → ViewModel → UI
 */
@Singleton
class CourseRepository @Inject constructor(
    private val courseDao: CourseDao
) {
    fun getAllCourses(): Flow<List<CourseEntity>> = courseDao.getAllCourses()

    fun getCoursesByLevel(level: String): Flow<List<CourseEntity>> = courseDao.getCoursesByLevel(level)

    fun getCoursesByTeacher(teacherId: Int): Flow<List<CourseEntity>> = courseDao.getCoursesByTeacher(teacherId)

    suspend fun getCourseById(id: Int): CourseEntity? = courseDao.getCourseById(id)

    suspend fun insertCourse(course: CourseEntity) = courseDao.insert(course)

    suspend fun deleteCourse(course: CourseEntity) = courseDao.delete(course)
}

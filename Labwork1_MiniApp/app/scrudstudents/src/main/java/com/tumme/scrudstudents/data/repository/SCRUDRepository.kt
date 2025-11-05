package com.tumme.scrudstudents.data.repository

import com.tumme.scrudstudents.data.local.dao.CourseDao
import com.tumme.scrudstudents.data.local.dao.StudentDao
import com.tumme.scrudstudents.data.local.dao.SubscribeDao
import com.tumme.scrudstudents.data.local.model.CourseEntity
import com.tumme.scrudstudents.data.local.model.StudentEntity
import com.tumme.scrudstudents.data.local.model.SubscribeEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository centralisé pour toutes les opérations de données.
 * Abstraction entre les ViewModels et les DAOs.
 */
class SCRUDRepository(
    private val studentDao: StudentDao,
    private val courseDao: CourseDao,
    private val subscribeDao: SubscribeDao
) {
    // Students
    fun getAllStudents(): Flow<List<StudentEntity>> = studentDao.getAllStudents()
    suspend fun insertStudent(student: StudentEntity) = studentDao.insert(student)
    suspend fun deleteStudent(student: StudentEntity) = studentDao.delete(student)
    suspend fun getStudentById(id: Int) = studentDao.getStudentById(id)

    // Courses
    fun getAllCourses(): Flow<List<CourseEntity>> = courseDao.getAllCourses()
    suspend fun insertCourse(course: CourseEntity) = courseDao.insert(course)
    suspend fun deleteCourse(course: CourseEntity) = courseDao.delete(course)
    suspend fun getCourseById(id: Int) = courseDao.getCourseById(id)

    // Subscribes
    fun getAllSubscribes(): Flow<List<SubscribeEntity>> = subscribeDao.getAllSubscribes()
    fun getSubscribesByStudent(sId: Int): Flow<List<SubscribeEntity>> = subscribeDao.getSubscribesByStudent(sId)
    fun getSubscribesByCourse(cId: Int): Flow<List<SubscribeEntity>> = subscribeDao.getSubscribesByCourse(cId)
    suspend fun insertSubscribe(subscribe: SubscribeEntity) = subscribeDao.insert(subscribe)
    suspend fun deleteSubscribe(subscribe: SubscribeEntity) = subscribeDao.delete(subscribe)
}
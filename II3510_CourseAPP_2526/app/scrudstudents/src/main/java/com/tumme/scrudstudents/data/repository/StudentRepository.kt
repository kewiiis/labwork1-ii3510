package com.tumme.scrudstudents.data.repository

import com.tumme.scrudstudents.data.local.dao.StudentDao
import com.tumme.scrudstudents.data.local.model.StudentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Student operations.
 * 
 * Abstracts data access for students.
 * 
 * Data Flow: DB → StudentDao → StudentRepository → ViewModel → UI
 */
@Singleton
class StudentRepository @Inject constructor(
    private val studentDao: StudentDao
) {
    fun getAllStudents(): Flow<List<StudentEntity>> = studentDao.getAllStudents()

    suspend fun getStudentById(id: Int): StudentEntity? = studentDao.getStudentById(id)

    suspend fun getStudentByUserId(userId: Int): StudentEntity? = studentDao.getStudentByUserId(userId)

    suspend fun insertStudent(student: StudentEntity) = studentDao.insert(student)

    suspend fun deleteStudent(student: StudentEntity) = studentDao.delete(student)
}

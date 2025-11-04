package com.tumme.scrudstudents.data.repository

import com.tumme.scrudstudents.data.local.dao.TeacherDao
import com.tumme.scrudstudents.data.local.model.TeacherEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Teacher operations.
 * 
 * Abstracts data access for teachers.
 * 
 * Data Flow: DB → TeacherDao → TeacherRepository → ViewModel → UI
 */
@Singleton
class TeacherRepository @Inject constructor(
    private val teacherDao: TeacherDao
) {
    fun getAllTeachers(): Flow<List<TeacherEntity>> = teacherDao.getAllTeachers()

    suspend fun getTeacherById(id: Int): TeacherEntity? = teacherDao.getTeacherById(id)

    suspend fun getTeacherByUserId(userId: Int): TeacherEntity? = teacherDao.getTeacherByUserId(userId)

    suspend fun insertTeacher(teacher: TeacherEntity) = teacherDao.insert(teacher)

    suspend fun deleteTeacher(teacher: TeacherEntity) = teacherDao.delete(teacher)
}

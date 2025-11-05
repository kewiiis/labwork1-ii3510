package com.tumme.scrudstudents.data.local.dao

import androidx.room.*
import com.tumme.scrudstudents.data.local.model.TeacherEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Teacher entity operations.
 * 
 * Handles CRUD operations for teachers.
 * 
 * Data Flow: DB → TeacherDao → Repository → ViewModel → UI
 */
@Dao
interface TeacherDao {
    @Query("SELECT * FROM teachers ORDER BY lastName, firstName")
    fun getAllTeachers(): Flow<List<TeacherEntity>>

    @Query("SELECT * FROM teachers WHERE idTeacher = :id LIMIT 1")
    suspend fun getTeacherById(id: Int): TeacherEntity?

    @Query("SELECT * FROM teachers WHERE userId = :userId LIMIT 1")
    suspend fun getTeacherByUserId(userId: Int): TeacherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(teacher: TeacherEntity): Long

    @Delete
    suspend fun delete(teacher: TeacherEntity)
}

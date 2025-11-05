package com.tumme.scrudstudents.data.local.dao

import androidx.room.*
import com.tumme.scrudstudents.data.local.model.StudentEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour les opérations CRUD sur la table students.
 * Utilise Room pour les requêtes SQL et Flow pour la réactivité.
 */
@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY lastName, firstName")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentEntity)

    @Delete
    suspend fun delete(student: StudentEntity)

    @Query("SELECT * FROM students WHERE idStudent = :id LIMIT 1")
    suspend fun getStudentById(id: Int): StudentEntity?

    @Query("SELECT * FROM students WHERE userId = :userId LIMIT 1")
    suspend fun getStudentByUserId(userId: Int): StudentEntity?
}
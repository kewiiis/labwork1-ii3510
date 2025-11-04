package com.tumme.scrudstudents.data.local.dao

import androidx.room.*
import com.tumme.scrudstudents.data.local.model.SubscribeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscribeDao {
    @Query("SELECT * FROM subscribes")
    fun getAllSubscribes(): Flow<List<SubscribeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subscribe: SubscribeEntity)

    @Delete
    suspend fun delete(subscribe: SubscribeEntity)

    @Query("SELECT * FROM subscribes WHERE studentId = :sId")
    fun getSubscribesByStudent(sId: Int): Flow<List<SubscribeEntity>>

    @Query("SELECT * FROM subscribes WHERE courseId = :cId")
    fun getSubscribesByCourse(cId: Int): Flow<List<SubscribeEntity>>

    @Query("SELECT * FROM subscribes WHERE studentId = :studentId AND courseId = :courseId LIMIT 1")
    suspend fun getSubscribeByStudentAndCourse(studentId: Int, courseId: Int): SubscribeEntity?

    @Update
    suspend fun update(subscribe: SubscribeEntity)
}
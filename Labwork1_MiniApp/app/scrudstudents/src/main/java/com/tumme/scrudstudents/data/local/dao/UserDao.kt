package com.tumme.scrudstudents.data.local.dao

import androidx.room.*
import com.tumme.scrudstudents.data.local.model.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for User entity operations.
 * 
 * Handles authentication-related database queries.
 * 
 * Data Flow: DB → UserDao → Repository → ViewModel → UI
 */
@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserByIdFlow(id: Int): Flow<UserEntity?>
}

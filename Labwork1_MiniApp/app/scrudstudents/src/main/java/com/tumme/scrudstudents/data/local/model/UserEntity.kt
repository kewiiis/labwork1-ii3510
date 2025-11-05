package com.tumme.scrudstudents.data.local.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a user in the authentication system.
 * 
 * This entity stores user credentials and role information.
 * The email is unique and serves as the login identifier.
 * 
 * Data Flow: DB → DAO → Repository → ViewModel → UI
 * 
 * Note: passwordHash is stored as SHA-256 hash (for educational purposes only,
 * not secure for production - should use proper password hashing like bcrypt).
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val passwordHash: String,
    val role: String // Role.STUDENT.value or Role.TEACHER.value
)

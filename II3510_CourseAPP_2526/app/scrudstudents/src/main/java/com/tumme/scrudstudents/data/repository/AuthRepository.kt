package com.tumme.scrudstudents.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.tumme.scrudstudents.data.local.dao.StudentDao
import com.tumme.scrudstudents.data.local.dao.TeacherDao
import com.tumme.scrudstudents.data.local.dao.UserDao
import com.tumme.scrudstudents.data.local.model.Role
import com.tumme.scrudstudents.data.local.model.StudentEntity
import com.tumme.scrudstudents.data.local.model.TeacherEntity
import com.tumme.scrudstudents.data.local.model.UserEntity
import com.tumme.scrudstudents.util.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for authentication operations.
 * 
 * Handles user registration, login, logout, and session management.
 * Session is persisted using SharedPreferences (key: "current_user_id").
 * 
 * Data Flow: UI → AuthRepository → DAO → DB
 * 
 * Note: Password hashing uses SHA-256 (for educational purposes only,
 * not secure for production).
 */
@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val studentDao: StudentDao,
    private val teacherDao: TeacherDao,
    private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val currentUserIdFlow = MutableStateFlow<Int?>(getCurrentUserId())

    /**
     * Current user ID state flow.
     * Observes changes in the logged-in user.
     */
    val currentUserId: StateFlow<Int?> = currentUserIdFlow.asStateFlow()

    companion object {
        private const val KEY_USER_ID = "current_user_id"
        private const val KEY_USER_EMAIL = "current_user_email"
        private const val KEY_USER_ROLE = "current_user_role"
        private const val KEY_LAST_ACTIVITY = "last_activity_timestamp"
        private const val SESSION_TIMEOUT_MINUTES = 30L // 30 minutes d'inactivité
    }

    /**
     * Registers a new user with the specified role.
     * 
     * @param email User email (unique)
     * @param password Plain text password (will be hashed)
     * @param role User role (STUDENT or TEACHER)
     * @param firstName First name for student/teacher profile
     * @param lastName Last name for student/teacher profile
     * @param studentLevel Student level (only for STUDENT role)
     * @return The created UserEntity, or null if email already exists
     */
    suspend fun register(
        email: String,
        password: String,
        role: Role,
        firstName: String,
        lastName: String,
        studentLevel: com.tumme.scrudstudents.data.local.model.LevelCourse? = null,
        dateOfBirth: Date? = null,
        gender: com.tumme.scrudstudents.data.local.model.Gender? = null
    ): UserEntity? {
        // Check if email already exists
        if (userDao.getUserByEmail(email) != null) {
            return null
        }

        // Hash password
        val passwordHash = PasswordHasher.hash(password)

        // Create user
        val user = UserEntity(
            email = email,
            passwordHash = passwordHash,
            role = role.value
        )
        val userId = userDao.insert(user).toInt()

        // Create role-specific profile
        when (role) {
            Role.STUDENT -> {
                require(studentLevel != null && dateOfBirth != null && gender != null)
                studentDao.insert(
                    StudentEntity(
                        userId = userId,
                        firstName = firstName,
                        lastName = lastName,
                        dateOfBirth = dateOfBirth,
                        gender = gender,
                        level = studentLevel
                    )
                )
            }
            Role.TEACHER -> {
                teacherDao.insert(
                    TeacherEntity(
                        userId = userId,
                        firstName = firstName,
                        lastName = lastName
                    )
                )
            }
        }

        // Save session and update current user flow (like login does)
        val createdUser = user.copy(id = userId)
        saveSession(createdUser)
        currentUserIdFlow.value = userId

        return createdUser
    }

    /**
     * Logs in a user with email and password.
     * 
     * @param email User email
     * @param password Plain text password
     * @return The UserEntity if login successful, null otherwise
     */
    suspend fun login(email: String, password: String): UserEntity? {
        val user = userDao.getUserByEmail(email) ?: return null

        if (PasswordHasher.verify(password, user.passwordHash)) {
            saveSession(user)
            currentUserIdFlow.value = user.id
            return user
        }

        return null
    }

    /**
     * Logs out the current user.
     */
    suspend fun logout() {
        clearSession()
        currentUserIdFlow.value = null
    }

    /**
     * Gets the current logged-in user as a Flow (for reactive updates).
     * 
     * @return Flow of current UserEntity, or null if not logged in
     */
    fun getCurrentUser(): Flow<UserEntity?> {
        val userId = getCurrentUserId()
        return if (userId != null) {
            userDao.getUserByIdFlow(userId)
        } else {
            kotlinx.coroutines.flow.flowOf(null)
        }
    }
    
    /**
     * Gets the current logged-in user synchronously (for initial check).
     * 
     * @return UserEntity if logged in, null otherwise
     */
    suspend fun getCurrentUserSync(): UserEntity? {
        val userId = getCurrentUserId()
        return if (userId != null) {
            userDao.getUserById(userId)
        } else {
            null
        }
    }

    /**
     * Gets current user ID from SharedPreferences.
     */
    private fun getCurrentUserId(): Int? {
        val userId = prefs.getInt(KEY_USER_ID, -1)
        return if (userId == -1) null else userId
    }

    /**
     * Saves session to SharedPreferences.
     */
    private fun saveSession(user: UserEntity) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, user.id)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_ROLE, user.role)
            putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis())
            apply()
        }
    }
    
    /**
     * Updates last activity timestamp.
     * Call this on user interactions to keep session alive.
     */
    fun updateLastActivity() {
        if (getCurrentUserId() != null) {
            prefs.edit().putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis()).apply()
        }
    }
    
    /**
     * Checks if session has expired due to inactivity.
     * @return true if session expired, false otherwise
     */
    fun isSessionExpired(): Boolean {
        val userId = getCurrentUserId()
        if (userId == null) return true
        
        val lastActivity = prefs.getLong(KEY_LAST_ACTIVITY, 0)
        if (lastActivity == 0L) return true
        
        val timeoutMillis = SESSION_TIMEOUT_MINUTES * 60 * 1000
        val elapsed = System.currentTimeMillis() - lastActivity
        return elapsed > timeoutMillis
    }

    /**
     * Clears session from SharedPreferences.
     */
    private fun clearSession() {
        prefs.edit().clear().apply()
        currentUserIdFlow.value = null
    }

    /**
     * Gets current user role from SharedPreferences.
     */
    fun getCurrentUserRole(): Role? {
        val roleString = prefs.getString(KEY_USER_ROLE, null)
        return roleString?.let { Role.from(it) }
    }
}

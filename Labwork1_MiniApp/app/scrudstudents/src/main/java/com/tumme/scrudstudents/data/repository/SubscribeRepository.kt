package com.tumme.scrudstudents.data.repository

import com.tumme.scrudstudents.data.local.dao.CourseDao
import com.tumme.scrudstudents.data.local.dao.SubscribeDao
import com.tumme.scrudstudents.data.local.model.CourseEntity
import com.tumme.scrudstudents.data.local.model.SubscribeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Subscribe operations.
 * 
 * Abstracts data access for subscriptions and handles business logic
 * for weighted grade calculation.
 * 
 * Data Flow: DB → SubscribeDao → SubscribeRepository → ViewModel → UI
 */
@Singleton
class SubscribeRepository @Inject constructor(
    private val subscribeDao: SubscribeDao,
    private val courseDao: CourseDao
) {
    fun getAllSubscribes(): Flow<List<SubscribeEntity>> = subscribeDao.getAllSubscribes()

    fun getSubscribesByStudent(studentId: Int): Flow<List<SubscribeEntity>> =
        subscribeDao.getSubscribesByStudent(studentId)

    fun getSubscribesByCourse(courseId: Int): Flow<List<SubscribeEntity>> =
        subscribeDao.getSubscribesByCourse(courseId)

    suspend fun getSubscribeByStudentAndCourse(studentId: Int, courseId: Int): SubscribeEntity? =
        subscribeDao.getSubscribeByStudentAndCourse(studentId, courseId)

    suspend fun insertSubscribe(subscribe: SubscribeEntity) = subscribeDao.insert(subscribe)

    suspend fun updateSubscribe(subscribe: SubscribeEntity) = subscribeDao.update(subscribe)

    suspend fun deleteSubscribe(subscribe: SubscribeEntity) = subscribeDao.delete(subscribe)

    /**
     * Calculates weighted final grade for a student.
     * 
     * Formula: weighted = Σ(score × ECTS) / Σ(ECTS)
     * 
     * @param subscribes List of subscriptions for the student
     * @param courses List of all courses (to get ECTS values)
     * @return Weighted grade (0..20), or null if no subscriptions or total ECTS is 0
     */
    suspend fun calculateWeightedGrade(
        subscribes: List<SubscribeEntity>,
        courses: List<CourseEntity>
    ): Float? {
        if (subscribes.isEmpty()) return null

        var totalPoints = 0f
        var totalEcts = 0f

        subscribes.forEach { subscribe ->
            val course = courses.find { it.idCourse == subscribe.courseId }
            if (course != null) {
                totalPoints += subscribe.score * course.ectsCourse
                totalEcts += course.ectsCourse
            }
        }

        // Handle division by zero: return null if no ECTS
        return if (totalEcts > 0) {
            totalPoints / totalEcts
        } else {
            null
        }
    }
}

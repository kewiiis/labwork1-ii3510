package com.tumme.scrudstudents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumme.scrudstudents.data.local.model.CourseEntity
import com.tumme.scrudstudents.data.local.model.SubscribeEntity
import com.tumme.scrudstudents.data.repository.CourseRepository
import com.tumme.scrudstudents.data.repository.SubscribeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Subscribe operations.
 * 
 * Manages subscriptions and grade calculations.
 * 
 * Data Flow: DB → SubscribeRepository → ViewModel (StateFlow) → UI (collectAsState)
 */
@HiltViewModel
class SubscribeViewModel @Inject constructor(
    private val subscribeRepository: SubscribeRepository,
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _subscribes: StateFlow<List<SubscribeEntity>> =
        subscribeRepository.getAllSubscribes().stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )
    val subscribes: StateFlow<List<SubscribeEntity>> = _subscribes

    private val _courses: StateFlow<List<CourseEntity>> =
        courseRepository.getAllCourses().stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )
    val courses: StateFlow<List<CourseEntity>> = _courses

    fun getSubscribesByStudent(studentId: Int): Flow<List<SubscribeEntity>> =
        subscribeRepository.getSubscribesByStudent(studentId)

    fun getSubscribesByCourse(courseId: Int): Flow<List<SubscribeEntity>> =
        subscribeRepository.getSubscribesByCourse(courseId)

    fun insertSubscribe(subscribe: SubscribeEntity) = viewModelScope.launch {
        subscribeRepository.insertSubscribe(subscribe)
    }

    fun updateSubscribe(subscribe: SubscribeEntity) = viewModelScope.launch {
        subscribeRepository.updateSubscribe(subscribe)
    }

    fun deleteSubscribe(subscribe: SubscribeEntity) = viewModelScope.launch {
        subscribeRepository.deleteSubscribe(subscribe)
    }

    /**
     * Calculates weighted grade for a student.
     * 
     * @param subscribes List of subscriptions
     * @return Weighted grade (0..20) or null if calculation not possible
     */
    suspend fun calculateWeightedGrade(subscribes: List<SubscribeEntity>): Float? {
        val coursesList = _courses.value
        return subscribeRepository.calculateWeightedGrade(subscribes, coursesList)
    }
}

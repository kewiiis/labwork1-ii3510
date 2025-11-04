package com.tumme.scrudstudents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumme.scrudstudents.data.local.model.CourseEntity
import com.tumme.scrudstudents.data.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Course operations.
 * 
 * Manages course data and CRUD operations.
 * 
 * Data Flow: DB → CourseRepository → ViewModel (StateFlow) → UI (collectAsState)
 */
@HiltViewModel
class CourseViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _courses: StateFlow<List<CourseEntity>> =
        courseRepository.getAllCourses().stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )
    val courses: StateFlow<List<CourseEntity>> = _courses

    fun getCoursesByLevel(level: String): Flow<List<CourseEntity>> =
        courseRepository.getCoursesByLevel(level)

    fun getCoursesByTeacher(teacherId: Int): Flow<List<CourseEntity>> =
        courseRepository.getCoursesByTeacher(teacherId)

    fun insertCourse(course: CourseEntity) = viewModelScope.launch {
        courseRepository.insertCourse(course)
    }

    fun deleteCourse(course: CourseEntity) = viewModelScope.launch {
        courseRepository.deleteCourse(course)
    }

    suspend fun getCourseById(id: Int): CourseEntity? = courseRepository.getCourseById(id)
}

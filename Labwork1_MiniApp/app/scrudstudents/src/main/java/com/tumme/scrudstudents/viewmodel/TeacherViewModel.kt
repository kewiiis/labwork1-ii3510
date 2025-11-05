package com.tumme.scrudstudents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumme.scrudstudents.data.local.model.TeacherEntity
import com.tumme.scrudstudents.data.repository.AuthRepository
import com.tumme.scrudstudents.data.repository.TeacherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Teacher operations.
 * 
 * Manages teacher-specific data and business logic.
 * 
 * Data Flow: DB → Repository → ViewModel (StateFlow) → UI (collectAsState)
 */
@HiltViewModel
class TeacherViewModel @Inject constructor(
    private val teacherRepository: TeacherRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentTeacher = MutableStateFlow<TeacherEntity?>(null)
    val currentTeacher: StateFlow<TeacherEntity?> = _currentTeacher.asStateFlow()

    /**
     * Loads the current logged-in teacher.
     */
    fun loadCurrentTeacher() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId.value ?: return@launch
            val teacher = teacherRepository.getTeacherByUserId(userId)
            _currentTeacher.value = teacher
        }
    }

    /**
     * Gets the current teacher ID.
     */
    suspend fun getCurrentTeacherId(): Int? {
        val userId = authRepository.currentUserId.value ?: return null
        return teacherRepository.getTeacherByUserId(userId)?.idTeacher
    }
}

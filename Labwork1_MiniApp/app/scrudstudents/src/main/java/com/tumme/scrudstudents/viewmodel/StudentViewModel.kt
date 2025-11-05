package com.tumme.scrudstudents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumme.scrudstudents.data.local.model.StudentEntity
import com.tumme.scrudstudents.data.repository.AuthRepository
import com.tumme.scrudstudents.data.repository.StudentRepository
import com.tumme.scrudstudents.data.repository.SubscribeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Student operations.
 * 
 * Manages student-specific data and business logic.
 * 
 * Data Flow: DB → Repository → ViewModel (StateFlow) → UI (collectAsState)
 */
@HiltViewModel
class StudentViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val subscribeRepository: SubscribeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentStudent = MutableStateFlow<StudentEntity?>(null)
    val currentStudent: StateFlow<StudentEntity?> = _currentStudent.asStateFlow()

    val allStudents: StateFlow<List<StudentEntity>> = studentRepository.getAllStudents()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * Loads the current logged-in student.
     */
    fun loadCurrentStudent() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId.value ?: return@launch
            val student = studentRepository.getStudentByUserId(userId)
            _currentStudent.value = student
        }
    }

    /**
     * Gets the current student ID.
     */
    suspend fun getCurrentStudentId(): Int? {
        val userId = authRepository.currentUserId.value ?: return null
        return studentRepository.getStudentByUserId(userId)?.idStudent
    }
}
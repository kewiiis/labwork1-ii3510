package com.tumme.scrudstudents.ui.navigation

/**
 * Navigation routes for the application.
 */
object Routes {
    // Auth
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Student
    const val STUDENT_HOME = "student_home"
    const val STUDENT_COURSES = "student_courses"
    const val STUDENT_SUBSCRIBES = "student_subscribes"
    const val STUDENT_GRADES = "student_grades"
    const val STUDENT_FINAL_GRADE = "student_final_grade"

    // Teacher
    const val TEACHER_HOME = "teacher_home"
    const val TEACHER_COURSES = "teacher_courses"
    const val TEACHER_STUDENTS = "teacher_students/{courseId}"
    const val TEACHER_GRADE_ENTRY = "teacher_grade_entry/{courseId}/{studentId}"

    // Course CRUD
    const val COURSE_FORM = "course_form"
    const val COURSE_DETAIL = "course_detail/{courseId}"

    // Subscribe CRUD
    const val SUBSCRIBE_FORM = "subscribe_form"
}

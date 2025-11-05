package com.tumme.scrudstudents.data.local.model

/**
 * Enum representing user roles in the application.
 */
enum class Role(val value: String) {
    STUDENT("STUDENT"),
    TEACHER("TEACHER");

    companion object {
        fun from(value: String) = entries.firstOrNull { it.value == value } ?: STUDENT
    }
}

package com.tumme.scrudstudents.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tumme.scrudstudents.data.local.dao.CourseDao
import com.tumme.scrudstudents.data.local.dao.StudentDao
import com.tumme.scrudstudents.data.local.dao.SubscribeDao
import com.tumme.scrudstudents.data.local.dao.TeacherDao
import com.tumme.scrudstudents.data.local.dao.UserDao
import com.tumme.scrudstudents.data.local.model.StudentEntity
import com.tumme.scrudstudents.data.local.model.CourseEntity
import com.tumme.scrudstudents.data.local.model.SubscribeEntity
import com.tumme.scrudstudents.data.local.model.UserEntity
import com.tumme.scrudstudents.data.local.model.TeacherEntity

@Database(
    entities = [
        UserEntity::class,
        StudentEntity::class,
        TeacherEntity::class,
        CourseEntity::class,
        SubscribeEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao
    abstract fun courseDao(): CourseDao
    abstract fun subscribeDao(): SubscribeDao
}
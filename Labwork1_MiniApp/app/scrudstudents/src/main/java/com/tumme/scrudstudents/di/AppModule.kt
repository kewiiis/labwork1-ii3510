package com.tumme.scrudstudents.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tumme.scrudstudents.data.local.AppDatabase
import com.tumme.scrudstudents.data.local.dao.CourseDao
import com.tumme.scrudstudents.data.local.dao.StudentDao
import com.tumme.scrudstudents.data.local.dao.SubscribeDao
import com.tumme.scrudstudents.data.local.dao.TeacherDao
import com.tumme.scrudstudents.data.local.dao.UserDao
import com.tumme.scrudstudents.data.repository.AuthRepository
import com.tumme.scrudstudents.data.repository.CourseRepository
import com.tumme.scrudstudents.data.repository.StudentRepository
import com.tumme.scrudstudents.data.repository.SubscribeRepository
import com.tumme.scrudstudents.data.repository.TeacherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * Module Hilt pour l'injection de dépendances.
 * Configure la base de données, les DAOs et les Repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "scrud-db")
            .fallbackToDestructiveMigration() // For development - use migrations in production
            .build()

    @Provides 
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides 
    fun provideStudentDao(db: AppDatabase): StudentDao = db.studentDao()
    
    @Provides 
    fun provideTeacherDao(db: AppDatabase): TeacherDao = db.teacherDao()
    
    @Provides 
    fun provideCourseDao(db: AppDatabase): CourseDao = db.courseDao()
    
    @Provides 
    fun provideSubscribeDao(db: AppDatabase): SubscribeDao = db.subscribeDao()

    @Provides
    @Singleton
    fun provideAuthRepository(
        userDao: UserDao,
        studentDao: StudentDao,
        teacherDao: TeacherDao,
        @ApplicationContext context: Context
    ): AuthRepository = AuthRepository(userDao, studentDao, teacherDao, context)

    @Provides
    @Singleton
    fun provideStudentRepository(studentDao: StudentDao): StudentRepository =
        StudentRepository(studentDao)

    @Provides
    @Singleton
    fun provideTeacherRepository(teacherDao: TeacherDao): TeacherRepository =
        TeacherRepository(teacherDao)

    @Provides
    @Singleton
    fun provideCourseRepository(courseDao: CourseDao): CourseRepository =
        CourseRepository(courseDao)

    @Provides
    @Singleton
    fun provideSubscribeRepository(
        subscribeDao: SubscribeDao,
        courseDao: CourseDao
    ): SubscribeRepository = SubscribeRepository(subscribeDao, courseDao)

    // Legacy SCRUDRepository for existing ViewModels
    @Provides
    @Singleton
    fun provideSCRUDRepository(
        studentDao: StudentDao,
        courseDao: CourseDao,
        subscribeDao: SubscribeDao
    ): com.tumme.scrudstudents.data.repository.SCRUDRepository =
        com.tumme.scrudstudents.data.repository.SCRUDRepository(studentDao, courseDao, subscribeDao)
}
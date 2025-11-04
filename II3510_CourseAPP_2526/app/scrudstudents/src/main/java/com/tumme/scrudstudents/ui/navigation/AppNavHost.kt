package com.tumme.scrudstudents.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tumme.scrudstudents.ui.auth.LoginScreen
import com.tumme.scrudstudents.ui.auth.RegisterScreen
import com.tumme.scrudstudents.ui.auth.SplashScreen
import com.tumme.scrudstudents.ui.student.*
import com.tumme.scrudstudents.ui.teacher.*
import com.tumme.scrudstudents.viewmodel.AuthViewModel

/**
 * Main navigation host for the application.
 * 
 * Implements dynamic navigation based on user role (Student/Teacher).
 * Routes are defined in Routes.kt.
 */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    // Global navigation handler for logout - prevents multiple navigation
    var hasNavigatedToLogin by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    
    LaunchedEffect(authState) {
        if (authState is com.tumme.scrudstudents.viewmodel.AuthState.LoggedOut) {
            // Only navigate if we're not already on login or splash and haven't navigated yet
            val currentRoute = navBackStackEntry?.destination?.route
            if (!hasNavigatedToLogin && currentRoute != Routes.LOGIN && currentRoute != Routes.SPLASH) {
                hasNavigatedToLogin = true
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.SPLASH) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        } else if (authState is com.tumme.scrudstudents.viewmodel.AuthState.LoggedIn) {
            // Reset flag when logged in
            hasNavigatedToLogin = false
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(Routes.LOGIN) {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController, authViewModel = authViewModel)
        }

        // Student routes
        composable(Routes.STUDENT_HOME) {
            StudentHomeScreen(navController = navController)
        }
        composable(Routes.STUDENT_COURSES) {
            StudentCourseListScreen(navController = navController)
        }
        composable(Routes.STUDENT_SUBSCRIBES) {
            StudentSubscribeScreen(navController = navController)
        }
        composable(Routes.STUDENT_GRADES) {
            StudentGradesScreen(navController = navController)
        }
        composable(Routes.STUDENT_FINAL_GRADE) {
            StudentFinalGradeScreen(navController = navController)
        }

        // Teacher routes
        composable(Routes.TEACHER_HOME) {
            TeacherHomeScreen(navController = navController)
        }
        composable(Routes.TEACHER_COURSES) {
            TeacherCourseListScreen(navController = navController)
        }
        composable(
            Routes.TEACHER_STUDENTS,
            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId") ?: 0
            TeacherStudentListScreen(courseId = courseId, navController = navController)
        }
        composable(
            Routes.TEACHER_GRADE_ENTRY,
            arguments = listOf(
                navArgument("courseId") { type = NavType.IntType },
                navArgument("studentId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId") ?: 0
            val studentId = backStackEntry.arguments?.getInt("studentId") ?: 0
            TeacherGradeEntryScreen(
                courseId = courseId,
                studentId = studentId,
                navController = navController
            )
        }

        // Course CRUD
        composable(Routes.COURSE_FORM) {
            com.tumme.scrudstudents.ui.course.CourseFormScreen(
                onSaved = { navController.popBackStack() }
            )
        }

        // Subscribe CRUD
        composable(Routes.SUBSCRIBE_FORM) {
            com.tumme.scrudstudents.ui.subscribe.SubscribeFormScreen(
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
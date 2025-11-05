package com.tumme.scrudstudents

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for dependency injection setup.
 * Hilt uses this class to generate the dependency injection components.
 */
@HiltAndroidApp
class SCRUDApplication : Application()

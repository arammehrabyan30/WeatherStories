package com.aram.mehrabyan.weatherstories

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Weather Stories App
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection
 */
@HiltAndroidApp
class WeatherStoriesApplication : Application()
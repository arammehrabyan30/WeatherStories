plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    kotlin("kapt")
}

android {
    namespace = "com.aram.mehrabyan.weatherstories"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aram.mehrabyan.weatherstories"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // API Keys should be added to gradle.properties file
        // OPENWEATHER_API_KEY=your_api_key_here
        // UNSPLASH_API_KEY=your_api_key_here
        val openWeatherApiKey: String = project.findProperty("OPENWEATHER_API_KEY") as String? ?: ""
        val unsplashApiKey: String = project.findProperty("UNSPLASH_API_KEY") as String? ?: ""

        buildConfigField("String", "OPENWEATHER_API_KEY", "\"$openWeatherApiKey\"")
        buildConfigField("String", "UNSPLASH_API_KEY", "\"$unsplashApiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt for Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Retrofit for Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Gson
    implementation(libs.gson)

    // Coil for Image Loading
    implementation(libs.coil.compose)

    // Location Services
    implementation(libs.play.services.location)

    // ViewModel Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core.v2)
    androidTestImplementation(platform(libs.androidx.compose.bom.android.test))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)  // <- Activamos KSP
    alias(libs.plugins.hilt) // <- Activamos Hilt
}

android {
    namespace = "com.example.unitime"
    compileSdk = 36 // O el que estés usando (34 o 35 es más estable hoy en día)

    defaultConfig {
        applicationId = "com.example.unitime"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    // ── Compose ──────────────────────────────────────────
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.viewbinding)
    // ── Lifecycle & ViewModel ────────────────────────────
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // ── Navigation ───────────────────────────────────────
    implementation(libs.androidx.navigation.compose)
    // ── Room (Base de datos local) ───────────────────────
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler) // ¡Usando KSP en vez de kapt!
    // ── WorkManager ──────────────────────────────────────
    implementation(libs.androidx.work.runtime.ktx)
    // ── DataStore (preferencias) ─────────────────────────
    implementation(libs.androidx.datastore.preferences)
    // ── Retrofit + Gson (API del clima) ──────────────────
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)
    // ── Coroutines ───────────────────────────────────────
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.coil.compose)
    // ── Hilt (Inyección de dependencias) ─────────────────
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)          // ¡Usando KSP en vez de kapt!
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
}
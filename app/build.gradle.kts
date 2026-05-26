plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.appdistribution)
}

import java.util.Properties

android {
    namespace = "com.turistgo.app"
    compileSdk = 35

    val env = Properties().apply {
        val envFile = rootProject.file(".env")
        if (envFile.exists()) {
            envFile.inputStream().use { load(it) }
        }
    }

    defaultConfig {
        applicationId = "com.turistgo.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        buildConfigField("String", "GROQ_API_KEY", "\"${env.getProperty("GROQ_API_KEY") ?: ""}\"")
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"${env.getProperty("GOOGLE_WEB_CLIENT_ID") ?: ""}\"")
        buildConfigField("String", "CLOUDINARY_CLOUD_NAME", "\"${env.getProperty("CLOUDINARY_CLOUD_NAME") ?: "TuristGo"}\"")
        buildConfigField("String", "CLOUDINARY_API_KEY", "\"${env.getProperty("CLOUDINARY_API_KEY") ?: ""}\"")
        buildConfigField("String", "CLOUDINARY_API_SECRET", "\"${env.getProperty("CLOUDINARY_API_SECRET") ?: ""}\"")
        buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"${env.getProperty("GOOGLE_MAPS_API_KEY") ?: ""}\"")
        buildConfigField("String", "GEMINI_API_KEY", "\"${env.getProperty("GEMINI_API_KEY") ?: ""}\"")
        buildConfigField("String", "ADMIN_EMAIL", "\"${env.getProperty("ADMIN_EMAIL") ?: ""}\"")
        buildConfigField("String", "ADMIN_PASSWORD", "\"${env.getProperty("ADMIN_PASSWORD") ?: ""}\"")
        
        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = env.getProperty("GOOGLE_MAPS_API_KEY") ?: ""
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            firebaseAppDistribution {
                artifactType = "APK"
                testers = "santiarco2611@gmail.com, juand.jdg3@gmail.com, elianay.hernandezo@uqvirtual.edu.co, santiago.arbelaezc@uqvirtual.edu.co"
                releaseNotes = "TuristGo v1.0.3 — Nueva pantalla de Conexiones (Seguidores y Siguiendo) interactiva desde el Perfil."
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.serialization.json)
    
    // Hilt & Navigation
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // DataStore & Cloudinary
    implementation(libs.data.store)
    implementation(libs.cloudinary.android)
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)

    // Google Sign-In & Credential Manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Networking & Groq AI
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.retrofit.serialization.converter)
    implementation(libs.generative.ai)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

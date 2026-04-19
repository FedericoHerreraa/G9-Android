plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.desarrollo_apps_1"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.desarrollo_apps_1"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    annotationProcessor("com.google.dagger:hilt-compiler:2.51.1")

    // ViewModel y LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.activity)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.glide)
    implementation(libs.okhttp)
    implementation(libs.hilt.android)
    annotationProcessor(libs.hilt.compiler)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.security.crypto)
}
plugins {
    //alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.android.application")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.firebase"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.firebase"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
    implementation("com.google.firebase:firebase-storage-ktx:21.0.0")
    implementation("com.google.firebase:firebase-messaging-ktx:23.2.1")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.gms:google-services:4.4.2")
    implementation("com.firebaseui:firebase-ui-storage:8.0.2")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("androidx.multidex:multidex:2.0.1")
}
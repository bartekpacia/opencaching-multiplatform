plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.compose")
}

android {
    namespace = "tech.pacia.opencaching.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "tech.pacia.opencaching.android"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.compose.ui:ui:1.5.1")
    implementation("androidx.compose.ui:ui-tooling:1.5.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.1")
    implementation("androidx.compose.foundation:foundation:1.5.1")
    implementation("androidx.compose.material:material:1.5.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
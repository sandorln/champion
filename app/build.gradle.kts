import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.service)
    alias(libs.plugins.firebase.crashlytices)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    kotlin("kapt")
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    namespace = "com.sandorln.champion"
    compileSdk = libs.versions.complieSdk.get().toInt()

    defaultConfig {
        applicationId = "com.sandorln.champion"
        minSdk = libs.versions.minSdk.get().toInt()

        targetSdk = libs.versions.complieSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            manifestPlaceholders["appName"] = "@string/app_name"
        }
        debug {
            applicationIdSuffix = ".dev"
            manifestPlaceholders["appName"] = "@string/app_name_dev"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    dataBinding {
        enable = true
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    implementation(libs.android.licenses)

    implementation(project(":core:design"))
    implementation(project(":feature:home"))
}


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    kotlin("kapt")
}

android {
    namespace = "com.sandorln.champion"
    compileSdk = libs.versions.wearComplieSdk.get().toInt()

    defaultConfig {
        applicationId = "com.sandorln.champion"
        minSdk = libs.versions.wearMinSdk.get().toInt()

        targetSdk = libs.versions.wearComplieSdk.get().toInt()
        versionCode = libs.versions.wearVersionCode.get().toInt()
        versionName = libs.versions.wearVersionName.get().toString()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    dataBinding {
        enable = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.wear)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.play.services.wearable)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    api(libs.coroutine.android)

    implementation(libs.glide.xml)
    implementation(libs.horologist.tiles)

    implementation(project(":core:domain"))
}
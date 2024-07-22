import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.sandorln.design"
    compileSdk = libs.versions.complieSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
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
    buildFeatures {
        compose = true
    }
    dataBinding {
        enable = true
    }
}

dependencies {
    implementation(libs.androidx.card)
    implementation(libs.androidx.core.ktx)
    api(libs.androidx.activity.compose)
    api(libs.androidx.navigation.compose)
    api(libs.androidx.navigation.hilt)

    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.ui.preview)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material)
    api(libs.androidx.compose.constraintlayout)

    api(libs.holix.bottomsheetdialog.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    api(libs.glide)
    api(libs.androidx.media.exoplayer)
    api(libs.androidx.media.ui)
}
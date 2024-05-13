plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.play.services.wearable)

}
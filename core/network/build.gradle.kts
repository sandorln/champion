plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.sandorln.network"
    compileSdk = libs.versions.complieSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        buildConfigField("String", "BASE_URL", "\"http://ddragon.leagueoflegends.com\"")
        buildConfigField("String", "BASE_LANGUAGE", "\"ko_KR\"")
    }

    buildFeatures {
        buildConfig = true
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
    implementation(libs.appcompat)
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.androidx.junit)

    implementation(libs.hilt.android)
    implementation(project(":app"))
    ksp(libs.hilt.compiler)

    implementation(libs.coroutine.android)

    implementation(libs.ktor.android)
    implementation(libs.ktor.cio)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.content.negotiation)
    implementation(libs.ktor.logging)

}
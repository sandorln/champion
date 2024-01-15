plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.google.service) apply false
    alias(libs.plugins.serialization) apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
}
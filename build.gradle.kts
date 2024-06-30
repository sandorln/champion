plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.google.service) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.firebase.crashlytices) apply false
    alias(libs.plugins.ksp) apply false
}

buildscript {
    dependencies {
        classpath(libs.android.licenses.plugin)
    }
}
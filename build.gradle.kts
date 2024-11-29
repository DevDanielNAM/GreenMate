// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.0.2")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.22")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.48")
    }
}

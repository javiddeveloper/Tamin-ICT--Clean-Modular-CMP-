import java.io.File // Required for File("...").toURI()

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    // Versions are now managed by the libs.versions.toml catalog
    // val agp_version = "8.13.0" // From catalog: libs.versions.androidGradlePlugin
    // val kotlin_version = "2.2.20" // From catalog: libs.versions.kotlin

    repositories {
        maven { url = uri("https://maven.myket.ir/") }
//        google()
//        mavenCentral()
//        maven { url = uri("https://jitpack.io") }
//        maven { url = uri("https://www.jitpack.io") }
//        maven { url = uri("https://jcenter.bintray.com") }
//        gradlePluginPortal()
//        mavenLocal()
       /* maven {
            url = uri("https://nexus.tamin.ir/content/groups/public")
        }*/
//        maven {
//            url = File("C:/sdk/extras/android/m2repository").toURI()
//        }
    }


}
plugins {
    alias(libs.plugins.android.application)  apply false
    alias(libs.plugins.kotlin.android)      apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.kotlin.parcelize)     apply false
    alias(libs.plugins.androidx.navigation.safeargs)  apply false
}



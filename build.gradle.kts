plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.androidx.room) apply false
    alias(libs.plugins.aboutlibraries) apply false
    alias(libs.plugins.kotlin.android) apply false
}

allprojects {
    // Link it to check task if it exists (usually in subprojects or root with specific plugins)
    tasks.matching { it.name == "check" }.all {
        // dependsOn(":checkDnNaming")
    }
}

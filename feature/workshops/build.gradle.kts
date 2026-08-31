plugins {
    alias(libs.plugins.tamin.kmp.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tamin.taminhamrah.feature.workshops"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.navigation.compose)
            // The forms attach photographs; same picker the other upload flows use.
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialog.compose)
        }
    }
}

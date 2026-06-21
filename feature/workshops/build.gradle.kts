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
        }
    }
}

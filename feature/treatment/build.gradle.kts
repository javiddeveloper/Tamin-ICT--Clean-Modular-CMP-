plugins {
    alias(libs.plugins.tamin.kmp.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tamin.taminhamrah.feature.treatment"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
    }
}

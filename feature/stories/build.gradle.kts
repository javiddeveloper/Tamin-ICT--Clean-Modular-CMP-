plugins {
    alias(libs.plugins.tamin.kmp.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tamin.taminhamrah.feature.stories"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.navigation.compose)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }

        androidMain.dependencies {
            // Story slides that carry a clip play through the platform's own stack.
            implementation(libs.media3.exoplayer)
            implementation(libs.media3.ui)
        }
    }
}

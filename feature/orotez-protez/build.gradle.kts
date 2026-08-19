plugins {
    alias(libs.plugins.tamin.kmp.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tamin.taminhamrah.feature.orotezprotez"

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.navigation.compose)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialog.compose)
        }

        androidMain.dependencies {
            // Camera permission launcher (rememberLauncherForActivityResult).
            implementation(libs.androidx.activity.compose)
        }

        androidUnitTest.dependencies {
            implementation(libs.robolectric)
        }
    }
}

plugins {
    alias(libs.plugins.tamin.kmp.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tamin.taminhamrah.feature.pensionSurvivor"
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            languageSettings.optIn("kotlin.io.encoding.ExperimentalEncodingApi")
        }
        commonMain.dependencies {
            implementation(libs.androidx.navigation.compose)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialog.compose)
            implementation(libs.kotlinx.datetime)
            // Needed for ByteReadChannel when opening the bundled rules PDF in TaminPdfViewer
            implementation(libs.ktor.client.core)
        }
    }
}

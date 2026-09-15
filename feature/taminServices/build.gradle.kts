plugins {
    alias(libs.plugins.tamin.kmp.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tamin.taminhamrah.feature.taminServices"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.navigation.compose)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.filekit.core)
            implementation(libs.filekit.compose)
            implementation(libs.filekit.dialog.compose)
            // PdfDownloadPR (core-ui) exposes io.ktor.utils.io.ByteReadChannel publicly — needed on
            // this module's own classpath to reference it (e.g. PaymentSheetViewModel), same as
            // feature/contractsAndPaymentAffair's identical dependency for the same reason.
            implementation(libs.ktor.client.core)
        }

        androidMain.dependencies {
            // Camera permission launcher (rememberLauncherForActivityResult).
            implementation(libs.androidx.activity.compose)
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
}
plugins {
    id("TaminHamrah.kmp.library")
    id("TaminHamrah.kmp.compose")
    id("TaminHamrah.naming.convention")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:core-domain"))
            implementation(project(":core:core-plugin"))
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.coil.network.okhttp)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.tamin.taminhamrah.core.ui"
}

compose.resources {
    packageOfResClass = "taminx.core.core_ui"
    publicResClass = true
}

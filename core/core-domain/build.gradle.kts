dependencies {
    implementation(libs.androidx.core.ktx)
}
plugins {
    id("TaminHamrah.kmp.library")
    id("TaminHamrah.naming.convention")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
        }

        androidMain.dependencies {
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.ktor.client.mock)
        }
    }
}

android {
    namespace = "com.tamin.taminhamrah.core.domain"
    buildFeatures {
        buildConfig = true
    }
}

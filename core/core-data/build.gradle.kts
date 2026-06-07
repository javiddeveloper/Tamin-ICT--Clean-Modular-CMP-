plugins {
    id("TaminHamrah.kmp.library")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:core-domain"))
            implementation(project(":core:core-database"))
            implementation(project(":core:core-network"))
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

android {
    namespace = "com.tamin.taminhamrah.core.data"
}

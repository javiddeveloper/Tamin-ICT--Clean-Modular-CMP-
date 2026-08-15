dependencies {
    implementation(libs.androidx.core.ktx)
}
plugins {
    alias(libs.plugins.tamin.kmp.library)
    alias(libs.plugins.tamin.kmp.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.skie)
}

kotlin {
    listOf(
//        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            export(libs.androidx.lifecycle.viewmodel)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core:core-domain"))
            api(project(":core:core-network"))
            api(project(":core:core-database"))
            api(project(":core:core-data"))
            api(project(":core:core-datastore"))
            api(project(":core:core-plugin"))
            api(project(":core:core-ui"))
            api(project(":feature:profile"))
            api(project(":feature:treatment"))
            api(project(":feature:pensioner"))
            api(project(":feature:cartable"))
            api(project(":feature:history"))
            api(project(":feature:contracts"))
            api(project(":feature:workshops"))
            api(project(":feature:my-inbox"))
            api(project(":feature:studentInsuranceContract"))
            api(project(":feature:agent"))
            api(project(":feature:taminServices"))
            api(project(":feature:healthProfile"))
            api(project(":feature:change-mobile"))
            api(project(":feature:security"))
            api(project(":feature:orotez-protez"))
//            api(project(":feature:feature-settings"))
            api(libs.androidx.lifecycle.viewmodel)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.koin.core.viewmodel)

            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
            implementation(libs.jetbrains.lifecycle.runtime.compose)

            implementation(libs.androidx.navigation.runtime)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.navigation3.runtime)

            implementation(libs.jetbrains.compose.material3.adaptive)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            implementation(libs.paging.common)

            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.aboutlibraries.compose.m3)
            implementation(libs.chrisbanes.haze)
        }

        androidMain.dependencies {
            implementation(libs.androidx.browser)
            implementation(libs.koin.android)
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.androidx.lifecycle.viewmodel.navigation3)
            implementation(libs.material3.adaptive.navigation.suite)
            implementation(libs.paging.compose)

            implementation(libs.zxing.core)
            implementation(libs.mlkit.barcode)
            implementation(libs.androidx.camera.core)
            implementation(libs.androidx.camera.camera2)
            implementation(libs.androidx.camera.lifecycle)
            implementation(libs.androidx.camera.view)

            implementation(libs.media3.exoplayer)
            implementation(libs.media3.ui)
            implementation(libs.okhttp)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.chucker.debug)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
    }
}

skie {
    features {
        enableSwiftUIObservingPreview = true
    }
}

android {
    namespace = "com.tamin.taminhamrah.shared"
    buildFeatures {
        buildConfig = true
    }
}

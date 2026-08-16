import java.util.Properties
import java.io.FileInputStream

plugins {
    id("TaminHamrah.android.application")
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.aboutlibraries)
}

// Load API keys from key.properties
val apiKeyPropertiesFile = rootProject.file("key.properties")
val apiKeyProperties = Properties().apply {
    if (apiKeyPropertiesFile.exists()) {
        load(FileInputStream(apiKeyPropertiesFile))
    }
}

fun getApiKey(key: String): String {
    return apiKeyProperties.getProperty(key) ?: System.getenv(key) ?: ""
}

android {
    namespace = "com.tamin.taminhamrah"

    defaultConfig {
        applicationId = "com.tamin.taminhamrah"
        versionCode = 7
        versionName = "2.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Default Build Config Fields
        buildConfigField("String", "BASE_URL_MEDICAL", "\"https://medical.tamin.ir\"")
        buildConfigField("String", "BASE_URL", "\"https://eservices.tamin.ir/api/\"")
        buildConfigField("String", "BASE_URL_ACCOUNT", "\"https://account.tamin.ir/auth/\"")
        buildConfigField("String", "BASE_URL_OV", "\"https://ov.tamin.ir/api/\"")
        buildConfigField("String", "AI_BASE_URL", "\"https://sw.tamin.ir/\"")
        buildConfigField("String", "AI_BASE_IP", "\"http://172.16.15.54:9001/\"")
        buildConfigField("String", "CLIENT_ID", "\"${getApiKey("OPERATIONAL_API_KEY")}\"")
        buildConfigField("String", "HEALTH_PROFILE", "\"http://172.16.14.115:5700/api/\"")
        // Feature Gate
        buildConfigField("boolean", "FEATURE_SIMILARITY_SEARCH", "false")
    }

    // Release signing config from environment variables (CI)
    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("RELEASE_KEYSTORE")
            val keystorePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
            val keyAliasName = System.getenv("RELEASE_KEY_ALIAS")
            val keyPasswordValue = System.getenv("RELEASE_KEY_PASSWORD")

            if (keystorePath != null && file(keystorePath).exists()) {
                storeFile = file(keystorePath)
                storePassword = keystorePassword
                keyAlias = keyAliasName
                keyPassword = keyPasswordValue
            }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    flavorDimensions.add("taminHamrah")
    productFlavors {
        create("direct") {
            dimension = "taminHamrah"
        }
        create("caffeBazaar") {
            dimension = "taminHamrah"
        }
        create("myket") {
            dimension = "taminHamrah"
        }
        create("flavorTest") {
            dimension = "taminHamrah"
            buildConfigField("String", "BASE_URL", "\"https://eservices.test.org:9090/api/\"")
            buildConfigField("String", "BASE_URL_MEDICAL", "\"http://medical.test.org:9087\"")
            buildConfigField("String", "BASE_URL_ACCOUNT", "\"https://account-test.tamin.ir:9090/auth/\"")
            buildConfigField("String", "CLIENT_ID", "\"${getApiKey("TEST_API_KEY")}\"")
        }
        create("reporter") {
            dimension = "taminHamrah"
            applicationId = "com.tamin.taminhamrahreporter"
            versionCode = 1
            versionName = "1.0.0"
            versionNameSuffix = "-گزارش گیری"
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = false
            val releaseKeystore = System.getenv("RELEASE_KEYSTORE")
            if (releaseKeystore != null && file(releaseKeystore).exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    applicationVariants.all {
        val variant = this
        variant.outputs.all {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            if (variant.buildType.name == "release") {
                output.outputFileName =
                    "Tamin_ICT_${variant.versionCode}_${variant.versionName}-(${variant.flavorName}).apk"
            }
        }
    }

}

dependencies {
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    androidTestImplementation(libs.kotlin.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)

    implementation(project(":shared"))
    implementation(project(":core:core-ui"))
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.biometric)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.ktor.client.core)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.paging.compose)
    implementation(libs.androidx.navigation.runtime)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.material3.adaptive)
    implementation(libs.material3.adaptive.navigation.suite)
    implementation(libs.aboutlibraries.compose.m3)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    implementation(libs.zxing.core)
    implementation(libs.mlkit.barcode)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
}

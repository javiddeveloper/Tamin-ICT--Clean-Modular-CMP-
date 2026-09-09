import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.tamin.kmp.feature)
    alias(libs.plugins.kotlin.serialization)
}

// Same key.properties / env-var pattern androidApp/build.gradle.kts uses for CLIENT_ID, so the
// real back-to-back credentials never have to be typed into a Kotlin source file.
val keyPropertiesFile = rootProject.file("key.properties")
val keyProperties = Properties().apply {
    if (keyPropertiesFile.exists()) {
        load(FileInputStream(keyPropertiesFile))
    }
}

fun getSecret(key: String, fallback: String): String =
    keyProperties.getProperty(key) ?: System.getenv(key) ?: fallback

android {
    namespace = "com.tamin.taminhamrah.feature.developerOptions"

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            // The tester's default back-to-back client, pre-filled so Debug Login works without
            // any local setup. Deliberately absent from the release build type below: unlike the
            // `AppConfig.isDebug` gate on the screens that read these, an empty BuildConfig field
            // keeps the secret text itself out of the release APK's compiled classes.
            buildConfigField(
                "String",
                "BACK_TO_BACK_CLIENT_ID",
                "\"${getSecret("BACK_TO_BACK_CLIENT_ID", "442e832b206822656b5f816f6a630383")}\"",
            )
            buildConfigField(
                "String",
                "BACK_TO_BACK_CLIENT_SECRET",
                "\"${getSecret("BACK_TO_BACK_CLIENT_SECRET", "04136b343832526014771644295c43636e4323051a805f4e1c3b192589820373")}\"",
            )
        }
        release {
            buildConfigField("String", "BACK_TO_BACK_CLIENT_ID", "\"\"")
            buildConfigField("String", "BACK_TO_BACK_CLIENT_SECRET", "\"\"")
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.navigation.compose)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.collections.immutable)
            // Both for the token screen: reading a JWT's `exp` claim and saying how long is left.
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

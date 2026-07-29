import java.util.Properties
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.androidx.navigation.safeargs)
    alias(libs.plugins.sentry)
}

val apiKeyPropertiesFile = rootProject.file("key.properties")
val apiKeyProperties = Properties()
apiKeyProperties.load(FileInputStream(apiKeyPropertiesFile))

android {
    namespace = "com.tamin.taminhamrah"

    compileSdk = libs.versions.compileSdk.get().toInt()


    room {
        schemaDirectory("$projectDir/schemas")
    }

    signingConfigs {
        create("release") {
            keyPassword = "honari5962saeed"
            keyAlias = "saeedhonari"
            storePassword = "saeed42364951370hj"
            storeFile = file("keystore/keystore_my_tamin_ap.jks")
        }
    }

    defaultConfig {
        applicationId = "com.tamin.taminhamrah"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 53
        versionName = "1.12.3"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL_MEDICAL", "\"https://medical.tamin.ir\"")
        buildConfigField("String", "BASE_URL", "\"https://eservices.tamin.ir/api/\"")
        buildConfigField("String", "BASE_URL_ACCOUNT", "\"https://account.tamin.ir/auth/\"")
        buildConfigField("String", "BASE_URL_OV", "\"https://ov.tamin.ir/api/\"")
        buildConfigField("String", "AI_BASE_URL", "\"https://sw.tamin.ir/\"")
//        buildConfigField("String", "AI_BASE_URL", "\"https://ai.tamin.ir/search_service\"")
        buildConfigField("String", "AI_BASE_IP", "\"http://172.16.15.54:9001/\"")
        buildConfigField("String", "CLIENT_ID", apiKeyProperties["OPERATIONAL_API_KEY"].toString())
        buildConfigField("String", "SENTRY_DSN", apiKeyProperties["SENTRY_DSN"].toString())
        buildConfigField(
            "String",
            "SENTRY_CONFIG_URL",
            apiKeyProperties["SENTRY_CONFIG_URL"].toString()
        )
        buildConfigField(
            "String",
            "SENTRY_AUTH_HEADER",
            apiKeyProperties["SENTRY_AUTH_HEADER"].toString()
        )


        vectorDrawables.useSupportLibrary = true
        signingConfig = signingConfigs.getByName("release")

    }

    flavorDimensions.add("taminHamrah")

    productFlavors {
        create("direct") {
            applicationId = "com.tamin.taminhamrah"
            dimension = "taminHamrah"
        }
        create("caffeBazaar") {
            applicationId = "com.tamin.taminhamrah"
            dimension = "taminHamrah"
        }
        create("myket") {
            applicationId = "com.tamin.taminhamrah"
            dimension = "taminHamrah"
        }
        create("flavorTest") {
            buildConfigField("String", "BASE_URL", "\"https://eservices.test.org:9090/api/\"")
            buildConfigField("String", "BASE_URL_MEDICAL", "\"http://medical.test.org:9087\"")
            buildConfigField(
                "String",
                "BASE_URL_ACCOUNT",
                "\"https://account-test.tamin.ir:9090/auth/\""
            )
            buildConfigField("String", "CLIENT_ID", apiKeyProperties["TEST_API_KEY"].toString())
            applicationId = "com.tamin.taminhamrah"
            dimension = "taminHamrah"
        }

        create("reporter") {
            dimension = "taminHamrah"
            applicationId = "com.tamin.taminhamrahreporter"
            versionCode = 1
            versionName = "1.0.0"
            versionNameSuffix = "-گزارش گیری"
        }


        sourceSets {
            getByName("main") {
                aidl.srcDirs("src/main/aidl")
            }
            getByName("reporter") {
                java.srcDirs("src/reporter/java")
                res.srcDirs("src/reporter/res")
            }
        }

        buildTypes {
            getByName("release") {
                isDebuggable = false
                isMinifyEnabled = true
                isShrinkResources = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }

        applicationVariants.all {
            outputs.forEach { output ->
                val outputImpl = output as com.android.build.gradle.internal.api.BaseVariantOutputImpl
                val versionNum = versionName.replace(".", "")
                var flavorNameStr = name
                flavorNameStr = if (flavorNameStr.contains("Debug", ignoreCase = true)) {
                    flavorNameStr.replace("Debug", "(debug)", ignoreCase = true)
                } else {
                    flavorNameStr.replace("Release", "(release)", ignoreCase = true)
                }
                val ext = outputImpl.outputFile.extension
                outputImpl.outputFileName =
                    "Tamin_ICT Code(${versionCode}) Name(${versionNum}) ${flavorNameStr}.$ext"
            }
        }


        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }

        buildFeatures {
            dataBinding = true
            viewBinding = true
            aidl = true
            buildConfig = true
        }

        lint {
            abortOnError = false
            checkReleaseBuilds = true
        }
    }

    dependencies {
        implementation(libs.acra.toast)
        implementation(libs.acra.http)
        implementation(libs.intuit.sdp)
        implementation(libs.intuit.ssp)
        implementation(libs.prdownloader)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.google.material)
        implementation(libs.androidx.constraintlayout)
        implementation(libs.androidx.exifinterface)
        implementation(libs.androidx.lifecycle.viewmodel.ktx)
        implementation(libs.androidx.documentfile)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.test.ext.junit)
        androidTestImplementation(libs.androidx.test.espresso.core)
        implementation(libs.androidx.recyclerview)
        implementation(libs.coil.kt)
        implementation(libs.coil.kt.svg)
        implementation(libs.materialStepperView)
        implementation(libs.retrofit.core)
        implementation(libs.retrofit.kotlinx.serialization.converter)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.okhttp.logging.interceptor)
        implementation(libs.okhttp.core)
        implementation(libs.retrofit.converter.gson)
        implementation(libs.retrofit.converter.scalars)
        implementation(libs.androidx.lifecycle.livedata.ktx)
        implementation(libs.androidx.lifecycle.common.java8)
        implementation(libs.androidx.lifecycle.extensions)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.coroutines.android)
        implementation(libs.hilt.android)
        ksp(libs.hilt.compiler)
        implementation(libs.androidx.room.runtime)
        implementation(libs.androidx.room.ktx)
        implementation(libs.androidx.room.paging)
        implementation(libs.androidx.swiperefreshlayout) // Added this line
        ksp(libs.androidx.room.compiler)
        implementation(libs.androidx.navigation.fragment.ktx)
        implementation(libs.androidx.navigation.ui.ktx)
        implementation(libs.androidx.activity.ktx)
        implementation(libs.lottie)
        implementation(libs.timber)
        implementation(libs.persianDate)
        implementation(libs.androidx.paging.runtime)
        implementation(libs.mpAndroidChart)
        implementation(libs.materialTapTargetPrompt)
        implementation(libs.pdfViewer)
        debugImplementation(libs.chucker.library)
        releaseImplementation(libs.chucker.library.no.op)
        implementation(libs.otaliastudios.zoomlayout)
        implementation(libs.androidx.browser)
        implementation(libs.androidx.fragment.ktx)
        implementation(libs.leinardi.speeddial)
        implementation(libs.zxing.core)
        implementation(libs.androidx.security.crypto)
        implementation(libs.androidx.biometric)
        implementation("com.github.massoudss:waveformSeekBar:5.0.2")
        implementation("com.github.squti:Android-Wave-Recorder:2.1.0")
        implementation("com.github.Armen101:AudioRecordView:1.0.5")
        implementation("com.github.lincollincol:amplituda:2.2.2")
        debugImplementation ("com.squareup.leakcanary:leakcanary-android:2.14")
    }
}
sentry {
    org = "sentry"
    url = apiKeyProperties["SENTRY_URL"].toString()
    projectName = "tamin-hamrah"
    includeSourceContext = false
    includeProguardMapping = true
    authToken = apiKeyProperties["SENTRY_AUTH_TOKEN"].toString()
}
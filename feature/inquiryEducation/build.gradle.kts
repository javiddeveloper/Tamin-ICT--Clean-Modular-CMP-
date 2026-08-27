plugins {
    alias(libs.plugins.tamin.kmp.feature)
    alias(libs.plugins.kotlin.serialization)
}

val robolectricSdk: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    robolectricSdk(libs.robolectric.android.all)
}

val robolectricSdkDir: Provider<Directory> = layout.buildDirectory.dir("robolectric-sdk")

val stageRobolectricSdk by tasks.registering(Sync::class) {
    from(robolectricSdk)
    into(robolectricSdkDir)
}

android {
    namespace = "com.tamin.taminhamrah.feature.inquiryEducation"

    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.all { test ->
            test.dependsOn(stageRobolectricSdk)
            test.systemProperty("robolectric.dependency.dir", robolectricSdkDir.get().asFile.absolutePath)
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
        }

        androidUnitTest.dependencies {
            implementation(libs.robolectric)
        }
    }
}

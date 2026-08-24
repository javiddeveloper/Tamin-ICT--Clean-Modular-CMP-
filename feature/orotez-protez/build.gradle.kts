plugins {
    alias(libs.plugins.tamin.kmp.feature)
    alias(libs.plugins.kotlin.serialization)
}

/**
 * Robolectric resolves its android-all jar itself, at *test runtime*, straight from
 * repo1.maven.org — it does not go through Gradle's repositories. CI only reaches
 * nexus.tamin.ir, so that download dies with UnknownHostException and every Robolectric
 * test in this module fails. Resolve the jar as a normal Gradle dependency instead (which
 * does go through Nexus), stage it in one directory, and hand that directory to Robolectric
 * so it never touches the network.
 */
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
    namespace = "com.tamin.taminhamrah.feature.orotezprotez"

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
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialog.compose)
        }

        androidMain.dependencies {
            // Camera permission launcher (rememberLauncherForActivityResult).
            implementation(libs.androidx.activity.compose)
        }

        androidUnitTest.dependencies {
            implementation(libs.robolectric)
        }
    }
}

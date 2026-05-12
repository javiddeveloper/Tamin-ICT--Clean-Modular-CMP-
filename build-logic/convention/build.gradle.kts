plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}

gradlePlugin {
    plugins {
        create("kmpLibrary") {
            id = "TaminHamrah.kmp.library"
            implementationClass = "TaminHamrahKmpLibraryPlugin"
        }
        create("androidApplication") {
            id = "TaminHamrah.android.application"
            implementationClass = "TaminHamrahAndroidApplicationPlugin"
        }
        create("kmpFeature") {
            id = "TaminHamrah.kmp.feature"
            implementationClass = "TaminHamrahKmpFeaturePlugin"
        }
        create("kmpCompose") {
            id = "TaminHamrah.kmp.compose"
            implementationClass = "TaminHamrahKmpComposePlugin"
        }
    }
}

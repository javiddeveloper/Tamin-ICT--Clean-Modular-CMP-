import org.gradle.kotlin.dsl.invoke
dependencies {
    implementation(libs.androidx.core.ktx)
}
plugins {
    id("TaminHamrah.kmp.library")
    id("TaminHamrah.naming.convention")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktrofit)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:core-domain"))
            implementation(project(":core:core-datastore"))
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.websockets)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.ktorfit.lib)
            implementation(libs.kermit.koin)
            implementation(libs.kermit.logging)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.chucker.debug)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
        }
    }
}

dependencies {
    add("kspAndroid", libs.ktorfit.ksp)
//    add("kspIosX64", libs.ktorfit.ksp)
    add("kspIosArm64", libs.ktorfit.ksp)
    add("kspIosSimulatorArm64", libs.ktorfit.ksp)
    add("kspCommonMainMetadata", libs.ktorfit.ksp)
}

android {
    namespace = "com.tamin.taminhamrah.core.network"
    sourceSets {
        getByName("test") {
            resources.srcDirs("src/commonTest/resources")
        }
    }
}

plugins {
    alias(libs.plugins.tamin.kmp.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tamin.taminhamrah.feature.pensionSurvivor"
}

val localMocksDir = file("src/localMocks/kotlin")

val mockData = false
val mockGateDir = layout.buildDirectory.dir("generated/pensionSurvivorMockGate")

val generatePensionSurvivorMockGate = tasks.register("generatePensionSurvivorMockGate") {
    description = "Selects real or gitignored local-mock Koin modules for pensionSurvivor"
    val mocksPresent = mockData && localMocksDir.exists() &&
        localMocksDir.walkTopDown().any { it.isFile && it.extension == "kt" }
    if (localMocksDir.exists()) {
        inputs.dir(localMocksDir)
    }
    outputs.dir(mockGateDir)
    doLast {
        val pkg = mockGateDir.get().asFile.resolve(
            "com/tamin/taminhamrah/feature/pensionSurvivor/di",
        )
        pkg.mkdirs()
        val source = if (mocksPresent) {
            """
            package com.tamin.taminhamrah.feature.pensionSurvivor.di

            import com.tamin.taminhamrah.feature.pensionSurvivor.localMocks.pensionSurvivorLocalMockModule
            import org.koin.core.module.Module

            fun pensionSurvivorFeatureModules(): List<Module> = listOf(pensionSurvivorLocalMockModule)
            """.trimIndent()
        } else {
            """
            package com.tamin.taminhamrah.feature.pensionSurvivor.di

            import org.koin.core.module.Module

            fun pensionSurvivorFeatureModules(): List<Module> = listOf(pensionSurvivorModule)
            """.trimIndent()
        }
        pkg.resolve("PensionSurvivorFeatureModules.kt").writeText(source)
    }
}

tasks.configureEach {
    if (name.startsWith("compile") && name.contains("Kotlin")) {
        dependsOn(generatePensionSurvivorMockGate)
    }
}

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir(mockGateDir)
            if (localMocksDir.exists()) {
                kotlin.srcDir(localMocksDir)
            }
            dependencies {
                implementation(libs.androidx.navigation.compose)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.kotlinx.collections.immutable)
                implementation(libs.filekit.core)
                implementation(libs.filekit.dialog.compose)
                implementation(libs.kotlinx.datetime)
                if (localMocksDir.exists()) {
                    implementation(libs.ktor.client.core)
                }
            }
        }
    }
}

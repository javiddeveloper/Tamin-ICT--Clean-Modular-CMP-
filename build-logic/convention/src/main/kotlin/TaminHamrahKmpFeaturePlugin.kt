import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class TaminHamrahKmpFeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("TaminHamrah.kmp.library")
            pluginManager.apply("TaminHamrah.kmp.compose")

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.apply {
                    commonMain.dependencies {
                        implementation(project(":core:core-domain"))
                        implementation(project(":core:core-network"))
                        implementation(project(":core:core-database"))
                        implementation(project(":core:core-ui"))

                        implementation(libs.findLibrary("koin-core").get())
                        implementation(libs.findLibrary("koin-core-viewmodel").get())
                        implementation(libs.findLibrary("koin-compose").get())
                        implementation(libs.findLibrary("koin-compose-viewmodel").get())
                        implementation(libs.findLibrary("androidx-lifecycle-viewmodel").get())
                        implementation(libs.findLibrary("kotlinx-coroutines-core").get())
                    }

                    androidMain.dependencies {
                        implementation(libs.findLibrary("androidx-core-ktx").get())
                    }

                    commonTest.dependencies {
                        implementation(libs.findLibrary("kotlin-test").get())
                        implementation(libs.findLibrary("kotlinx-coroutines-test").get())
                        implementation(libs.findLibrary("turbine").get())
                    }
                }
            }
        }
    }
}

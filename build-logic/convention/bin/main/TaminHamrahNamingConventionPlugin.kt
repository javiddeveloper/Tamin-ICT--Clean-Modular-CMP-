import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class TaminHamrahNamingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val checkTask = tasks.register("checkNamingConvention") {
                group = "verification"
                description = "Enforces naming conventions for models and mappers based on architecture layers."
                doLast {
                    val conventions = when {
                        path.endsWith("core-domain") -> listOf("model" to "DN.kt")
                        path.endsWith("core-network") -> listOf("model" to "Dto.kt")
                        path.endsWith("core-ui") -> listOf("model" to "PR.kt", "mapper" to "Mapper.kt")
                        path.endsWith("core-database") -> listOf("data/local/entity" to "Entity.kt")
                        path.endsWith("core-data") -> listOf("data/mapper" to "Mapper.kt")
                        else -> emptyList()
                    }

                    if (conventions.isEmpty()) return@doLast

                    var totalErrors = 0
                    conventions.forEach { (folderPath, suffix) ->
                        val possibleRoots = listOf(
                            "src/commonMain/kotlin/com/tamin/taminhamrah/$folderPath",
                            "src/commonMain/kotlin/com/tamin/taminx/$folderPath"
                        )

                        possibleRoots.map { file(it) }.filter { it.exists() }.forEach { baseDir ->
                            baseDir.walkTopDown()
                                .filter { it.isFile && it.extension == "kt" }
                                .forEach { file ->
                                    if (!file.name.endsWith(suffix)) {
                                        println("ERROR: File ${file.absolutePath} in module $path must end with '$suffix'")
                                        totalErrors++
                                    }
                                }
                        }
                    }

                    if (totalErrors > 0) {
                        throw GradleException("Found $totalErrors naming convention violations in $path. Check logs for details.")
                    }
                }
            }

            // Link to common tasks to ensure it runs
            afterEvaluate {
                tasks.matching {
                    it.name == "check" ||
                    it.name.contains("assemble", ignoreCase = true) ||
                    it.name.contains("compileKotlin", ignoreCase = true)
                }.all {
                    dependsOn(checkTask)
                }
            }
        }
    }
}

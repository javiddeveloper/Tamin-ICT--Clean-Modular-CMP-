pluginManagement {
    includeBuild("build-logic")
    repositories {
        maven { url = uri("https://maven.myket.ir/") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://maven.myket.ir/") }
        google()
        mavenCentral()
    }
}

rootProject.name = "TaminX"
include(":shared")
include(":androidApp")
include(":core:core-domain")
include(":core:core-network")
include(":core:core-database")
include(":core:core-ui")
include(":core:core-plugin")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://maven.myket.ir/") }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://maven.myket.ir/") }
    }
}

rootProject.name = "TaminX"
include(":shared")
include(":androidApp")
include(":core:core-domain")
include(":core:core-network")
include(":core:core-database")
include(":core:core-data")
include(":core:core-datastore")
include(":core:core-ui")
include(":core:core-plugin")
include(":feature:profile")

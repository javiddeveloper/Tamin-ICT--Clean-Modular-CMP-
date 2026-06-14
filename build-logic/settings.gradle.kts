dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://nexus.tamin.ir/content/groups/public") }
        maven { url = uri("https://maven.myket.ir/") }
        google()
        mavenCentral()
    }
}

pluginManagement {
    repositories {
        maven { url = uri("https://nexus.tamin.ir/content/groups/public") }
        maven { url = uri("https://maven.myket.ir/") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "build-logic"
include(":convention")

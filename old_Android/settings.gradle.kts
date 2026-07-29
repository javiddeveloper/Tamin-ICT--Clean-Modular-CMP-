pluginManagement {
    repositories {
        maven { url = uri("https://maven.myket.ir/") }
//        gradlePluginPortal()
//        google()
//        mavenCentral()
//        maven {
//            url = uri("https://nexus.tamin.ir/content/groups/public")
//        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://maven.myket.ir/") }
//        google()
//        mavenCentral()
//         maven {
//             url = uri("https://nexus.tamin.ir/content/groups/public")
//         }
//        maven { url = uri("https://jitpack.io") }
//        maven { url = uri("https://www.jitpack.io") }
//        maven { url = uri("https://jcenter.bintray.com") }
    }
}

rootProject.name = "TaminHamrah"
include(":app")

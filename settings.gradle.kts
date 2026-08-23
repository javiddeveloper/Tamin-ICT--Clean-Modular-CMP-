pluginManagement {
    includeBuild("build-logic")
    repositories {
        maven { url = uri("https://nexus.tamin.ir/content/groups/public") }
        maven { url = uri("https://maven.myket.ir/") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://nexus.tamin.ir/content/groups/public") }
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
include(":core:core-data")
include(":core:core-datastore")
include(":core:core-ui")
include(":core:core-plugin")
include(":feature:profile")
include(":feature:treatment")
include(":feature:pensioner")
include(":feature:cartable")
include(":feature:history")
include(":feature:contracts")
include(":feature:workshops")
include(":feature:studentInsuranceContract")
include(":feature:agent")
include(":feature:healthProfile")
include(":feature:taminServices")
include(":feature:change-mobile")
include(":feature:my-inbox")
include(":feature:addDependent")
include(":feature:pensionStatusInquiry")
include(":feature:userRequest")
include(":feature:security")
include(":feature:settings")
include(":feature:orotez-protez")
include(":feature:girlSurvivor")

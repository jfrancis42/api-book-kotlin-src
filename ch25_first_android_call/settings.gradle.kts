pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(
        RepositoriesMode.FAIL_ON_PROJECT_REPOS
    )
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "ch25_first_android_call"
include(":app")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenLocal()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}


rootProject.name = "TheKey"
include(":app_mobile")
include(":dynamic_qrcodescanner")

if (file("private").exists()) {
    include(":private:feature_firebase")
}

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
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}


rootProject.name = "TheKey"
include(":core")
include(":app_mobile")
include(":dynamic_qrcodescanner")

val isPrivateAvailable = file("private").list()?.isNotEmpty() ?: false

if (isPrivateAvailable) {
    include(":private:feature_firebase")
    include(":private:feature_billing")
    include(":private:dynamic_autofill")
}

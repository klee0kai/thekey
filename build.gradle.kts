buildscript {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.10")
    }
}

tasks.register<Delete>("clean") {
    doLast {
        delete(rootProject.buildDir)
    }
}


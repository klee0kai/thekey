buildscript {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.3.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.20")
        classpath("com.github.klee0kai.brooklyn:brooklyn-plugin:0.0.3")
    }
}

tasks.register<Delete>("clean") {
    doLast {
        delete(rootProject.buildDir)
    }
}


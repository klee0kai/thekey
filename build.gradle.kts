buildscript {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.4.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.22")
        classpath("com.github.klee0kai.brooklyn:brooklyn-plugin:0.0.3")
        classpath("app.cash.paparazzi:paparazzi-gradle-plugin:1.3.3")
    }

}

subprojects {
    plugins.withId("app.cash.paparazzi") {
        afterEvaluate {
            // https://cashapp.github.io/paparazzi/changelog/#132-2024-01-13
            dependencies.constraints {
                add("testImplementation", "com.google.guava:guava") {
                    attributes {
                        attribute(
                            TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE,
                            objects.named(TargetJvmEnvironment::class, TargetJvmEnvironment.STANDARD_JVM)
                        )
                    }
                    because("https://cashapp.github.io/paparazzi/changelog/#132-2024-01-13")
                }
            }
        }
    }
}

tasks.register<Delete>("clean") {
    doLast {
        delete(rootProject.buildDir)
    }
}


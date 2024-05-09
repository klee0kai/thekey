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
        classpath(libs.agp)
        classpath(libs.kotlin.plugin)
        classpath(libs.kotlin.serialization)
        classpath(libs.brooklyn)
        classpath(libs.paparazzi)
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


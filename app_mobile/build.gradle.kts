import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("brooklyn-plugin")
    id("app.cash.paparazzi")
}

val appGroup = "com.github.klee0kai.thekey.app"

brooklyn {
    group = appGroup
}

android {
    namespace = appGroup
    compileSdk = 34
    dynamicFeatures += setOf(":dynamic_qrcodescanner")

    defaultConfig {
        applicationId = appGroup
        minSdk = 25
        targetSdk = 34
        versionCode = 6
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        externalNativeBuild {
            cmake {
                cppFlags.add("")
                arguments.add("")
                targets.add("crypt-storage-lib")
            }
        }
    }


    signingConfigs.register("release") {
        try {
            val keystoreProperties = Properties().apply {
                File("keystore.properties").inputStream().use { fis ->
                    load(fis)
                }
            }
            storeFile = file(keystoreProperties.getProperty("storeFile")).also {
                check(it.exists()) { "storeFile $it no exist" }
            }
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
        } catch (e: Exception) {
            println("error to configure signing ${e}")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs["release"]
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                abiFilters.addAll(listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a"))
            }
            externalNativeBuild {
                cmake {
                    arguments.add("-DBROOKLYN_FOLDER=release")
                }
            }
        }
        debug {
            signingConfig = signingConfigs["debug"]
            externalNativeBuild {
                cmake {
                    arguments.add("-DBROOKLYN_FOLDER=debug")
                }
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = File("src/main/cpp/CMakeLists.txt")
            version = "3.10.2"
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    composeOptions {
        //  https://developer.android.com/jetpack/androidx/releases/compose-kotlin
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all { test ->
                test.environment["robolectric.logging.enabled"] = "true"
                test.maxHeapSize = "4g"
                if (project.hasProperty("parallel")) {
                    test.maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
                }
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

afterEvaluate {
    val kotlinCompileTasks = tasks.filter {
        it is JavaCompile || it is KotlinCompile
    }
    val cmakeTasks = tasks.filter {
        it is com.android.build.gradle.tasks.ExternalNativeBuildJsonTask ||
                it is com.android.build.gradle.tasks.ExternalNativeBuildTask
    }

    cmakeTasks.forEach { cmakeTask ->
        kotlinCompileTasks.forEach { kotlinTask ->
            cmakeTask.mustRunAfter(kotlinTask)
        }
    }
}

dependencies {
    implementation(project(":private:feature_firebase"))

    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle)

    implementation(libs.compose.bom)
    implementation(libs.bundles.compose)
    debugImplementation(libs.bundles.compose.debug)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.stone)

    implementation(libs.jetbrain.immutable)
    implementation(libs.jetbrain.coroutines)

    implementation(libs.ml.dynamic)

    // stone
    implementation(libs.bundles.stone)
    kapt(libs.stone.kapt)

    // room
    implementation(libs.room.runtime)
    kapt(libs.room.kapt)

    // hummus
    implementation(libs.hummus)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)
    androidTestImplementation(libs.compose.test)
}
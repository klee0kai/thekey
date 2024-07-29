import com.android.build.api.dsl.ApplicationBuildType

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
    appDefaults(appGroup, project)
    dynamicFeatures += setOf(":dynamic_qrcodescanner", ":dynamic_findstorage")
    if (Commercial.isCommercialAvailable) {
        dynamicFeatures += setOf(":private:dynamic_autofill", ":private:dynamic_gdrive")
    }

    defaultConfig {
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
    buildTypes {
        val releaseConf: ApplicationBuildType.() -> Unit = {
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
                    arguments.add("-DBROOKLYN_FOLDER=${name}")
                }
            }
        }
        release(releaseConf)
        commercialRelease(releaseConf)

        val debugConf: ApplicationBuildType.() -> Unit = {
            isDebuggable = true
            signingConfig = signingConfigs["debug"]
            externalNativeBuild {
                cmake {
                    arguments.add("-DBROOKLYN_FOLDER=${name}")
                }
            }
        }
        debug(debugConf)
        commercialDebug {
            debugConf()
            signingConfig = signingConfigs["release"]
        }
    }
    commercialSourceSets()

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
    kotlinOptions {
        jvmTarget = "17"
    }
}

brooklynTaskOrdering()

dependencies {
    commercialImplementation { project(":private:feature_firebase") }
    commercialImplementation { project(":private:feature_billing") }
    implementation(project(":core"))

    implementation(libs.compose.bom)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.compose.debug)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.stone)
    implementation(libs.bundles.fix.doubles)

    implementation(libs.jetbrain.immutable)
    implementation(libs.jetbrain.coroutines)

    implementation(libs.ml.dynamic)

    kapt(libs.stone.kapt)

    implementation(libs.room.runtime)
    kapt(libs.room.kapt)

    implementation(libs.hummus)
    implementation(libs.lorem)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)
    androidTestImplementation(libs.compose.test)
}
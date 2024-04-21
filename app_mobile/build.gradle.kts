import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("brooklyn-plugin")
}

val appGroup = "com.github.klee0kai.thekey.app"

brooklyn {
    group = appGroup
}

android {
    namespace = appGroup
    compileSdk = 34
    dynamicFeatures += setOf(":qrcodescanner")

    defaultConfig {
        applicationId = appGroup
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation(platform("androidx.compose:compose-bom:2024.04.00"))
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3-android:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0-alpha13")
    implementation("androidx.wear.compose:compose-material:1.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")

    // dynamic features
//    implementation("com.google.android.play:core:1.10.3")
    implementation("com.google.android.play:feature-delivery-ktx:2.1.0")

    // compose-navigation-reimagined
    implementation("dev.olshevski.navigation:reimagined:1.5.0")

    // shimmer compose https://github.com/valentinilk/compose-shimmer
    implementation("com.valentinilk.shimmer:compose-shimmer:1.2.0")

    // coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // stone
    implementation("com.github.klee0kai.stone:android_lib:1.0.4")
    implementation("com.github.klee0kai.stone:kotlin_lib:1.0.4")
    kapt("com.github.klee0kai.stone:stone_processor:1.0.4")

    // BarCodeScan
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.mlkit:vision-common:17.3.0")
    implementation("com.google.mlkit:camera:16.0.0-beta3")
    implementation("com.google.android.gms:play-services-vision-common:19.1.3")
    implementation("androidx.camera:camera-mlkit-vision:1.4.0-alpha04")

    // room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // hummus
    implementation("com.github.klee0kai.hummus:android_kotlin_hummus:0.0.2")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
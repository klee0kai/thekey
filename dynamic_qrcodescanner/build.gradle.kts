plugins {
    id("com.android.dynamic-feature")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("app.cash.paparazzi")
}

android {
    namespace = "com.github.klee0kai.thekey.feature.qrcodescanner"
    compileSdk = 34

    defaultConfig {
        minSdk = 25
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.register("pushFeatureDebug") {
    dependsOn(tasks.getByName("assembleDebug"))
    notCompatibleWithConfigurationCache("todo")
    val adb = android.adbExecutable
    val apkFile = layout.buildDirectory.file("outputs/apk/debug").get().asFile
    doLast {
        exec { commandLine = listOf(adb.absolutePath, "shell", "rm", "-rf", "/data/local/tmp/tkey_features") }
    }
    doLast {
        exec { commandLine = listOf(adb.absolutePath, "shell", "mkdir", "-p", "/data/local/tmp/tkey_features") }
    }
    doLast {
        exec { commandLine = listOf(adb.absolutePath, "push", apkFile.absolutePath, "/data/local/tmp/tkey_features") }
    }
}

dependencies {
    implementation(project(":app_mobile"))

    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle)

    implementation(libs.bundles.compose)
    debugImplementation(libs.bundles.compose.debug)

    implementation(libs.jetbrain.immutable)
    implementation(libs.jetbrain.coroutines)

    // stone
    implementation(libs.bundles.stone)
    kapt(libs.stone.kapt)

    // BarCodeScan
    implementation(libs.ml.barcode)
    implementation(libs.ml.camera)
    implementation(libs.ml.vision)

    // hummus
    implementation(libs.hummus)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)
}
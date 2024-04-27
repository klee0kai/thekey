plugins {
    id("com.android.dynamic-feature")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
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
        kotlinCompilerExtensionVersion = "1.5.4"
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
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3-android:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0-alpha13")
    implementation("androidx.wear.compose:compose-material:1.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")

    // stone
    implementation("com.github.klee0kai.stone:android_lib:1.0.4")
    implementation("com.github.klee0kai.stone:kotlin_lib:1.0.4")
    kapt("com.github.klee0kai.stone:stone_processor:1.0.4")

    // BarCodeScan
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.mlkit:vision-common:17.3.0")
    implementation("com.google.mlkit:camera:16.0.0-beta3")
    implementation("com.google.android.gms:play-services-vision-common:19.1.3")
    implementation("androidx.camera:camera-mlkit-vision:1.4.0-alpha05")

    // hummus
    implementation("com.github.klee0kai.hummus:android_kotlin_hummus:0.0.2")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.annotation:annotation:1.7.1")
}
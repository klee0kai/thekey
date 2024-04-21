plugins {
    id("com.android.dynamic-feature")
    id("org.jetbrains.kotlin.android")
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
}

tasks.register("installDebug") {
    dependsOn(tasks.getByName("assembleDebug"))
    notCompatibleWithConfigurationCache("todo")
    val adb = android.adbExecutable
    val apkFile = layout.buildDirectory.file("outputs/apk/debug").get().asFile
    doLast {
        exec { commandLine = listOf(adb.absolutePath, "shell", "mkdir", "-p", "/data/local/tmp/tkey_features") }
    }
    doLast {
        exec { commandLine = listOf(adb.absolutePath, "push", apkFile.absolutePath, "/data/local/tmp/tkey_features") }
    }
}

dependencies {
    implementation(project(":app_mobile"))
    implementation("androidx.core:core-ktx:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.annotation:annotation:1.7.1")
}
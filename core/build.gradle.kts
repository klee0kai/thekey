plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    kotlin("kapt")
}

android {
    defaults("com.github.klee0kai.thekey.core", project)

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {

        }
    }

    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(libs.compose.bom)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.compose.debug)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.stone)

    implementation(libs.ml.dynamic)

    implementation(libs.jetbrain.immutable)
    implementation(libs.jetbrain.coroutines)

    implementation(libs.hummus)

    implementation(libs.bundles.stone)
    kapt(libs.stone.kapt)

    implementation(libs.room.runtime)
    kapt(libs.room.kapt)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso)
}
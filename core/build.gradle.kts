plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("kotlin-parcelize")
    kotlin("plugin.serialization")
}

android {
    defaults("com.github.klee0kai.thekey.core", project)

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            proguardFiles("proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
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

    implementation(libs.ml.common)
    implementation(libs.ml.dynamic)

    implementation(libs.jetbrain.immutable)
    implementation(libs.jetbrain.coroutines)
    implementation(libs.jetbrain.serialization.json)

    implementation(libs.hummus)

    implementation(libs.bundles.stone)
    kapt(libs.stone.kapt)

    implementation(libs.room.runtime)
    kapt(libs.room.kapt)

    implementation(libs.lorem)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso)
}
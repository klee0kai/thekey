plugins {
    id("com.android.application")
}

android {

    namespace = "com.kee0kai.thekey"
    compileSdk = 34
    ndkVersion = "21.4.7075529"


    defaultConfig {
        applicationId = "com.kee0kai.thekey"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "0.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags.add("")
                arguments.add("ANDROID_BUILD=TRUE")
                targets.add("crypt-storage-lib")
            }
        }
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles.add(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFiles.add(File("proguard-rules.pro"))
            ndk {
                abiFilters.addAll(listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a"))
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    externalNativeBuild {
        cmake {
            path = File("../tkcore/CMakeLists.txt")
            version = "3.10.2"
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

apply(from = "../signing.gradle")

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    //stone
    implementation("com.github.klee0kai.stone:android_lib:1.0.3")
    annotationProcessor("com.github.klee0kai.stone:stone_processor:1.0.3")

    // room
    implementation("androidx.room:room-runtime:2.6.0")
    annotationProcessor("androidx.room:room-compiler:2.6.0")


    //hummus
    implementation("com.github.klee0kai.hummus:android_java_hummus:0.0.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
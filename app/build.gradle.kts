/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
}

android {
    namespace = "com.viwath.srulibrarymobile"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.viwath.srulibrarymobile"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }

    @Suppress("DEPRECATION")
    packagingOptions{
        resources.excludes.add("META-INF/DEPENDENCIES")
        exclude("META-INF/gradle/incremental.annotation.processors")
    }
}
kapt {
    correctErrorTypes = true
    includeCompileClasspath = false
    arguments {
        arg("dagger.fastInit", "enabled")
        arg("kapt.kotlin.generated", "true")
    }
}
hilt {
    enableAggregatingTask = true
}
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // google android material design
    implementation(libs.material.v1130alpha01)


    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // hilt
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.compiler)
    kapt(libs.hilt.android.compiler)
    kapt(libs.androidx.hilt.compiler)

    // view-model
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    //MPAndroidChart
    implementation(libs.github.philJay.mpAndroidChart)
    // Glass morphism
    implementation(libs.fk.blur.view.android)
    // Circle progress
    implementation(libs.circleprogress)
    // Swipe refresh
    implementation(libs.androidx.swiperefreshlayout)
    // qrcode reader
    implementation(libs.zxing)
    implementation(libs.zxing.android.embedded)
    // CameraX
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)

    // flexbox
    implementation(libs.flexbox.layout)
    //security-crypto
    implementation(libs.security.crypto)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

}
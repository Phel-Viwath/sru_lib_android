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
        versionName = "1.0-demo"

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

    applicationVariants.all {
        outputs.all {
            val outputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            val variantName = name
            val versionName = versionName
            val versionCode = versionCode

            outputImpl.outputFileName = "SRU-Lib-${variantName}-v${versionName}-${versionCode}.apk"
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true // for datetime
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
//kapt {
//    correctErrorTypes = true
//    includeCompileClasspath = false
//    arguments {
//        arg("dagger.fastInit", "enabled")
//        arg("kapt.kotlin.generated", "true")
//    }
//}
hilt {
    enableAggregatingTask = true
}
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    // google android material design
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

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
    // blur view (Dimezis)
    implementation(libs.blurview)

    // Circle progress
    implementation(libs.circleprogress)
    // Swipe refresh
    implementation(libs.androidx.swiperefreshlayout)
    // qrcode reader
    implementation(libs.zxing)
    implementation(libs.zxing.android.embedded)
    implementation(libs.code.scanner)
    // CameraX
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)

    // flexbox
    implementation(libs.flexbox.layout)
    //security-crypto
    implementation(libs.security.crypto)

    // biometric
    implementation (libs.androidx.biometric)

    // date time
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // https://mvnrepository.com/artifact/androidx.datastore/datastore-preferences
    implementation(libs.androidx.datastore.preferences)


}
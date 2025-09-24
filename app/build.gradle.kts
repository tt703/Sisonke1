

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms)
    alias(libs.plugins.firebase.crashlytics.plugin)

}

android {
    namespace = "com.example.tlotlotau"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tlotlotau"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    viewBinding {
        enable = true
    }
}

dependencies {
    //Activity layout
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.annotation)
    implementation(libs.com.google.material)
    implementation(libs.androidx.activity)

    // PDF viewer
    implementation("com.github.barteksc:PdfiumAndroid:pdfium-android-1.9.0")

    // Optional PDF generation libraries
    implementation(libs.openpdf)
    implementation(libs.itextpdf)
    implementation(libs.itext7.core)

    // QR/Barcode
    implementation(libs.zxing.core)
    implementation(libs.zxing.android.embedded)

    // Firebase
    implementation(libs.firebase.crashlytics)
    implementation (platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-auth-ktx:23.2.1")
    // Firebase Functions client
    implementation("com.google.firebase:firebase-functions:22.0.1")
    //Firebase Firestore
    implementation(libs.firebase.firestore)



    //Image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")


    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

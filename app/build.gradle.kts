import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
}

val keystoreProperties = try {
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    Properties().apply {
        load(FileInputStream(keystorePropertiesFile))
    }
} catch (_: Throwable) {
    Properties()
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

android {

    namespace = "the.autarch.pixelista"
    compileSdk = 37

    defaultConfig {
        applicationId = "the.autarch.pixelista"
        minSdk = 28
        targetSdk = 37
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        val keystoreFile = if (keystoreProperties.containsKey("storeFile")) {
            val keystorePath = keystoreProperties["storeFile"] as String
            file(System.getProperty("user.home") + File.separator + keystorePath)
        } else {
            file(System.getProperty("user.home"))
        }
        create("playstore") {
            storeFile = keystoreFile
            storePassword = keystoreProperties["storePassword"] as? String
            keyAlias = keystoreProperties["keyAlias"]  as? String
            keyPassword = keystoreProperties["keyPassword"] as? String
        }
    }
    flavorDimensions += "signing"
    productFlavors {
        create("github") {}
        create("playstore") {}
    }
    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            productFlavors.getByName("playstore").signingConfig = signingConfigs.getByName("playstore")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.adaptive)
    implementation(libs.androidx.adaptive.layout)
    implementation(libs.androidx.adaptive.navigation)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.material.kolor)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
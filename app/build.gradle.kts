import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id ("com.chaquo.python")
    id("com.google.gms.google-services")
}

android {
    namespace = "eu.ase.travelcompanionapp"
    compileSdk = 35
    flavorDimensions += "pyVersion"
    productFlavors {
        create("py310") { dimension = "pyVersion" }
        create("py311") { dimension = "pyVersion" }
    }
    defaultConfig {
        applicationId = "eu.ase.travelcompanionapp"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }

        val properties = gradleLocalProperties(rootDir, providers)
        buildConfigField("String", "GOOGLE_API_KEY",
            properties.getProperty("GOOGLE_API_KEY")
        )
        buildConfigField("String", "AMADEUS_API_KEY",
            properties.getProperty("AMADEUS_API_KEY")
        )
        buildConfigField("String", "AMADEUS_API_SECRET",
            properties.getProperty("AMADEUS_API_SECRET")
        )
        buildConfigField("String", "GROQ_KEY",
            properties.getProperty("GROQ_KEY")
        )
        buildConfigField("String", "OPEN_AI_API_KEY",
            properties.getProperty("OPEN_AI_API_KEY")
        )
        buildConfigField("String", "RAPID_API_KEY",
            properties.getProperty("RAPID_API_KEY")
        )
        buildConfigField("String", "STRIPE_PUBLISHABLE_KEY",
            properties.getProperty("STRIPE_PUBLISHABLE_KEY")
        )
        buildConfigField("String", "STRIPE_SECRET_KEY",
            properties.getProperty("STRIPE_SECRET_KEY")
        )
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}
chaquopy {
    productFlavors {
        getByName("py310") { version = "3.10" }
        getByName("py311") { version = "3.11" }
    }
    defaultConfig {
        pip{
            install("numpy")
            install("openai==0.28.1")
        }
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
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.firebase.functions.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.bundles.koin)

    implementation(libs.bundles.ktor)

    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)

    implementation(libs.bundles.coil)

    implementation(libs.androidx.runtime.livedata)

    implementation(libs.places)

    implementation (libs.accompanist.pager)

    implementation(libs.accompanist.permissions)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    implementation(libs.androidx.datastore.preferences)

    implementation (libs.stripe.android)
}

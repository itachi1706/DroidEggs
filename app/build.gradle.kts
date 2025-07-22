plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
    alias(libs.plugins.compose.compiler)
}

val isGHActions: Boolean = System.getenv("GITHUB_ACTIONS")?.toBoolean() ?: false

android {
    compileSdk = 36
    buildToolsVersion = "35.0.0"

    defaultConfig {
        namespace = "com.itachi1706.droideggs"
        applicationId = "com.itachi1706.droideggs"
        minSdk = 21
        targetSdk = 36
        versionCode = 721
        versionName = "4.3.2"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    androidResources {
        localeFilters += listOf(
            "en", // English
        )
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    packaging {
        resources {
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("META-INF/NOTICE")
            excludes.add("META-INF/LICENSE*")
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            multiDexEnabled = true
        }
        create("googlePlay") {
            initWith(getByName("release"))
            matchingFallbacks.add("release")
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    // This enables long timeouts required on slow environments, e.g. Travis
    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
    installation {
        timeOutInMs = 10 * 60 * 1000 // Set the timeout to 10 minutes
        installOptions.addAll(listOf("-d", "-t"))
    }
    lint {
        abortOnError = !isGHActions
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // Import the BoM for the Firebase platformc
    implementation(platform(libs.firebase.bom))
    // Import the Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.animation.core.android)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.dynamicanimation.ktx)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.material)
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.window)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)
    implementation(libs.material)
    implementation(libs.appupdater)
    implementation(libs.helperlib)
    implementation(libs.attribouter) {
        exclude(group = "com.google.android", module = "flexbox")
    }
    implementation(libs.flexbox)
}

apply(plugin = libs.plugins.google.services.get().pluginId)

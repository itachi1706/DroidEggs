plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

val isGHActions: Boolean = System.getenv("GITHUB_ACTIONS")?.toBoolean() ?: false

android {
    compileSdk = 34
//    buildToolsVersion = "34.0.0"
//    buildToolsVersion.set("34.0.0")
    buildToolsVersion = "34.0.0"

    defaultConfig {
        namespace = "com.itachi1706.droideggs"
        applicationId = "com.itachi1706.droideggs"
        minSdk = 21
        targetSdk = 34
        versionCode = 649
        versionName = "4.3.1"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resValue("string", "tray__authority", "${applicationId}.tray")
        resourceConfigurations.add("en")
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
        installOptions.add("-d")
        installOptions.add("-t")
//        installOptions = listOf("-d", "-t")
    }
    lint {
        abortOnError = !isGHActions
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

val supportLibraryVersion: String = "28.0.0+"

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    // Import the Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))

    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.compose.animation:animation-core-android")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.ui:ui-graphics-android")
    implementation("androidx.compose.foundation:foundation-android:1.6.8")
    implementation("androidx.compose.ui:ui-tooling-preview-android:1.6.8")
    implementation("androidx.window:window:1.3.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.dynamicanimation:dynamicanimation-ktx:1.0.0-alpha03")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("com.google.firebase:firebase-perf-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.itachi1706.appupdater:appupdater:3.0.2")
    implementation("com.itachi1706.helpers:helperlib:1.4.3")
    implementation("me.jfenn:Attribouter:0.1.9")
}

apply(plugin = "com.google.gms.google-services")

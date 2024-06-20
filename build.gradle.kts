plugins {
    id("com.android.application") version "8.4.2" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.1" apply false
    id("com.google.firebase.firebase-perf") version "1.4.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("org.sonarqube") version "5.0.0.4638"
}

sonarqube {
    properties {
        property("sonar.projectKey", "itachi1706_DroidEggs")
        property("sonar.organization", "itachi1706")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.androidLint.reportPaths", "app/build/reports/lint-results-debug.xml")
        property("sonar.projectVersion", project(":app").extensions.getByType(com.android.build.gradle.AppExtension::class.java).defaultConfig.versionName ?: "1.0")
    }
}
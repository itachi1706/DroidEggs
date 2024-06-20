plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.sonarqube)
}

sonarqube {
    properties {
        property("sonar.projectKey", "itachi1706_DroidEggs")
        property("sonar.organization", "itachi1706")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.androidLint.reportPaths", "app/build/reports/lint-results-debug.xml")
        property("sonar.projectVersion",
            project(":app").extensions.getByType(com.android.build.gradle.AppExtension::class.java).defaultConfig.versionName
                ?: "1.0"
        )
    }
}
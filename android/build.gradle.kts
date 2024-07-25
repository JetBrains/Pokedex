
plugins {
    id("com.android.application")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    @Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "OPT_IN_USAGE")
    compilerOptions {
        languageVersion.set(
            org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(
                version = getOverriddenKotlinLanguageVersion(project) ?: "2.0"
            )
        )
        apiVersion.set(
            org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(
                version = getOverriddenKotlinApiVersion(project) ?: "2.0"
            )
        )
    }

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation("androidx.activity:activity-compose:1.6.1")

                // Koin
                api(libs.koin.android)
            }
        }
    }
}

android {
    namespace = "com.mocoding.pokedex.android"
    compileSdk = Configuration.compileSdk
    defaultConfig {
        applicationId = "com.mocoding.pokedex.android"
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
        versionCode = Configuration.versionCode
        versionName = Configuration.versionName
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

object Configuration {
    const val compileSdk = 34
    const val targetSdk = 34
    const val minSdk = 24
    const val majorVersion = 1
    const val minorVersion = 0
    const val patchVersion = 0
    const val versionName = "$majorVersion.$minorVersion.$patchVersion"
    const val versionCode = 1
}

fun getOverriddenKotlinLanguageVersion(project: Project): String? {
    val languageVersion = project.rootProject.properties["kotlin_language_version"] as? String
    if (languageVersion != null) {
        project.logger.info("""[COMMUNITY_PROJECT_BUILD] Configured Kotlin Language version: '$languageVersion' for project ${project.name}""")
    }
    return languageVersion
}

fun getOverriddenKotlinApiVersion(project: Project): String? {
    val apiVersion = project.rootProject.properties["kotlin_api_version"] as? String
    if (apiVersion != null) {
        project.logger.info("""[COMMUNITY_PROJECT_BUILD] Configured Kotlin API version: '$apiVersion' for project $${project.name}""")
    }
    return apiVersion
}
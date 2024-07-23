import com.mocoding.pokedex.Configuration
import com.mocoding.pokedex.Deps

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
                with(Deps.Koin) {
                    api(android)
                }
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
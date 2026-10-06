
plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.compose")
    id("app.cash.sqldelight")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    @Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "OPT_IN_USAGE")
    compilerOptions {
        getOverriddenKotlinLanguageVersion(project)?.let {
            languageVersion.set(
                org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(it)
            )
        }
        getOverriddenKotlinApiVersion(project)?.let {
            apiVersion.set(
                org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(it)
            )
        }
    }

    jvm("desktop")

    android {
        namespace = "com.mocoding.pokedex"
        compileSdk = Configuration.compileSdk
        minSdk = Configuration.minSdk
        compilerOptions {
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }


    cocoapods {
        summary = "Pokedex the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "15.0"
        podfile = project.file("../ios/Podfile")
        framework {
            baseName = "shared"
            isStatic = true

            export(libs.decompose)
            export(libs.essenty.lifecycle)
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation("co.touchlab:stately-common:2.0.6")

            // Compose
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.material)
            api(libs.compose.material3)
            api(libs.material.icons)

            // Ktor
            api(libs.ktor.client)
            api(libs.ktor.serialization.json)
            api(libs.ktor.content.negotiation)
            api(libs.ktor.client.logging)

            // Logback for ktor logging
            implementation(libs.logback)

            // SqlDelight
            api(libs.cash.coroutines.extension)
            api(libs.cash.primitive.adapters)

            // Koin
            api(libs.koin.core)
            api(libs.koin.test)

            // KotlinX Serialization Json
            implementation(libs.kotlinx.serialization)

            // Coroutines
            implementation(libs.coroutines.core)

            // MVIKotlin
            api(libs.mvikotlin)
            api(libs.mvikotlin.main)
            api(libs.mvikotlin.extensions.coroutines)

            // Decompose
            api(libs.decompose)
            api(libs.decompose.ext.compose)

            // Image Loading
            api(libs.github.imageloader)
            api(libs.essenty.lifecycle)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            // Ktor
            implementation(libs.ktor.client.android)

            // SqlDelight
            implementation(libs.cash.sqldelight.android.driver)

            // Koin
            implementation(libs.koin.android)
        }
        val desktopMain by getting {
            dependencies {
                // Ktor
                implementation(libs.ktor.client.java)

                // SqlDelight
                implementation(libs.cash.sqldelight.sqlite.driver)
            }
        }
        iosMain.dependencies {
            // Ktor
            implementation(libs.ktor.client.darwin)

            // SqlDelight
            implementation(libs.cash.sqldelight.native.driver)
        }
    }
}

sqldelight {
    databases {
        register("PokemonDatabase") {
            packageName.set("com.mocoding.pokedex.core.database")
        }
    }
}

object Configuration {
    const val compileSdk = 37
    const val targetSdk = 37
    const val minSdk = 24
    const val majorVersion = 1
    const val minorVersion = 0
    const val patchVersion = 0
    const val versionName = "$majorVersion.$minorVersion.$patchVersion"
    const val versionCode = 1
}

fun getOverriddenKotlinLanguageVersion(project: Project): String? {
    val languageVersion = (project.rootProject.properties["kotlin.language.version"]
        ?: project.rootProject.properties["kotlin.languageVersion"]) as? String
    if (languageVersion != null) {
        project.logger.info("""[COMMUNITY_PROJECT_BUILD] Configured Kotlin Language version: '$languageVersion' for project ${project.name}""")
    }
    return languageVersion
}

fun getOverriddenKotlinApiVersion(project: Project): String? {
    val apiVersion = (project.rootProject.properties["kotlin.api.version"]
        ?: project.rootProject.properties["kotlin.apiVersion"]) as? String
    if (apiVersion != null) {
        project.logger.info("""[COMMUNITY_PROJECT_BUILD] Configured Kotlin API version: '$apiVersion' for project $${project.name}""")
    }
    return apiVersion
}
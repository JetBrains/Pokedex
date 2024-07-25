
plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("kotlin-parcelize")
    id("app.cash.sqldelight")
    id("org.jetbrains.kotlin.plugin.compose")
}

@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
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

    jvm("desktop")

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }


    cocoapods {
        summary = "Pokedex the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../ios/Podfile")
        framework {
            baseName = "shared"
            isStatic = true

            export(libs.decompose)
            export(libs.essenty.lifecycle)
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {

                implementation("co.touchlab:stately-common:2.0.6")
                // Compose
                with(compose) {
                    api(runtime)
                    api(foundation)
                    api(material)
                    api(material3)
                    api(materialIconsExtended)
                }

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
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                // Ktor
                implementation(libs.ktor.client.android)

                // SqlDelight
                implementation(libs.cash.sqldelight.android.driver)

                // Koin
                implementation(libs.koin.android)
            }
        }
        val androidUnitTest by getting

        val desktopMain by getting {
            dependsOn(commonMain)

            dependencies {
                // Ktor
                implementation(libs.ktor.client.java)

                // SqlDelight
                implementation(libs.cash.sqldelight.sqlite.driver)
            }
        }

        val iosMain by creating {
            dependsOn(commonMain)

            dependencies {
                // Ktor
                implementation(libs.ktor.client.darwin)

                // SqlDelight
                implementation(libs.cash.sqldelight.native.driver)
            }
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosX64Main by getting {
            dependsOn(iosMain)
        }
        val iosArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosTest by creating {
            dependsOn(commonTest)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
        val iosArm64Test by getting {
            dependsOn(iosTest)
        }
        val iosX64Test by getting {
            dependsOn(iosTest)
        }
    }
}

android {
    namespace = "com.mocoding.pokedex"
    compileSdk = Configuration.compileSdk
    defaultConfig {
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

sqldelight {
    databases {
        create("PokemonDatabase") {
            packageName.set("com.mocoding.pokedex.core.database")
        }
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
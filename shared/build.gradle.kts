
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

    androidTarget ()

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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// TODO: Remove when Android Lint becomes compatible with Kotlin 2.2.x
// Issue: IncompatibleClassChangeError in NonNullableMutableLiveDataDetector
// caused by Kotlin Analysis API changes (KaCallableMemberCall class vs interface)
afterEvaluate {
    tasks.matching { task: Task -> task.name.contains("lint", ignoreCase = true) }.configureEach {
        enabled = false
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
    const val compileSdk = 35
    const val targetSdk = 35
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

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
                version = "2.0"
            )
        )
        apiVersion.set(
            org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(
                version = "2.0"
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

            export(Deps.ArkIvanov.Decompose.decompose)
            export(Deps.ArkIvanov.Essenty.lifecycle)
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
                with(Deps.Io.Ktor) {
                    api(ktorClientCore)
                    api(ktorSerializationKotlinxJson)
                    api(ktorClientContentNegotiation)
                    api(ktorClientLogging)
                }

                // Logback for ktor logging
                implementation(Deps.Logback.logbackClassic)

                // SqlDelight
                with(Deps.CashApp.SQLDelight) {
                    api(coroutinesExtensions)
                    api(primitiveAdapters)
                }

                // Koin
                with(Deps.Koin) {
                    api(core)
                    api(test)
                }

                // KotlinX Serialization Json
                implementation(Deps.Org.JetBrains.Kotlinx.kotlinxSerializationJson)

                // Coroutines
                implementation(Deps.Org.JetBrains.Kotlinx.coroutinesCore)

                // MVIKotlin
                with(Deps.ArkIvanov.MVIKotlin) {
                    api(mvikotlin)
                    api(mvikotlinMain)
                    api(mvikotlinExtensionsCoroutines)
                }

                // Decompose
                with(Deps.ArkIvanov.Decompose) {
                    api(decompose)
                    api(extensionsCompose)
                }

                // Image Loading
                api(Deps.Github.imageLoader)
                implementation(Deps.ArkIvanov.Essenty.lifecycle)
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
                implementation(Deps.Io.Ktor.ktorClientAndroid)

                // SqlDelight
                implementation(Deps.CashApp.SQLDelight.androidDriver)

                // Koin
                implementation(Deps.Koin.android)
            }
        }
        val androidUnitTest by getting

        val desktopMain by getting {
            dependsOn(commonMain)

            dependencies {
                // Ktor
                implementation(Deps.Io.Ktor.ktorClientJava)

                // SqlDelight
                implementation(Deps.CashApp.SQLDelight.sqliteDriver)
            }
        }

        val iosMain by creating {
            dependsOn(commonMain)

            dependencies {
                // Ktor
                implementation(Deps.Io.Ktor.ktorClientDarwin)

                // SqlDelight
                implementation(Deps.CashApp.SQLDelight.nativeDriver)
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

object Versions {
    const val gradlePlugin = "7.3.0"
    const val composeCompiler = "1.5.6-dev-k2.0.0-Beta1-06a03be2b42"
    const val coroutines = "1.8.0-RC"
    const val serialization = "1.6.2"
    const val mviKotlin = "3.3.0"
    const val decompose = "3.0.0-alpha03"
    const val essenty = "2.0.0-alpha02"
    const val ktor = "2.3.7"
    const val sqlDelight = "2.0.0"
    const val koin = "3.5.3"
    const val imageLoader = "1.2.10"
    const val logbackClassic = "1.2.11"
    const val kotlinWrappersBom = "1.0.0-pre.510"
}

object Deps {
    object Org {
        object JetBrains {
            object Kotlinx {
                const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"

                const val kotlinxSerializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}"
            }

            object KotlinWrappers {
                const val kotlinWrappersBom = "org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:${Versions.kotlinWrappersBom}"
                const val kotlinStyled = "org.jetbrains.kotlin-wrappers:kotlin-styled"
            }
        }
    }

    object Io {
        object Ktor {
            const val ktorClientCore = "io.ktor:ktor-client-core:${Versions.ktor}"
            const val ktorSerializationKotlinxJson = "io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}"
            const val ktorClientContentNegotiation = "io.ktor:ktor-client-content-negotiation:${Versions.ktor}"
            const val ktorClientLogging = "io.ktor:ktor-client-logging:${Versions.ktor}"

            // Engines
            const val ktorClientAndroid = "io.ktor:ktor-client-android:${Versions.ktor}"
            const val ktorClientDarwin = "io.ktor:ktor-client-darwin:${Versions.ktor}"
            const val ktorClientJava = "io.ktor:ktor-client-java:${Versions.ktor}"
            const val ktorClientJs = "io.ktor:ktor-client-js:${Versions.ktor}"
        }
    }

    object Logback {
        const val logbackClassic = "ch.qos.logback:logback-classic:${Versions.logbackClassic}"
    }

    object CashApp {
        object SQLDelight {
            const val gradlePlugin = "app.cash.sqldelight:gradle-plugin:${Versions.sqlDelight}"
            const val androidDriver = "app.cash.sqldelight:android-driver:${Versions.sqlDelight}"
            const val sqliteDriver = "app.cash.sqldelight:sqlite-driver:${Versions.sqlDelight}"
            const val nativeDriver = "app.cash.sqldelight:native-driver:${Versions.sqlDelight}"
            const val sqljsDriver = "app.cash.sqldelight:sqljs-driver:${Versions.sqlDelight}"

            const val coroutinesExtensions = "app.cash.sqldelight:coroutines-extensions:${Versions.sqlDelight}"
            const val primitiveAdapters = "app.cash.sqldelight:primitive-adapters:${Versions.sqlDelight}"
        }
    }

    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val test = "io.insert-koin:koin-test:${Versions.koin}"
        const val android = "io.insert-koin:koin-android:${Versions.koin}"
    }

    object ArkIvanov {
        object MVIKotlin {
            const val mvikotlin = "com.arkivanov.mvikotlin:mvikotlin:${Versions.mviKotlin}"
            const val mvikotlinMain = "com.arkivanov.mvikotlin:mvikotlin-main:${Versions.mviKotlin}"
            const val mvikotlinExtensionsCoroutines = "com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:${Versions.mviKotlin}"
        }

        object Decompose {
            const val decompose = "com.arkivanov.decompose:decompose:${Versions.decompose}"
            const val extensionsCompose = "com.arkivanov.decompose:extensions-compose:${Versions.decompose}"
        }

        object Essenty {
            const val lifecycle = "com.arkivanov.essenty:lifecycle:${Versions.essenty}"
        }
    }

    object Github {
        const val imageLoader = "io.github.qdsfdhvh:image-loader:${Versions.imageLoader}"
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
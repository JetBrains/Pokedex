pluginManagement {
    @Suppress("LocalVariableName")
    val kotlin_version: String? by settings
    @Suppress("LocalVariableName")
    val kotlin_repo_url: String? by settings

    repositories {
        google()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        mavenCentral()
        kotlin_repo_url?.also { maven(it) }
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }

    plugins {
        val kotlinVersion = kotlin_version ?: "1.9.21"
        val agpVersion = extra["agp.version"] as String
        val composeVersion = extra["composeMpp.version"] as String
        val sqldelightVersion = extra["sqldelight.version"] as String

        kotlin("jvm").version(kotlinVersion)
        kotlin("multiplatform").version(kotlinVersion)
        kotlin("android").version(kotlinVersion)
        kotlin("plugin.serialization").version(kotlinVersion)
        id("com.android.base").version(agpVersion)
        id("com.android.application").version(agpVersion)
        id("com.android.library").version(agpVersion)
        id("org.jetbrains.compose").version(composeVersion)
        id("org.jetbrains.compose").version(composeVersion)
        id("app.cash.sqldelight").version(sqldelightVersion)
        id("org.jetbrains.kotlin.plugin.compose").version(kotlinVersion)
    }
}

dependencyResolutionManagement {
    @Suppress("LocalVariableName")
    val kotlin_repo_url: String? by settings

    val composeCompilerCustomRepo =
        extra.properties["compose.compiler.customRepositoryUrl"] as? String

    repositories {
        google()
        mavenCentral()
        kotlin_repo_url?.also { maven(it) }
        composeCompilerCustomRepo?.also { maven(it) }
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }
}

rootProject.name = "Pokedex"
include(":android")
include(":desktop")
include(":shared")

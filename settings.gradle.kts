pluginManagement {
    // Resolve Kotlin version from dot-notation property only
    val kotlinVersion = providers.gradleProperty("kotlin.version").orNull ?: "2.4.21"

    // Ensure Kotlin/Native bundle version matches Kotlin plugin version
    val kotlinNativeVersion = providers.gradleProperty("kotlin.native.version").orNull ?: kotlinVersion
    if (kotlinNativeVersion != kotlinVersion) {
        throw GradleException(
            "Kotlin mismatch: kotlin.version=$kotlinVersion vs kotlin.native.version=$kotlinNativeVersion. Align them (TeamCity should pass the same value)."
        )
    }

    // Optional custom repo for Kotlin dev/experimental builds
    val kotlinRepo = providers.gradleProperty("kotlin.repo").orNull

    repositories {
        google()
        gradlePluginPortal()
        maven("https://packages.jetbrains.team/maven/p/cmp/dev")
        mavenCentral()
        kotlinRepo?.takeIf { it.isNotBlank() }?.let { maven(it) }
        maven("https://packages.jetbrains.team/maven/p/kt/dev/")
    }

    plugins {
        println("Final kotlin version = $kotlinVersion")

        val agpVersion = extra["agp.version"] as String
        val composeVersion = extra["composeMpp.version"] as String
        val sqldelightVersion = extra["sqldelight.version"] as String

        kotlin("jvm").version(kotlinVersion)
        kotlin("multiplatform").version(kotlinVersion)
        kotlin("plugin.serialization").version(kotlinVersion)
        id("com.android.base").version(agpVersion)
        id("com.android.application").version(agpVersion)
        id("com.android.kotlin.multiplatform.library").version(agpVersion)
        id("org.jetbrains.compose").version(composeVersion)
        id("app.cash.sqldelight").version(sqldelightVersion)
        id("org.jetbrains.kotlin.plugin.compose").version(kotlinVersion)
    }
}

dependencyResolutionManagement {
    // Optional custom repo for Kotlin dev/experimental builds
    val kotlinRepo = providers.gradleProperty("kotlin.repo").orNull

    val composeCompilerCustomRepo =
        extra.properties["compose.compiler.customRepositoryUrl"] as? String

    repositories {
        google()
        mavenCentral()
        kotlinRepo?.takeIf { it.isNotBlank() }?.let { maven(it) }
        composeCompilerCustomRepo?.takeIf { it.isNotBlank() }?.let { maven(it) }
        maven("https://packages.jetbrains.team/maven/p/kt/dev/")
    }
}

rootProject.name = "Pokedex"
include(":androidApp")
include(":desktopApp")
include(":shared")

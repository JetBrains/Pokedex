
plugins {
    id("com.android.application")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
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

    androidTarget ()

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
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
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
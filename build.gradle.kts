plugins {
    //trick: for the same plugin versions in all sub-modules
    kotlin("multiplatform") apply false
    kotlin("jvm") apply false
    kotlin("plugin.serialization") apply false
    id("app.cash.sqldelight") apply false
    id("com.android.application") apply false
    id("com.android.kotlin.multiplatform.library") apply false
    id("org.jetbrains.compose") apply false
    id("org.jetbrains.kotlin.plugin.compose") apply false
}

allprojects {
    val kotlinRepo: String? = extra.properties["kotlin.repo"] as? String

    val composeCompilerCustomRepo =
        extra.properties["compose.compiler.customRepositoryUrl"] as? String

    repositories {
        google()
        mavenCentral()
        maven("https://packages.jetbrains.team/maven/p/cmp/dev")
        maven("https://androidx.dev/storage/compose-compiler/repository/")
        kotlinRepo?.takeIf { it.isNotBlank() }?.let { maven(it) }
        composeCompilerCustomRepo?.takeIf { it.isNotBlank() }?.let { maven(it) }
        maven("https://packages.jetbrains.team/maven/p/kt/dev/")
    }
}

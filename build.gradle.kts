plugins {
    //trick: for the same plugin versions in all sub-modules
    kotlin("android") version "1.9.21" apply false
    kotlin("multiplatform") version "1.9.21" apply false
    kotlin("plugin.serialization") version "1.9.21" apply false
    id("app.cash.sqldelight") version "2.0.0" apply false
    id("com.android.application") version "7.3.0" apply false
    id("com.android.library") version "7.3.0" apply false
    id("org.jetbrains.compose") version "1.5.11" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

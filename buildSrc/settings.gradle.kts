pluginManagement {

    @Suppress("LocalVariableName")
    val kotlin_version: String? by settings

    println("buildsrc kotlin_version = $kotlin_version")
    println("buildsrc community.project.kotlin.version = ${extra.properties["community.project.kotlin.version"]}")
    val kotlinVersion = extra.properties["community.project.kotlin.version"] as? String
        ?: kotlin_version
        ?: "2.0.0"
    println("buildsrc Final kotlin version = $kotlinVersion")

    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        }
        maven {
            url = uri("https://packages.jetbrains.team/maven/p/ui/dev")
        }
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("org.jetbrains.kotlin")) {
                useVersion(kotlinVersion)
            }
        }
    }
}
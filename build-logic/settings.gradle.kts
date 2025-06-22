@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }

        create("ktorLibs") {
            from("io.ktor:ktor-version-catalog:3.2.0")
        }
    }
}

rootProject.name = "build-logic"
include(":convention")

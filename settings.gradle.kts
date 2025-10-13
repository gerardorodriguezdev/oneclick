rootProject.name = "OneClick"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        maven("https://packages.confluent.io/maven/")
    }

    versionCatalogs {
        create("ktorLibs") {
            from("io.ktor:ktor-version-catalog:3.3.1")
        }
    }
}

include(
    "client:app",
    "client:shared:di",
    "client:shared:ui",
    "client:shared:navigation",
    "client:shared:network",
    "client:shared:notifications",
    "client:features:home",
)

include(
    "shared:logging",
    "shared:dispatchers",
    "shared:timeProvider",
    "shared:network",
    "shared:contracts:core",
    "shared:contracts:auth",
    "shared:contracts:homes",
)

include(
    "server:services:app",
    "server:services:mock",
    "server:shared:core",
    "server:shared:auth",
    "server:shared:db",
)

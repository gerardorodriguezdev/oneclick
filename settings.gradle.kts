rootProject.name = "TheOneClick"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")

    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        maven("https://packages.confluent.io/maven/")
    }

    versionCatalogs {
        create("ktorLibs") {
            from("io.ktor:ktor-version-catalog:3.2.0")
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
    "shared:contracts:core",
)

include(
    "server:services:auth",
    "server:services:homes",
    "server:mock",
    "server:shared",
)

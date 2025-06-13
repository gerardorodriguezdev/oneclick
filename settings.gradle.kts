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
    "server:app",
    "server:mock",
    "server:shared",
)
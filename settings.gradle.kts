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
    }
}

include(
    "server:app",
    "server:mock",
    "server:shared",
)

include(
    "client:app",
    "client:shared",
)

include(
    "shared:base",
    "shared:testing",
    "shared:dispatchers",
    "shared:timeProvider",
)

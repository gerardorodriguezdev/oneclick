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
    "server:core",
    "server:mock",
    "server:shared",
)

include(
    "client:core",
    "client:shared",
)

include(
    "shared:core",
    "shared:testing",
    "shared:dispatchers",
    "shared:timeProvider",
)

include(
    "rules:provider",
    "rules:models",
)

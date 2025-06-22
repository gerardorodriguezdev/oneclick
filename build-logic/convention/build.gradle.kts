plugins {
    `kotlin-dsl`
    alias(libs.plugins.kmp.serialization)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvm.api.get().toInt()))
    }
}

dependencies {
    implementation(libs.gradle.kmp.api)
    implementation(libs.gradle.jvm.kotlin.api)
    implementation(libs.gradle.kmp.ktor)
    implementation(libs.gradle.kmp.compose)
    implementation(libs.gradle.android.application)
    implementation(libs.gradle.android.library)
    implementation(libs.gradle.chamaleon)
    implementation(libs.gradle.kmp.serialization)
    implementation(libs.gradle.jvm.chamaleon)

    testImplementation(libs.kmp.test)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }

    test {
        useJUnitPlatform()
    }
}

gradlePlugin {
    plugins {
        register("jvm-server") {
            id = "theoneclick.jvm.server"
            implementationClass = "buildLogic.convention.plugins.JvmServerPlugin"
        }

        register("wasm-website") {
            id = "theoneclick.wasm.website"
            implementationClass = "buildLogic.convention.plugins.WasmWebsitePlugin"
        }

        register("android-app") {
            id = "theoneclick.android.app"
            implementationClass = "buildLogic.convention.plugins.AndroidAppPlugin"
        }

        register("jvm-library") {
            id = "theoneclick.jvm.library"
            implementationClass = "buildLogic.convention.plugins.JvmLibraryPlugin"
        }

        register("wasm-library") {
            id = "theoneclick.wasm.library"
            implementationClass = "buildLogic.convention.plugins.WasmLibraryPlugin"
        }

        register("android-library") {
            id = "theoneclick.android.library"
            implementationClass = "buildLogic.convention.plugins.AndroidLibraryPlugin"
        }
    }
}

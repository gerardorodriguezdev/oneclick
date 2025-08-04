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
    implementation(libs.gradle.kmp.kotlin)
    implementation(libs.gradle.jvm.kotlin)
    implementation(libs.gradle.ktor)
    implementation(libs.gradle.compose)
    implementation(libs.gradle.android.application)
    implementation(libs.gradle.android.library)
    implementation(libs.gradle.chamaleon)
    implementation(libs.gradle.serialization)
    implementation(libs.gradle.docker.compose)
    implementation(libs.jvm.chamaleon)

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

        register("ios-app") {
            id = "theoneclick.ios.app"
            implementationClass = "buildLogic.convention.plugins.IOSAppPlugin"
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

        register("ios-library") {
            id = "theoneclick.ios.library"
            implementationClass = "buildLogic.convention.plugins.IOSLibraryPlugin"
        }
    }
}

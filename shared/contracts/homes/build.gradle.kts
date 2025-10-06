@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("oneclick.android.library")
    id("oneclick.jvm.library")
    id("oneclick.wasm.library")
    id("oneclick.ios.library")
    alias(libs.plugins.kmp.serialization)
    alias(libs.plugins.kmp.poko)
}

androidLibrary {
    namespace = "oneclick.shared.contracts.homes"
    compileSdkVersion = libs.versions.android.api.get().toInt()
    minSdkVersion = libs.versions.android.api.get().toInt()
    composeEnabled = false
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(ktorLibs.serialization.kotlinx.json)
                implementation(projects.shared.contracts.core)
                runtimeOnly(libs.kmp.poko)
            }
        }
    }
}

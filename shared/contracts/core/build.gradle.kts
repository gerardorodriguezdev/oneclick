@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("theoneclick.android.library")
    id("theoneclick.jvm.library")
    id("theoneclick.wasm.library")
    id("theoneclick.ios.library")
    alias(libs.plugins.kmp.serialization)
    alias(libs.plugins.kmp.poko)
}

androidLibrary {
    namespace = "theoneclick.shared.contracts.core"
    compileSdkVersion = libs.versions.android.api.get().toInt()
    minSdkVersion = libs.versions.android.api.get().toInt()
    composeEnabled = false
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(ktorLibs.serialization.kotlinx.json)
                runtimeOnly(libs.kmp.poko)
            }
        }
    }
}

@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("theoneclick.android.library")
    id("theoneclick.jvm.library")
    id("theoneclick.wasm.library")
    id("theoneclick.ios.library")
    alias(libs.plugins.kmp.serialization)
}

androidLibrary {
    namespace.set("theoneclick.shared.logging")
    compileSdkVersion.set(libs.versions.android.api.get().toInt())
    minSdkVersion.set(libs.versions.android.api.get().toInt())
    composeEnabled.set(false)
}

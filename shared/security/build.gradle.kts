@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("oneclick.android.library")
    id("oneclick.jvm.library")
}

androidLibrary {
    namespace = "oneclick.shared.security"
    compileSdkVersion = libs.versions.android.api.get().toInt()
    minSdkVersion = libs.versions.android.api.get().toInt()
    composeEnabled = false
}

kotlin {
    sourceSets {
        androidMain {
            dependsOn(jvmMain.get())
        }
    }
}
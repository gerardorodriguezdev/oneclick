plugins {
    id("theoneclick.wasm.library")
    id("theoneclick.android.library")
    alias(libs.plugins.kmp.compose.compiler)
    alias(libs.plugins.kmp.compose.jetbrains)
    alias(libs.plugins.kmp.serialization)
    alias(libs.plugins.ksp)
}

androidLibrary {
    namespace.set("theoneclick.client.feature.home")
    compileSdkVersion.set(libs.versions.android.api.get().toInt())
    minSdkVersion.set(libs.versions.android.api.get().toInt())
    composeEnabled.set(true)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.kmp.coroutines)
                implementation(libs.kmp.navigation)
                implementation(libs.kmp.viewModel)
                implementation(libs.kmp.ktor.serialization.kotlinx.json)
                implementation(libs.kmp.ktor.client.core)
                implementation(libs.kmp.immutable)
                implementation(libs.kmp.kotlin.inject)
                implementation(libs.kmp.kotlin.inject.kmp)
                implementation(projects.shared.core)
                implementation(projects.shared.dispatchers)
                implementation(projects.shared.timeProvider)
                implementation(projects.client.shared.di)
                implementation(projects.client.shared.network)
                implementation(projects.client.shared.navigation)
                implementation(projects.client.shared.ui)
                implementation(projects.client.shared.notifications)

                project.dependencies {
                    kspCommonMainMetadata(libs.ksp.kotlin.inject)
                    kspAndroid(libs.ksp.kotlin.inject)
                    kspWasmJs(libs.ksp.kotlin.inject)
                }
            }
        }
    }
}
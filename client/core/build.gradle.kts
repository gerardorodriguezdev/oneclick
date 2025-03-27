import com.codingfeline.buildkonfig.compiler.FieldSpec
import io.github.gerardorodriguezdev.chamaleon.gradle.plugin.extensions.ChamaleonExtension
import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    id("theoneclick.wasm.website")
    id("theoneclick.android.app")
    alias(libs.plugins.kmp.compose.compiler)
    alias(libs.plugins.kmp.compose.jetbrains)
    alias(libs.plugins.kmp.serialization)
    alias(libs.plugins.kmp.atomicfu)
    alias(libs.plugins.kmp.build.config)
    alias(libs.plugins.chamaleon)
}

wasmWebsite {
    outputFileName.set("theoneclick.js")
}

androidApp {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())

    namespace.set("theoneclick")
    compileSdkVersion.set(libs.versions.android.api.get().toInt())

    applicationId.set("org.theoneclick")
    minSdkVersion.set(libs.versions.android.api.get().toInt())
    targetSdkVersion.set(libs.versions.android.api.get().toInt())
    versionCode.set(1)
    versionName.set("1.0")

    storeFile.set(file("local/keystore.jks"))
    storePassword.set(chamaleon.androidProviderString("KEYSTORE_PASSWORD"))
    keyAlias.set(chamaleon.androidProviderString("KEY_ALIAS"))
    keyPassword.set(chamaleon.androidProviderString("KEY_PASSWORD"))

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
                implementation(libs.kmp.ktor.client.auth)
                implementation(libs.kmp.navigation)
                implementation(libs.kmp.viewModel)
                implementation(libs.kmp.ktor.serialization.kotlinx.json)
                implementation(libs.kmp.ktor.client.core)
                implementation(libs.kmp.ktor.client.content.negotiation)
                implementation(libs.kmp.ktor.client.logging)
                implementation(libs.kmp.koin.core)
                implementation(libs.kmp.koin.compose)
                implementation(libs.kmp.koin.core.viewmodel)
                implementation(libs.kmp.koin.compose.viewmodel)
                implementation(libs.kmp.datetime)
                implementation(libs.kmp.immutable)
                implementation(libs.kmp.window.classes)
                implementation(projects.shared.core)
                implementation(projects.shared.dispatchers)
                implementation(projects.shared.timeProvider)
                implementation(projects.rules.models)
                api(libs.kmp.atomicfu)
            }
        }

        @OptIn(ExperimentalComposeLibrary::class)
        commonTest {
            dependencies {
                implementation(libs.kmp.test)
                implementation(compose.uiTest)
                implementation(libs.kmp.test.koin)
                implementation(libs.kmp.test.ktor.client.mock)
                implementation(libs.kmp.test.turbine)
                implementation(projects.shared.testing)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.android.activity)
                implementation(libs.android.datastore)
                implementation(compose.preview)
                implementation(libs.jvm.ktor.client.okhttp)
            }
        }

        androidInstrumentedTest {
            dependencies {
                implementation(libs.android.test.junit)
                implementation(libs.android.test.navigation)
                implementation(libs.kmp.test.turbine)
            }

            project.dependencies {
                debugImplementation(libs.android.test.leak.canary)
                debugImplementation(compose.uiTooling)
                debugImplementation(libs.android.test.manifest)
            }
        }

        wasmJsTest {
            dependencies {
                implementation(libs.kmp.test.turbine)
            }
        }
    }
}

buildkonfig {
    packageName = "theoneclick.client.core.buildkonfig"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, name = "PROTOCOL", value = null, nullable = true)
        buildConfigField(FieldSpec.Type.STRING, name = "HOST", value = null, nullable = true)
        buildConfigField(FieldSpec.Type.INT, name = "PORT", value = null, nullable = true)
        buildConfigField(FieldSpec.Type.BOOLEAN, name = "IS_DEBUG", value = "false")
    }

    targetConfigs {
        create("wasmJs") {
            buildConfigField(FieldSpec.Type.STRING, name = "PROTOCOL", value = null, nullable = true)
            buildConfigField(FieldSpec.Type.STRING, name = "HOST", value = null, nullable = true)
            buildConfigField(FieldSpec.Type.INT, name = "PORT", value = null, nullable = true)
            buildConfigField(
                FieldSpec.Type.BOOLEAN,
                name = "IS_DEBUG",
                value = chamaleon.selectedEnvironment().wasmPlatform.propertyBooleanValue("IS_DEBUG").toString()
            )
        }

        create("android") {
            buildConfigField(
                FieldSpec.Type.STRING,
                name = "PROTOCOL",
                value = chamaleon.androidProviderString("PROTOCOL").get(),
                nullable = true
            )
            buildConfigField(
                FieldSpec.Type.STRING,
                name = "HOST",
                value = chamaleon.androidProviderString("HOST").get(),
                nullable = true
            )
            buildConfigField(
                FieldSpec.Type.INT,
                name = "PORT",
                value = chamaleon.androidProviderString("PORT").get(),
                nullable = true
            )
            buildConfigField(
                FieldSpec.Type.BOOLEAN,
                name = "IS_DEBUG",
                value = chamaleon.selectedEnvironment().androidPlatform.propertyBooleanValue("IS_DEBUG").toString()
            )
        }
    }
}

fun ChamaleonExtension.androidProviderString(name: String): Provider<String> =
    provider { chamaleon.selectedEnvironment().androidPlatform.propertyStringValue(name) }
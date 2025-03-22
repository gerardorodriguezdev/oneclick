plugins {
    id("theoneclick.android.library")
    id("theoneclick.jvm.library")
    id("theoneclick.wasm.library")
    alias(libs.plugins.kmp.serialization)
}

jvmLibrary {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())
}

androidLibrary {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())
    namespace.set("theoneclick.shared.core")
    compileSdkVersion.set(libs.versions.android.api.get().toInt())
    minSdkVersion.set(libs.versions.android.api.get().toInt())
    composeEnabled.set(false)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kmp.ktor.serialization.kotlinx.json)
                implementation(libs.kmp.ktor.client.core)
                implementation(libs.kmp.datetime)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kmp.test)
                implementation(projects.shared.testing)
            }
        }
    }
}

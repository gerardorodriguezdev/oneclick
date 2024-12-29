plugins {
    id("theoneclick.android.library")
    id("theoneclick.jvm.library")
    id("theoneclick.wasm.library")
}

jvmLibrary {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())
}

androidLibrary {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())
    namespace.set("theoneclick.rules.models")
    compileSdkVersion.set(libs.versions.android.api.get().toInt())
    minSdkVersion.set(libs.versions.android.api.get().toInt())
    composeEnabled.set(false)
}

plugins {
    id("theoneclick.wasm.library")
    id("theoneclick.android.library")
}

androidLibrary {
    namespace.set("theoneclick.client.shared.di")
    compileSdkVersion.set(libs.versions.android.api.get().toInt())
    minSdkVersion.set(libs.versions.android.api.get().toInt())
    composeEnabled.set(false)
}
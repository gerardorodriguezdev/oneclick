plugins {
    id("oneclick.android.library")
    id("oneclick.jvm.library")
    id("oneclick.wasm.library")
    id("oneclick.ios.library")
}

androidLibrary {
    namespace = "oneclick.shared.timeProvider"
    compileSdkVersion = libs.versions.android.api.get().toInt()
    minSdkVersion = libs.versions.android.api.get().toInt()
    composeEnabled = false
}

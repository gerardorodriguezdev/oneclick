plugins {
    id("theoneclick.jvm.library")
}

jvmLibrary {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())
}

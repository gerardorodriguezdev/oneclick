plugins {
    id("oneclick.jvm.app")
    alias(libs.plugins.kmp.chamaleon)
}

jvmApp {
    jvmTarget = libs.versions.jvm.api.get().toInt()
    mainClass = "oneclick.client.app.home.ApplicationKt"
}

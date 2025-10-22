plugins {
    id("oneclick.jvm.app")
    alias(libs.plugins.kmp.chamaleon)
}

jvmApp {
    jvmTarget = libs.versions.jvm.api.get().toInt()
    mainClass = "oneclick.client.app.home.ApplicationKt"
}

kotlin {
    dependencies  {
        implementation(ktorLibs.client.core)
        implementation(libs.kmp.coroutines)
        implementation(projects.shared.contracts.core)
        implementation(projects.shared.contracts.auth)
        implementation(projects.shared.contracts.homes)
        implementation(projects.shared.dispatchers)
        implementation(projects.shared.logging)
        implementation(projects.shared.network)
    }
}

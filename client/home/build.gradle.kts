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
        implementation(projects.shared.contracts.core)
        implementation(projects.shared.contracts.homes)
        implementation(libs.kmp.coroutines)
    }
}

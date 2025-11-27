plugins {
    id("oneclick.jvm.library")
}

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                implementation(libs.jvm.email)
                implementation(libs.jvm.email.activation)
                implementation(libs.kmp.coroutines)
                implementation(projects.shared.logging)
                implementation(projects.shared.dispatchers)
            }
        }
    }
}

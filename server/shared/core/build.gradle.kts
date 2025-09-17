plugins {
    id("theoneclick.jvm.library")
}

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                implementation(ktorLibs.server.core)
                implementation(projects.shared.contracts.core)
            }
        }
    }
}

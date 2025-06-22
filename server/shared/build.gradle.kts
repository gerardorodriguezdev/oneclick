plugins {
    id("theoneclick.jvm.library")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(ktorLibs.server.core)
                implementation(projects.shared.contracts.core)
            }
        }
    }
}

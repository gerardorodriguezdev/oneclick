plugins {
    id("theoneclick.jvm.library")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kmp.ktor.server.core)
                implementation(projects.shared.contracts.core)
            }
        }
    }
}

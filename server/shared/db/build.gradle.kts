plugins {
    id("oneclick.jvm.library")
}

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                implementation(libs.jvm.hiraki)
                implementation(libs.jvm.postgresql)
                implementation(libs.kmp.sqldelight)
            }
        }
    }
}

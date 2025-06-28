plugins {
    id("theoneclick.jvm.library")
    alias(libs.plugins.kmp.atomicfu)
    alias(libs.plugins.kmp.sqldelight)
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                implementation(ktorLibs.server.core)
                implementation(ktorLibs.serialization.kotlinx.json)
                implementation(ktorLibs.server.auth)
                implementation(ktorLibs.server.contentNegotiation)
                implementation(ktorLibs.server.callLogging)
                implementation(ktorLibs.server.requestValidation)
                implementation(ktorLibs.server.statusPages)
                implementation(ktorLibs.server.rateLimit)
                implementation(ktorLibs.server.callId)
                implementation(ktorLibs.server.compression)
                implementation(libs.kmp.kotlin.inject)
                implementation(libs.kmp.sqldelight)
                implementation(libs.kmp.sqldelight.coroutines)
                implementation(libs.jvm.bcrypt)
                implementation(libs.jvm.redis)
                implementation(projects.shared.contracts.core)
                implementation(projects.shared.timeProvider)
                implementation(projects.shared.dispatchers)

                project.dependencies.ksp(libs.ksp.kotlin.inject)
            }
        }
    }
}

//TODO: Separate
sqldelight {
    databases {
        create("UsersDatabase") {
            packageName.set("theoneclick.server.shared.postgresql")
            dialect("app.cash.sqldelight:postgresql-dialect:2.1.0")
        }
    }
}

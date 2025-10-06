plugins {
    alias(libs.plugins.kmp.kotlin) apply false
    alias(libs.plugins.kmp.build.config) apply false
    alias(libs.plugins.kmp.serialization) apply false
    alias(libs.plugins.kmp.sqldelight) apply false
    alias(libs.plugins.kmp.poko) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kmp.ksp) apply false
    alias(libs.plugins.kmp.jib) apply false
}
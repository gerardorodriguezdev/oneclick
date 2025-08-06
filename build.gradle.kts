import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.kmp.kotlin) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kmp.build.config) apply false
    alias(libs.plugins.gradle.jib) apply false
    alias(libs.plugins.gradle.detekt)
}

detekt {
    buildUponDefaultConfig = true
    allRules = true
    config.from("detekt.yml")
    autoCorrect = true
}

dependencies {
    detektPlugins(libs.gradle.detekt.formatting)
    detektPlugins(libs.gradle.detekt.compose)
}

tasks.withType<Detekt> detekt@{
    setSource(files(project.projectDir))
    exclude("**/build/**")
}

import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.kmp.kotlin) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kmp.build.config) apply false
    alias(libs.plugins.detekt)
}

detekt {
    buildUponDefaultConfig = true
    allRules = true
    config.from("detekt.yml")
}

dependencies {
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.compose)
    detektPlugins(projects.rules.provider)
}

tasks.withType<Detekt> detekt@{
    setSource(files(project.projectDir))
    exclude("**/build/**")
}

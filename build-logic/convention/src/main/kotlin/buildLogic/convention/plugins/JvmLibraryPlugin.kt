package buildLogic.convention.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

class JvmLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugins()
            configureKotlinMultiplatformExtension()
        }
    }

    private fun Project.applyPlugins() {
        pluginManager.apply {
            apply(KotlinMultiplatformPluginWrapper::class.java)
        }
    }

    private fun Project.configureKotlinMultiplatformExtension() {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            jvm()
        }
    }
}

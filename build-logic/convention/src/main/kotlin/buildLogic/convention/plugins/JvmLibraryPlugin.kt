package buildLogic.convention.plugins

import buildLogic.convention.extensions.plugins.JvmLibraryExtension
import buildLogic.convention.extensions.toJavaLanguageVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

class JvmLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugins()
            val jvmLibraryExtension = createJvmLibraryExtension()
            configureKotlinMultiplatformExtension(jvmLibraryExtension)
        }
    }

    private fun Project.applyPlugins() {
        pluginManager.apply {
            apply(KotlinMultiplatformPluginWrapper::class.java)
        }
    }

    private fun Project.createJvmLibraryExtension(): JvmLibraryExtension {
        val extension = extensions.create(JVM_LIBRARY_EXTENSION_NAME, JvmLibraryExtension::class.java)
        return extension
    }

    private fun Project.configureKotlinMultiplatformExtension(jvmLibraryExtension: JvmLibraryExtension) {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            compilerOptions {
                extraWarnings.set(true)
            }

            jvmToolchain {
                languageVersion.set(jvmLibraryExtension.jvmTarget.toJavaLanguageVersion())
            }

            jvm()
        }
    }

    private companion object {
        const val JVM_LIBRARY_EXTENSION_NAME = "jvmLibrary"
    }
}

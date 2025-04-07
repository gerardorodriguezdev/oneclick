package buildLogic.convention.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

class WasmLibraryPlugin : Plugin<Project> {
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

    @OptIn(ExperimentalWasmDsl::class)
    private fun Project.configureKotlinMultiplatformExtension() {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            compilerOptions {
                extraWarnings.set(true)
            }

            wasmJs {
                browser()
            }
        }
    }
}

package buildLogic.convention.plugins

import buildLogic.convention.extensions.plugins.WasmWebsiteExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

class WasmWebsitePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            applyPlugins()
            val wasmWebsiteExtension = createWasmWebsiteExtension()
            configureKotlinMultiplatformExtension(wasmWebsiteExtension)
        }
    }

    private fun Project.applyPlugins() {
        pluginManager.apply {
            apply(KotlinMultiplatformPluginWrapper::class.java)
        }
    }

    private fun Project.createWasmWebsiteExtension(): WasmWebsiteExtension {
        val extension = extensions.create(WASM_WEBSITE_EXTENSION_NAME, WasmWebsiteExtension::class.java)
        return extension
    }

    @OptIn(ExperimentalWasmDsl::class)
    private fun Project.configureKotlinMultiplatformExtension(wasmWebsiteExtension: WasmWebsiteExtension) {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            compilerOptions {
                extraWarnings.set(true)
                freeCompilerArgs.add("-Xwhen-guards")
            }

            wasmJs {
                browser {
                    commonWebpackConfig {
                        outputFileName = wasmWebsiteExtension.outputFileName.get()
                    }

                    testTask {
                        useKarma {
                            useChrome()
                        }
                    }
                }
                binaries.executable()
            }
        }
    }

    private companion object {
        const val WASM_WEBSITE_EXTENSION_NAME = "wasmWebsite"
    }
}

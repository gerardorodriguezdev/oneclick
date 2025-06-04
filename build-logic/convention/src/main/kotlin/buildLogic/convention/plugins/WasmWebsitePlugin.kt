package buildLogic.convention.plugins

import buildLogic.convention.extensions.plugins.WasmWebsiteExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

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
        val rootDirPath = rootDir.path
        val projectDirPath = projectDir.path

        extensions.configure(KotlinMultiplatformExtension::class.java) {
            compilerOptions {
                extraWarnings.set(true)
            }

            wasmJs {
                browser {
                    commonWebpackConfig {
                        this.configDirectory
                        outputFileName = wasmWebsiteExtension.outputFileName.get()
                        devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                            static = (static ?: mutableListOf()).apply {
                                add(rootDirPath)
                                add(projectDirPath)
                            }

                            port = wasmWebsiteExtension.webpackPort.get()

                            val webpackProxy = wasmWebsiteExtension.webpackProxy.orNull
                            webpackProxy?.let {
                                proxy = mutableListOf(
                                    KotlinWebpackConfig.DevServer.Proxy(
                                        context = webpackProxy.context,
                                        target = webpackProxy.target,
                                    )
                                )
                            }
                        }
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

package buildLogic.convention.plugins

import buildLogic.convention.extensions.plugins.WasmWebsiteExtension
import buildLogic.convention.tasks.CreateWebpackConfigTask
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
            registerTasks(wasmWebsiteExtension)
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
        val configDir = project.layout.buildDirectory.dir(WEBPACK_CONFIG_DIRECTORY_NAME).get().asFile
        val outputFileName = "${project.name}.[contenthash].js"

        extensions.configure(KotlinMultiplatformExtension::class.java) {
            compilerOptions {
                extraWarnings.set(true)
            }

            wasmJs {
                browser {
                    commonWebpackConfig {
                        configDirectory = configDir
                        this.outputFileName = outputFileName
                        devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                            static = (static ?: mutableListOf()).apply {
                                add(rootDirPath)
                                add(projectDirPath)
                            }

                            port = wasmWebsiteExtension.webpackConfiguration.port.get()

                            val webpackProxy = wasmWebsiteExtension.webpackConfiguration.proxy.orNull
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

    private fun Project.registerTasks(wasmWebsiteExtension: WasmWebsiteExtension) {
        val webpackConfigTaskOutputFile =
            layout.buildDirectory.file("$WEBPACK_CONFIG_DIRECTORY_NAME/$WEBPACK_CONFIG_FILE_NAME")
        val webpackConfigTask = tasks.register(CREATE_WEBPACK_CONFIG_TASK_NAME, CreateWebpackConfigTask::class.java) {
            ignoredFiles.set(wasmWebsiteExtension.webpackConfiguration.ignoredFiles)
            outputFile.set(webpackConfigTaskOutputFile)
        }

        tasks.named(JS_PACKAGE_JSON_TASK_NAME) {
            dependsOn(webpackConfigTask)
        }

        val wasmWebsiteConsumerConfiguration = configurations.create(WASM_WEBSITE_CONSUMER_CONFIGURATION_NAME) {
            isCanBeConsumed = true
            isCanBeResolved = false
        }

        val wasmDistributionTask = tasks.named(WASM_DISTRIBUTION_TASK_NAME)
        artifacts {
            add(
                wasmWebsiteConsumerConfiguration.name,
                wasmDistributionTask.map { it.outputs.files.singleFile }
            ) {
                builtBy(wasmDistributionTask)
            }
        }
    }

    companion object {
        private const val WEBPACK_CONFIG_DIRECTORY_NAME = "webpackConfigurations"
        private const val WEBPACK_CONFIG_FILE_NAME = "WasmWebsitePluginConfig.js"

        private const val CREATE_WEBPACK_CONFIG_TASK_NAME = "createWebpackConfigFile"
        private const val JS_PACKAGE_JSON_TASK_NAME = "wasmJsPackageJson"
        private const val WASM_DISTRIBUTION_TASK_NAME = "wasmJsBrowserDistribution"

        private const val WASM_WEBSITE_EXTENSION_NAME = "wasmWebsite"

        const val WASM_WEBSITE_CONSUMER_CONFIGURATION_NAME = "wasmWebsiteConsumer"
    }
}

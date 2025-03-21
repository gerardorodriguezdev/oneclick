package buildLogic.convention.plugins

import buildLogic.convention.extensions.plugins.AndroidLibraryExtension
import buildLogic.convention.extensions.toJavaVersion
import buildLogic.convention.extensions.toJvmTarget
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

class AndroidLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            applyPlugins()
            val androidLibraryExtension = createAndroidLibraryExtension()
            configureKotlinMultiplatformExtension(androidLibraryExtension)
            configureLibraryExtension(androidLibraryExtension)
        }
    }

    private fun Project.applyPlugins() {
        pluginManager.apply {
            apply(KotlinMultiplatformPluginWrapper::class.java)
            apply(LibraryPlugin::class.java)
        }
    }

    private fun Project.createAndroidLibraryExtension(): AndroidLibraryExtension {
        val extension = extensions.create(ANDROID_LIBRARY_EXTENSION_NAME, AndroidLibraryExtension::class.java)
        return extension
    }

    private fun Project.configureLibraryExtension(androidLibraryExtension: AndroidLibraryExtension) {
        extensions.configure(AndroidComponentsExtension::class.java) {
            finalizeDsl {
                extensions.configure(LibraryExtension::class.java) {
                    namespace = androidLibraryExtension.namespace.get()
                    compileSdk = androidLibraryExtension.compileSdkVersion.get()

                    defaultConfig {
                        minSdk = androidLibraryExtension.minSdkVersion.get()
                    }

                    compileOptions {
                        sourceCompatibility = androidLibraryExtension.jvmTarget.get().toJavaVersion()
                        targetCompatibility = androidLibraryExtension.jvmTarget.get().toJavaVersion()
                    }

                    buildFeatures {
                        compose = androidLibraryExtension.composeEnabled.get()
                    }
                }
            }
        }
    }

    private fun Project.configureKotlinMultiplatformExtension(androidLibraryExtension: AndroidLibraryExtension) {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            compilerOptions {
                extraWarnings.set(true)
                freeCompilerArgs.add("-Xwhen-guards")
            }

            androidTarget {
                compilerOptions {
                    jvmTarget.set(androidLibraryExtension.jvmTarget.toJvmTarget())
                }
            }
        }
    }

    private companion object {
        const val ANDROID_LIBRARY_EXTENSION_NAME = "androidLibrary"
    }
}

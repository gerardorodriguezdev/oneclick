package buildLogic.convention.plugins

import buildLogic.convention.extensions.plugins.AndroidAppExtension
import buildLogic.convention.extensions.toJavaVersion
import buildLogic.convention.extensions.toJvmTarget
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

class AndroidAppPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            applyPlugins()
            val androidAppExtension = createAndroidAppExtension()
            configureKotlinMultiplatformExtension(androidAppExtension)
            configureAppExtension(androidAppExtension)
        }
    }

    private fun Project.applyPlugins() {
        pluginManager.apply {
            apply(KotlinMultiplatformPluginWrapper::class.java)
            apply(AppPlugin::class.java)
        }
    }

    private fun Project.createAndroidAppExtension(): AndroidAppExtension {
        val extension = extensions.create(ANDROID_APP_EXTENSION_NAME, AndroidAppExtension::class.java)
        return extension
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    private fun Project.configureKotlinMultiplatformExtension(androidAppExtension: AndroidAppExtension) {
        extensions.configure(KotlinMultiplatformExtension::class.java) {
            compilerOptions {
                extraWarnings.set(true)
                freeCompilerArgs.add("-Xwhen-guards")
            }

            androidTarget {
                compilerOptions {
                    jvmTarget.set(androidAppExtension.jvmTarget.toJvmTarget())
                }

                unitTestVariant {
                    sourceSetTree.set(KotlinSourceSetTree.test)
                }

                instrumentedTestVariant {
                    sourceSetTree.set(KotlinSourceSetTree.test)
                }
            }
        }
    }

    private fun Project.configureAppExtension(androidAppExtension: AndroidAppExtension) {
        extensions.configure(AndroidComponentsExtension::class.java) {
            finalizeDsl {
                extensions.configure(ApplicationExtension::class.java) {
                    namespace = androidAppExtension.namespace.get()
                    compileSdk = androidAppExtension.compileSdkVersion.get()

                    val mainSourceSet = sourceSets["main"]
                    with(mainSourceSet) {
                        manifest.srcFile("src/androidMain/AndroidManifest.xml")
                        res.srcDirs("src/androidMain/res")
                        resources.srcDirs("src/commonMain/resources")
                    }

                    defaultConfig {
                        applicationId = androidAppExtension.applicationId.get()
                        minSdk = androidAppExtension.minSdkVersion.get()
                        targetSdk = androidAppExtension.targetSdkVersion.get()
                        versionCode = androidAppExtension.versionCode.get()
                        versionName = androidAppExtension.versionName.get()
                        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    }

                    signingConfigs {
                        create("release") {
                            storeFile = androidAppExtension.storeFile.get()
                            storePassword = androidAppExtension.storePassword.get()
                            keyAlias = androidAppExtension.keyAlias.get()
                            keyPassword = androidAppExtension.keyPassword.get()
                        }
                    }

                    buildTypes {
                        getByName("release") {
                            isMinifyEnabled = true
                            isShrinkResources = true
                            signingConfig = signingConfigs.getByName("release")
                            proguardFiles(
                                getDefaultProguardFile("proguard-android-optimize.txt"),
                                "src/androidMain/proguard-rules.pro"
                            )
                        }
                    }

                    compileOptions {
                        sourceCompatibility = androidAppExtension.jvmTarget.get().toJavaVersion()
                        targetCompatibility = androidAppExtension.jvmTarget.get().toJavaVersion()
                    }

                    buildFeatures {
                        compose = androidAppExtension.composeEnabled.get()
                    }
                }
            }
        }
    }

    private companion object {
        const val ANDROID_APP_EXTENSION_NAME = "androidApp"
    }
}

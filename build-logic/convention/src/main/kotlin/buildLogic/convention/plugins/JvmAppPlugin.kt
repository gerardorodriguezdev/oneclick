package buildLogic.convention.plugins

import buildLogic.convention.extensions.baseDockerImage
import buildLogic.convention.extensions.fullNameDockerImage
import buildLogic.convention.extensions.plugins.JvmAppExtension
import buildLogic.convention.extensions.toJavaLanguageVersion
import buildLogic.convention.extensions.toMap
import com.google.cloud.tools.jib.gradle.JibExtension
import com.google.cloud.tools.jib.gradle.JibPlugin
import com.google.cloud.tools.jib.gradle.JibTask
import io.github.gerardorodriguezdev.chamaleon.gradle.plugin.ChamaleonGradlePlugin
import io.github.gerardorodriguezdev.chamaleon.gradle.plugin.extensions.ChamaleonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

class JvmAppPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            applyPlugins()
            val chamaleonExtension = extensions.findByType(ChamaleonExtension::class.java)
            val jvmAppExtension = createJvmAppExtension()
            configureJavaApplicationExtension(jvmAppExtension)
            configureKotlinExtension(jvmAppExtension)
            configureJibExtension(jvmAppExtension)
            registerTasks(chamaleonExtension)
        }
    }

    private fun Project.applyPlugins() {
        pluginManager.apply {
            apply(KotlinPluginWrapper::class.java)
            apply(ApplicationPlugin::class.java)
            apply(ChamaleonGradlePlugin::class.java)
            apply(JibPlugin::class.java)
        }
    }

    private fun Project.createJvmAppExtension(): JvmAppExtension {
        val extension = extensions.create(JVM_APP_EXTENSION_NAME, JvmAppExtension::class.java)
        return extension
    }

    private fun Project.configureJavaApplicationExtension(jvmAppExtension: JvmAppExtension) {
        extensions.configure(JavaApplication::class.java) {
            mainClass.set(jvmAppExtension.mainClass)
        }
    }

    private fun Project.configureKotlinExtension(jvmAppExtension: JvmAppExtension) {
        extensions.configure(KotlinJvmProjectExtension::class.java) {
            compilerOptions {
                extraWarnings.set(true)
            }

            jvmToolchain {
                languageVersion.set(jvmAppExtension.jvmTarget.toJavaLanguageVersion())
            }
        }
    }

    private fun Project.configureJibExtension(jvmAppExtension: JvmAppExtension) {
        tasks.withType(JibTask::class.java).configureEach {
            notCompatibleWithConfigurationCache(
                "JIB plugin is not compatible with the configuration cache. " +
                        "See https://github.com/GoogleContainerTools/jib/issues/3132"
            )
        }

        afterEvaluate {
            extensions.configure(JibExtension::class.java) {
                dockerClient {
                    executable = jvmAppExtension.dockerConfiguration.executablePath.get()
                }

                container {
                    setFormat("Docker")
                }

                from {
                    val jvmTarget = jvmAppExtension.jvmTarget.get()
                    image = baseDockerImage(jvmTarget)
                }

                to {
                    setImage(
                        fullNameDockerImage(
                            imageRegistryUrl = jvmAppExtension.dockerConfiguration.registryUrl,
                            imageName = jvmAppExtension.dockerConfiguration.name,
                            identifier = provider { "latest" },
                        )
                    )

                    auth {
                        setUsername(jvmAppExtension.dockerConfiguration.registryUsername)
                        setPassword(jvmAppExtension.dockerConfiguration.registryPassword)
                    }

                    setTags(
                        provider {
                            setOf(
                                jvmAppExtension.dockerConfiguration.tag.get(),
                                "latest",
                            )
                        }
                    )
                }
            }
        }
    }

    private fun Project.registerTasks(chamaleonExtension: ChamaleonExtension?) {
        val environmentVariablesProvider = provider { chamaleonExtension.toMap() }
        tasks.named<JavaExec>(RUN_TASK_NAME) {
            standardInput = System.`in`

            dependsOn(tasks.named<Jar>(JVM_JAR_TASK_NAME))
            classpath(tasks.named<Jar>(JVM_JAR_TASK_NAME))

            environmentVariablesProvider.get().forEach { (key, value) ->
                environment(key, value)
            }
        }

        tasks.register(PUBLISH_IMAGE_TASK_NAME) {
            dependsOn(JibPlugin.BUILD_IMAGE_TASK_NAME)
        }
    }

    private companion object {
        const val RUN_TASK_NAME = "run"
        const val PUBLISH_IMAGE_TASK_NAME = "publishImage"
        const val JVM_JAR_TASK_NAME = "jar"
        const val JVM_APP_EXTENSION_NAME = "jvmApp"
    }
}

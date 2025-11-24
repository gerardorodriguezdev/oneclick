package buildLogic.convention.plugins

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
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
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
            registerTasks(jvmAppExtension = jvmAppExtension, chamaleonExtension = chamaleonExtension)
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
                    image = "eclipse-temurin:${jvmAppExtension.jvmTarget.get()}-jre"
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

    private fun Project.registerTasks(jvmAppExtension: JvmAppExtension, chamaleonExtension: ChamaleonExtension?) {
        val environmentVariablesProvider = provider { chamaleonExtension.toMap() }
        tasks.named<JavaExec>(RUN_TASK_NAME) {
            standardInput = System.`in`

            dependsOn(tasks.named<Jar>(JVM_JAR_TASK_NAME))
            classpath(tasks.named<Jar>(JVM_JAR_TASK_NAME))

            environmentVariablesProvider.get().forEach { (key, value) ->
                environment(key, value)
            }
        }

        val jibBuildTarTask = tasks.named(JibPlugin.BUILD_DOCKER_TASK_NAME)
        tasks.register<Exec>(RUN_DOCKER_TASK_NAME) {
            dependsOn(jibBuildTarTask)

            standardInput = System.`in`
            standardOutput = System.out
            errorOutput = System.err

            commandLine(
                "bash",
                "-c",
                runDockerCommand(
                    environmentVariables = environmentVariablesProvider.get(),
                    port = jvmAppExtension.dockerConfiguration.port.get().toString(),
                    fullNameDockerImage = fullNameDockerImage(
                        imageRegistryUrl = jvmAppExtension.dockerConfiguration.registryUrl,
                        imageName = jvmAppExtension.dockerConfiguration.name,
                        identifier = provider { "latest" },
                    ).get(),
                )
            )
        }

        tasks.register(PUBLISH_IMAGE_TASK_NAME) {
            dependsOn(JibPlugin.BUILD_IMAGE_TASK_NAME)
        }
    }

    private fun runDockerCommand(
        environmentVariables: Map<String, String>,
        port: String,
        fullNameDockerImage: String,
    ): String {
        val environmentVariables = environmentVariables.entries
        val environmentVariablesStrings = environmentVariables.map { (key, value) -> "$key=$value" }
        val environmentVariablesString =
            environmentVariablesStrings.joinToString(prefix = " ", separator = " ", transform = { "-e $it" })

        return "docker run -i -p $port:$port$environmentVariablesString $fullNameDockerImage"
    }

    private companion object {
        const val RUN_TASK_NAME = "run"
        const val RUN_DOCKER_TASK_NAME = "runDocker"
        const val PUBLISH_IMAGE_TASK_NAME = "publishImage"
        const val JVM_JAR_TASK_NAME = "jar"
        const val JVM_APP_EXTENSION_NAME = "jvmApp"
    }
}

package buildLogic.convention.plugins

import buildLogic.convention.extensions.plugins.JvmServerExtension
import buildLogic.convention.extensions.toJavaVersion
import io.github.gerardorodriguezdev.chamaleon.gradle.plugin.ChamaleonGradlePlugin
import io.github.gerardorodriguezdev.chamaleon.gradle.plugin.extensions.ChamaleonExtension
import io.ktor.plugin.*
import io.ktor.plugin.features.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

class JvmServerPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            applyPlugins()
            val chamaleonExtension = extensions.findByType(ChamaleonExtension::class.java)
            val jvmServerExtension = createJvmServerExtension()
            configureJavaApplicationExtension(chamaleonExtension, jvmServerExtension)
            configureKtorExtension(chamaleonExtension, jvmServerExtension)
            registerTasks(chamaleonExtension)
        }
    }

    private fun Project.applyPlugins() {
        pluginManager.apply {
            apply(KtorGradlePlugin::class.java)
            apply(KotlinPluginWrapper::class.java)
            apply(ChamaleonGradlePlugin::class.java)
        }
    }

    private fun Project.createJvmServerExtension(): JvmServerExtension {
        val extension = extensions.create(JVM_SERVER_EXTENSION_NAME, JvmServerExtension::class.java)
        return extension
    }

    private fun Project.configureJavaApplicationExtension(
        chamaleonExtension: ChamaleonExtension?,
        jvmServerExtension: JvmServerExtension,
    ) {
        extensions.configure(JavaApplication::class.java) {
            mainClass.set(jvmServerExtension.mainClass)

            val isDebug =
                chamaleonExtension?.selectedEnvironmentOrNull()?.jvmPlatformOrNull?.propertyBooleanValueOrNull(
                    "IS_DEBUG"
                )
            if (isDebug == true) {
                applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
            }
        }
    }

    private fun Project.configureKtorExtension(
        chamaleonExtension: ChamaleonExtension?,
        jvmServerExtension: JvmServerExtension
    ) {
        project.ktorExtensions.configure(DockerExtension::class.java) {
            localImageName.set(jvmServerExtension.dockerConfiguration.imageName)
            jreVersion.set(jvmServerExtension.jvmTarget.toJavaVersion())
            imageTag.set(jvmServerExtension.dockerConfiguration.imageTag)
            externalRegistry.set(
                DockerImageRegistry.externalRegistry(
                    project = externalRegistryProject(
                        imageRegistryUrl = jvmServerExtension.dockerConfiguration.imageRegistryUrl,
                        imageName = jvmServerExtension.dockerConfiguration.imageName,
                        imageTag = jvmServerExtension.dockerConfiguration.imageTag,
                    ),
                    username = jvmServerExtension.dockerConfiguration.imageRegistryUsername,
                    password = jvmServerExtension.dockerConfiguration.imageRegistryPassword,
                )
            )

            chamaleonExtension.toMap().forEach { (key, value) ->
                environmentVariables.add(
                    DockerEnvironmentVariable(
                        variable = key,
                        value = value,
                    )
                )
            }
        }
    }

    private fun Project.registerTasks(chamaleonExtension: ChamaleonExtension?) {
        tasks.named(BUILD_DOCKER_IMAGE_TASK_NAME) {
            dependsOn(tasks.named(TESTS_TASK_NAME))
        }

        tasks.named<JavaExec>(RUN_TASK_NAME) {
            dependsOn(tasks.named<Jar>(JVM_JAR_TASK_NAME))
            classpath(tasks.named<Jar>(JVM_JAR_TASK_NAME))

            chamaleonExtension.toMap().forEach { (key, value) ->
                environment(key, value)
            }
        }
    }

    private fun ChamaleonExtension?.toMap(): List<Pair<String, String>> {
        if (this == null) return emptyList()

        val properties = selectedEnvironmentOrNull()?.jvmPlatformOrNull?.properties?.values ?: return emptyList()

        return properties.map { property -> property.name.value to property.value.toString() }
    }

    private fun Project.externalRegistryProject(
        imageRegistryUrl: Provider<String>,
        imageName: Provider<String>,
        imageTag: Provider<String>,
    ): Provider<String> =
        provider { "${imageRegistryUrl.get()}/${imageName.get()}:${imageTag.get()}" }

    private companion object {
        const val BUILD_DOCKER_IMAGE_TASK_NAME = "buildImage"
        const val TESTS_TASK_NAME = "test"
        const val RUN_TASK_NAME = "run"
        const val JVM_JAR_TASK_NAME = "jar"

        const val JVM_SERVER_EXTENSION_NAME = "jvmServer"
    }
}

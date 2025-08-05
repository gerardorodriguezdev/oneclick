package buildLogic.convention.plugins

import buildLogic.convention.extensions.plugins.JvmServerExtension
import buildLogic.convention.extensions.toJavaLanguageVersion
import buildLogic.convention.extensions.toJavaVersion
import buildLogic.convention.tasks.createDockerComposeConfigTask.CreateDockerComposeConfigInput
import buildLogic.convention.tasks.createDockerComposeConfigTask.CreateDockerComposeConfigInput.App
import buildLogic.convention.tasks.createDockerComposeConfigTask.CreateDockerComposeConfigTask
import com.avast.gradle.dockercompose.ComposeExtension
import com.avast.gradle.dockercompose.DockerComposePlugin
import io.github.gerardorodriguezdev.chamaleon.gradle.plugin.ChamaleonGradlePlugin
import io.github.gerardorodriguezdev.chamaleon.gradle.plugin.extensions.ChamaleonExtension
import io.ktor.plugin.*
import io.ktor.plugin.features.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

class JvmServerPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            applyPlugins()
            val chamaleonExtension = extensions.findByType(ChamaleonExtension::class.java)
            val jvmServerExtension = createJvmServerExtension()
            configureJavaApplicationExtension(jvmServerExtension)
            configureKtorExtension(chamaleonExtension, jvmServerExtension)
            configureKotlinExtension(jvmServerExtension)
            configureDockerComposeExtension(jvmServerExtension)
            registerTasks(jvmServerExtension, chamaleonExtension)
        }
    }

    private fun Project.applyPlugins() {
        pluginManager.apply {
            apply(KtorGradlePlugin::class.java)
            apply(KotlinPluginWrapper::class.java)
            apply(ChamaleonGradlePlugin::class.java)
            apply(DockerComposePlugin::class.java)
        }
    }

    private fun Project.createJvmServerExtension(): JvmServerExtension {
        val extension = extensions.create(JVM_SERVER_EXTENSION_NAME, JvmServerExtension::class.java)
        return extension
    }

    private fun Project.configureJavaApplicationExtension(jvmServerExtension: JvmServerExtension) {
        extensions.configure(JavaApplication::class.java) {
            mainClass.set(jvmServerExtension.mainClass)
        }
    }

    private fun Project.configureKtorExtension(
        chamaleonExtension: ChamaleonExtension?,
        jvmServerExtension: JvmServerExtension
    ) {
        val ktorExtension = extensions.getByType(KtorExtension::class.java)
        val ktorExtensions = (ktorExtension as ExtensionAware).extensions
        val dockerImageRegistry = DockerImageRegistry.externalRegistry(
            project = externalRegistryProject(
                imageRegistryUrl = jvmServerExtension.dockerConfiguration.imageRegistryUrl,
                imageName = jvmServerExtension.dockerConfiguration.imageName,
                imageTag = jvmServerExtension.dockerConfiguration.imageTag,
            ),
            username = jvmServerExtension.dockerConfiguration.imageRegistryUsername,
            password = jvmServerExtension.dockerConfiguration.imageRegistryPassword,
        )
        val environmentVariablesProvider = provider { chamaleonExtension.toMap() }

        ktorExtensions.configure(DockerExtension::class.java) {
            localImageName.set(jvmServerExtension.dockerConfiguration.imageName)
            jreVersion.set(jvmServerExtension.jvmTarget.toJavaVersion())
            imageTag.set(jvmServerExtension.dockerConfiguration.imageTag)
            externalRegistry.set(dockerImageRegistry)

            environmentVariablesProvider.get().forEach { (key, value) ->
                environmentVariables.add(
                    DockerEnvironmentVariable(
                        variable = key,
                        value = value,
                    )
                )
            }
        }
    }

    private fun Project.configureKotlinExtension(jvmServerExtension: JvmServerExtension) {
        extensions.configure(KotlinJvmProjectExtension::class.java) {
            compilerOptions {
                extraWarnings.set(true)
            }

            jvmToolchain {
                languageVersion.set(jvmServerExtension.jvmTarget.toJavaLanguageVersion())
            }
        }
    }

    private fun Project.configureDockerComposeExtension(jvmServerExtension: JvmServerExtension) {
        val dockerComposeFileNameString = dockerComposeFileNameString()
        extensions.configure(ComposeExtension::class.java) {
            useComposeFiles.add(dockerComposeFileNameString)
            executable.set(jvmServerExtension.dockerComposeConfiguration.dockerComposeExecutablePath)
            dockerExecutable.set(jvmServerExtension.dockerComposeConfiguration.dockerExecutablePath)
        }
    }

    private fun Project.registerTasks(jvmServerExtension: JvmServerExtension, chamaleonExtension: ChamaleonExtension?) {
        val environmentVariablesProvider = provider { chamaleonExtension.toMap() }
        tasks.named<JavaExec>(RUN_TASK_NAME) {
            dependsOn(tasks.named<Jar>(JVM_JAR_TASK_NAME))
            classpath(tasks.named<Jar>(JVM_JAR_TASK_NAME))

            environmentVariablesProvider.get().forEach { (key, value) ->
                environment(key, value)
            }
        }

        val dockerComposeFileName = dockerComposeFileName()
        val createDockerComposeConfigTask =
            tasks.register<CreateDockerComposeConfigTask>(CREATE_DOCKER_COMPOSE_TASK_NAME) {
                input.set(
                    CreateDockerComposeConfigInput(
                        app = App(
                            imageName = jvmServerExtension.dockerConfiguration.imageName.get(),
                            imageTag = jvmServerExtension.dockerConfiguration.imageTag.get(),
                            imagePort = jvmServerExtension.dockerConfiguration.imagePort.get(),
                            environmentVariables = environmentVariablesProvider.get(),
                        ),
                        postgresDatabase = jvmServerExtension.dockerComposeConfiguration.postgresDatabase.orNull,
                        redisDatabase = jvmServerExtension.dockerComposeConfiguration.redisDatabase.orNull,
                    )
                )

                outputFile.set(dockerComposeFileName)
            }

        val buildImageTask = tasks.named(BUILD_DOCKER_IMAGE_TASK_NAME)
        val loadImageTask = tasks.register<Exec>(LOAD_DOCKER_IMAGE_TASK_NAME) {
            dependsOn(buildImageTask)
            dependsOn(createDockerComposeConfigTask)

            commandLine("bash", "-c", "docker load < build/jib-image.tar")
        }

        tasks.named(COMPOSE_UP_TASK_NAME) {
            dependsOn(loadImageTask)
        }
    }

    private fun ChamaleonExtension?.toMap(): Map<String, String> =
        if (this == null) emptyMap() else buildMap {
            selectedEnvironmentOrNull()?.jvmPlatformOrNull?.properties?.forEach { (key, value) ->
                value.value?.toString()?.let { valueString ->
                    put(key, valueString)
                }
            }
        }

    private fun Project.externalRegistryProject(
        imageRegistryUrl: Provider<String>,
        imageName: Provider<String>,
        imageTag: Provider<String>,
    ): Provider<String> =
        provider { "${imageRegistryUrl.get()}/${imageName.get()}:${imageTag.get()}" }

    private fun Project.dockerComposeFileName(): Provider<RegularFile> =
        project.layout.buildDirectory.file("$DOCKER_COMPOSE_DIRECTORY_NAME/$DOCKER_COMPOSE_FILE_NAME")

    private fun Project.dockerComposeFileNameString(): Provider<String> =
        dockerComposeFileName()
            .map { dockerComposeFile ->
                val dockerComposeFilePath = dockerComposeFile.asFile.toPath()
                project.layout.projectDirectory.asFile.toPath().relativize(dockerComposeFilePath).toString()
            }

    private companion object {
        const val BUILD_DOCKER_IMAGE_TASK_NAME = "buildImage"
        const val LOAD_DOCKER_IMAGE_TASK_NAME = "loadImage"
        const val COMPOSE_UP_TASK_NAME = "composeUp"
        const val RUN_TASK_NAME = "run"
        const val JVM_JAR_TASK_NAME = "jar"
        const val CREATE_DOCKER_COMPOSE_TASK_NAME = "createDockerCompose"

        const val DOCKER_COMPOSE_DIRECTORY_NAME = "dockerCompose"
        const val DOCKER_COMPOSE_FILE_NAME = "docker-compose.yml"

        const val JVM_SERVER_EXTENSION_NAME = "jvmServer"
    }
}

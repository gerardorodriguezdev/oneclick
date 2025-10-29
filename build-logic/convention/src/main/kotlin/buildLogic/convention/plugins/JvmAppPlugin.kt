package buildLogic.convention.plugins

import buildLogic.convention.extensions.plugins.JvmAppExtension
import buildLogic.convention.extensions.toJavaLanguageVersion
import buildLogic.convention.extensions.toMap
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
            registerTasks(chamaleonExtension)
        }
    }

    private fun Project.applyPlugins() {
        pluginManager.apply {
            apply(KotlinPluginWrapper::class.java)
            apply(ApplicationPlugin::class.java)
            apply(ChamaleonGradlePlugin::class.java)
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
    }

    private companion object Companion {
        const val RUN_TASK_NAME = "run"
        const val JVM_JAR_TASK_NAME = "jar"
        const val JVM_APP_EXTENSION_NAME = "jvmApp"
    }
}

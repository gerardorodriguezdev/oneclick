package buildLogic.convention.plugins

import buildLogic.convention.extensions.plugins.LocalRunEnvironmentExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

class LocalRunEnvironmentPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            val propertiesMap = localPropertiesMap()
            addEnvironmentVariables(propertiesMap)
            createLocalRunEnvironmentExtension(propertiesMap)
        }
    }

    private fun Project.localPropertiesMap(): Map<String, String> {
        val propertiesFile = file(LOCAL_ENVIRONMENT_PROPERTIES_PATH)
        val properties = Properties()
        val propertiesMap = hashMapOf<String, String>()

        if (propertiesFile.exists()) {
            propertiesFile.inputStream().use { inputStream ->
                properties.load(inputStream)
            }
        }

        properties.forEach { (key, value) ->
            propertiesMap[key.toString()] = value.toString()
        }

        return propertiesMap
    }

    private fun Project.addEnvironmentVariables(propertiesMap: Map<String, String>) {
        propertiesMap.forEach { (key, value) ->
            if (System.getenv(key) == null) {
                extensions.extraProperties[key] = value
            }
        }
    }

    private fun Project.createLocalRunEnvironmentExtension(propertiesMap: Map<String, String>) {
        val extension =
            extensions.create(LOCAL_RUN_ENVIRONMENT_EXTENSION_NAME, LocalRunEnvironmentExtension::class.java)
        extension.propertiesMap.set(propertiesMap)
    }

    private companion object {
        const val LOCAL_RUN_ENVIRONMENT_EXTENSION_NAME = "localRunEnvironment"
        const val LOCAL_ENVIRONMENT_PROPERTIES_PATH = "local/environment.properties"
    }
}

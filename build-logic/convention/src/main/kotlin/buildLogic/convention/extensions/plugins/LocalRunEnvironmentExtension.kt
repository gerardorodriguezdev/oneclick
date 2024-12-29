package buildLogic.convention.extensions.plugins

import org.gradle.api.provider.MapProperty

interface LocalRunEnvironmentExtension {
    val propertiesMap: MapProperty<String, String>
}

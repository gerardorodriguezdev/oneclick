package buildLogic.convention.models

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

interface DockerComposeConfiguration {
    val executablePath: Property<String>
    val imagesConfigurations: ListProperty<ImageConfiguration>
}
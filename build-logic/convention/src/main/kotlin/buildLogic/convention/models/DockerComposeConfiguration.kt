package buildLogic.convention.models

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

interface DockerComposeConfiguration {
    val dockerExecutablePath: Property<String>
    val dockerComposeExecutablePath: Property<String>
    val imagesConfigurations: ListProperty<ImageConfiguration>
}
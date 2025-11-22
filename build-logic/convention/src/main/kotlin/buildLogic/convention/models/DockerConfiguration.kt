package buildLogic.convention.models

import org.gradle.api.provider.Property

interface DockerConfiguration {
    val imagePort: Property<Int>
    val imageName: Property<String>
    val imageTag: Property<String>
    val imageRegistryUrl: Property<String>
    val imageRegistryUsername: Property<String>
    val imageRegistryPassword: Property<String>
}
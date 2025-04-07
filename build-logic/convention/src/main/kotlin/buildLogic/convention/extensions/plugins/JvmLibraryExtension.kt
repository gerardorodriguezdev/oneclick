package buildLogic.convention.extensions.plugins

import org.gradle.api.provider.Property

interface JvmLibraryExtension {
    val jvmTarget: Property<Int>
}

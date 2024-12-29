package buildLogic.convention.extensions.plugins

import org.gradle.api.provider.Property

interface AndroidLibraryExtension {
    val jvmTarget: Property<Int>
    val namespace: Property<String>
    val compileSdkVersion: Property<Int>
    val minSdkVersion: Property<Int>
    val composeEnabled: Property<Boolean>
}

package buildLogic.convention.extensions.plugins

import org.gradle.api.provider.Property
import java.io.File

interface AndroidAppExtension {
    val jvmTarget: Property<Int>
    val namespace: Property<String>
    val compileSdkVersion: Property<Int>
    val applicationId: Property<String>
    val minSdkVersion: Property<Int>
    val targetSdkVersion: Property<Int>
    val versionCode: Property<Int>
    val versionName: Property<String>
    val composeEnabled: Property<Boolean>
    val storeFile: Property<File>
    val storePassword: Property<String>
    val keyAlias: Property<String>
    val keyPassword: Property<String>
}

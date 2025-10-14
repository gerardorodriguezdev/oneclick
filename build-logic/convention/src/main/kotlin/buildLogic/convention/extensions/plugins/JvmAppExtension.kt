package buildLogic.convention.extensions.plugins

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class JvmAppExtension @Inject constructor(objects: ObjectFactory) {
    val jvmTarget: Property<Int> = objects.property(Int::class.java)
    val mainClass: Property<String> = objects.property(String::class.java)
}

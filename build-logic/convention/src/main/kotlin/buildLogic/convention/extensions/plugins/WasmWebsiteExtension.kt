package buildLogic.convention.extensions.plugins

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

open class WasmWebsiteExtension @Inject constructor(objects: ObjectFactory) {
    val outputFileName: Property<String> = objects.property(String::class.java)
    val webpackConfiguration: WebpackConfiguration = objects.newInstance(WebpackConfiguration::class)

    fun webpackConfiguration(configure: WebpackConfiguration.() -> Unit) {
        webpackConfiguration.configure()
    }

    interface WebpackConfiguration {
        val port: Property<Int>
        val proxy: Property<Proxy>
        val ignoredFiles: ListProperty<String>

        data class Proxy(
            val context: MutableList<String>,
            val target: String,
        )
    }
}

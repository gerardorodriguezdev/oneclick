package buildLogic.convention.extensions.plugins

import org.gradle.api.provider.Property

interface WasmWebsiteExtension {
    val outputFileName: Property<String>
    val webpackPort: Property<Int>
    val webpackProxy: Property<Proxy>

    data class Proxy(
        val context: MutableList<String>,
        val target: String,
    )
}

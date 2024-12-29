package buildLogic.convention.extensions.plugins

import org.gradle.api.provider.Property

interface WasmWebsiteExtension {
    val outputFileName: Property<String>
}

package buildLogic.convention.extensions

import buildLogic.convention.plugins.WasmWebsitePlugin
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project

fun DependencyHandlerScope.consumeWasmWebsite(projectPath: String) {
    add(
        WasmWebsitePlugin.WASM_WEBSITE_CONSUMER_CONFIGURATION_NAME,
        project(projectPath, WasmWebsitePlugin.WASM_WEBSITE_CONSUMER_CONFIGURATION_NAME)
    )
}
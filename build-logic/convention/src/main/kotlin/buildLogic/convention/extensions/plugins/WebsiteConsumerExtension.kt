package buildLogic.convention.extensions.plugins

import org.gradle.api.provider.ListProperty

interface WebsiteConsumerExtension {
    val projectPaths: ListProperty<String>

    operator fun invoke(vararg projectPaths: String) {
        this.projectPaths.set(projectPaths.toList())
    }

    companion object {
        internal const val WEBSITE_CONFIGURATION = "WebsiteConfiguration"
        internal const val WEBSITE_CONSUMER_CONFIGURATION = "WebsiteConsumerConfiguration"
    }
}

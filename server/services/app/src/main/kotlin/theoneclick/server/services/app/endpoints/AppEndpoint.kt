package theoneclick.server.services.app.endpoints

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.routing.*
import theoneclick.server.services.app.endpoints.AppEndpointConstants.fileNameContainsHashRegex

internal fun Route.appEndpoint() {
    install(CachingHeaders)

    staticResources("/", "static") {
        modify { resource, call ->
            val fileName = resource.file.substringAfterLast('/')
            val shouldCacheFile = fileName.matches(fileNameContainsHashRegex)

            if (shouldCacheFile) {
                call.caching = CachingOptions(
                    CacheControl.MaxAge(
                        maxAgeSeconds = Int.MAX_VALUE,
                        visibility = CacheControl.Visibility.Public
                    )
                )
            }
        }
    }
}

private object AppEndpointConstants {
    val fileNameContainsHashRegex = Regex(
        ".*[a-f0-9]{8,20}.*",
        RegexOption.IGNORE_CASE
    )
}
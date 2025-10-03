package theoneclick.server.services.app.endpoints

import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import theoneclick.server.services.app.endpoints.AppEndpointConstants.fileNameContainsHashRegex

internal fun Route.appEndpoint() {
    staticResources("/", "static") {
        preCompressed(CompressedFileType.BROTLI)

        cacheControl { resource ->
            val fileName = resource.file.substringAfterLast('/')
            val shouldCacheFile = fileName.matches(fileNameContainsHashRegex)

            if (shouldCacheFile) {
                listOf(
                    CacheControl.MaxAge(
                        visibility = CacheControl.Visibility.Public,
                        maxAgeSeconds = Int.MAX_VALUE
                    )
                )
            } else {
                emptyList()
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
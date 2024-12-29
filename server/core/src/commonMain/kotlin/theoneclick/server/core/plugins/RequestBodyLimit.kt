package theoneclick.server.core.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.*

fun Application.configureRequestBodyLimit() {
    install(RequestBodyLimit)
}

@Suppress("ThrowExpression")
private val RequestBodyLimit = createApplicationPlugin("RequestBodyLimit", ::RequestBodyLimitConfiguration) {
    val bodyLimit = pluginConfig.bodyLimit

    onCall { call ->
        val contentLength = call.request.contentLength()
        if (contentLength != null && contentLength > bodyLimit) throw PayloadTooLargeException(bodyLimit)
    }
}

private data class RequestBodyLimitConfiguration(
    var bodyLimit: Int = DEFAULT_BODY_LIMIT,
) {
    companion object {
        const val DEFAULT_BODY_LIMIT = 2048
    }
}

private class PayloadTooLargeException(bodyLimit: Int) :
    ContentTransformationException("Request is larger than the limit of $bodyLimit bytes")

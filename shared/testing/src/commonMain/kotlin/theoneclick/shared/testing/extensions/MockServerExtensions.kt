package theoneclick.shared.testing.extensions

import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.serialization.json.Json

fun mockEngine(
    pathToFake: String,
    onPathFound: MockRequestHandleScope.(request: HttpRequestData) -> HttpResponseData,
): MockEngine =
    MockEngine { request ->
        if (request.url.fullPath == pathToFake) {
            onPathFound(request)
        } else {
            respondError(HttpStatusCode.NotFound)
        }
    }

inline fun <reified T> MockRequestHandleScope.respondJson(data: T): HttpResponseData =
    respond(
        content = Json.encodeToString<T>(data),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json"),
    )

inline fun <reified T> HttpRequestData.toRequestBodyObject(): T? {
    val requestBody = body
    val requestBodyTextContent = (requestBody as? TextContent) ?: return null
    val requestBodyString = requestBodyTextContent.text
    return Json.decodeFromString<T>(requestBodyString)
}

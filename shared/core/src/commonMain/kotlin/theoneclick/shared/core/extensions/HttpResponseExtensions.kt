package theoneclick.shared.core.extensions

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders

val HttpResponse.rawCurrentUrl: String? get() = headers[HttpHeaders.Location]
package theoneclick.server.services.app.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*

internal fun Application.configureCompression(baseUrl: String) {
    install(Compression) {
        gzip {
            minimumSize(1024)
            condition {
                request.headers[HttpHeaders.Referrer]?.startsWith(baseUrl) == true
            }
        }
    }
}

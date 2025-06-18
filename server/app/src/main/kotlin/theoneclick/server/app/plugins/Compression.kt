package theoneclick.server.app.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*
import theoneclick.server.app.di.Environment

fun Application.configureCompression(environment: Environment) {
    install(Compression) {
        gzip {
            minimumSize(1024)
            condition {
                request.headers[HttpHeaders.Referrer]?.startsWith(environment.baseUrl) == true
            }
        }
    }
}
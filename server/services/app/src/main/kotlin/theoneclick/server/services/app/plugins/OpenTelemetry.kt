package theoneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.instrumentation.ktor.v3_0.KtorServerTelemetry
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk

internal fun Application.configureOpenTelemetry() {
    val openTelemetry = getOpenTelemetry()
    install(KtorServerTelemetry) {
        setOpenTelemetry(openTelemetry)
    }
}

private fun getOpenTelemetry(): OpenTelemetry =
    AutoConfiguredOpenTelemetrySdk.builder().addResourceCustomizer { oldResource, _ ->
        oldResource.toBuilder()
            .putAll(oldResource.attributes)
            .build()
    }.build().openTelemetrySdk
package theoneclick.server.core.endpoints.fulfillment

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.core.data.validators.ParamsValidator
import theoneclick.server.core.data.validators.ParamsValidator.FulfillmentRequestValidationResult.InvalidFulfillmentRequest
import theoneclick.server.core.data.validators.ParamsValidator.FulfillmentRequestValidationResult.ValidFulfillmentRequest
import theoneclick.server.core.data.validators.ParamsValidator.FulfillmentRequestValidationResult.ValidFulfillmentRequest.Sync
import theoneclick.server.core.endpoints.fulfillment.FulfillmentResponse.Payload
import theoneclick.server.core.extensions.bearerAuthentication
import theoneclick.server.core.extensions.post
import theoneclick.server.core.plugins.koin.inject
import theoneclick.server.core.data.models.endpoints.ServerEndpoints

fun Routing.fulfillmentEndpoint() {
    val paramsValidator: ParamsValidator by inject()

    bearerAuthentication {
        post(
            endpoint = ServerEndpoints.FULFILLMENT,
            requestValidation = paramsValidator::isFulfillmentRequestValid,
        ) { fulfillmentRequestValidationResult ->
            when (fulfillmentRequestValidationResult) {
                is ValidFulfillmentRequest -> handleValidFulfillmentRequest(fulfillmentRequestValidationResult)
                is InvalidFulfillmentRequest -> call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}

private suspend fun RoutingContext.handleValidFulfillmentRequest(validFulfillmentRequest: ValidFulfillmentRequest) {
    when (validFulfillmentRequest) {
        is Sync -> handleSync(validFulfillmentRequest)
    }
}

private suspend fun RoutingContext.handleSync(sync: Sync) {
    call.respond(
        FulfillmentResponse(
            requestId = sync.requestId,
            payload = Payload(
                agentUserId = sync.userData.userId.value,
                genericDevices = sync.userData.devices.map { device -> device.toDevice() },
            )
        )
    )
}

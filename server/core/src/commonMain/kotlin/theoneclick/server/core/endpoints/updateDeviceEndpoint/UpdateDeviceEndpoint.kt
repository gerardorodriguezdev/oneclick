package theoneclick.server.core.endpoints.updateDeviceEndpoint

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.extensions.defaultAuthentication
import theoneclick.server.core.extensions.post
import theoneclick.server.core.plugins.koin.inject
import theoneclick.server.core.validators.ParamsValidator
import theoneclick.server.core.validators.ParamsValidator.UpdateDeviceValidationResult.InvalidDevice
import theoneclick.server.core.validators.ParamsValidator.UpdateDeviceValidationResult.ValidDevice
import theoneclick.shared.core.models.endpoints.ClientEndpoint

fun Routing.updateDeviceEndpoint() {
    val paramsValidator: ParamsValidator by inject()
    val userDataSource: UserDataSource by inject()

    defaultAuthentication {
        post(
            endpoint = ClientEndpoint.UPDATE_DEVICE,
            requestValidation = paramsValidator::isUpdateDeviceRequestValid,
        ) { updateDeviceValidationResult ->
            when (updateDeviceValidationResult) {
                is ValidDevice -> handleValidDevice(
                    validDevice = updateDeviceValidationResult,
                    userDataSource = userDataSource,
                )

                is InvalidDevice -> call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}

private suspend fun RoutingContext.handleValidDevice(
    validDevice: ValidDevice,
    userDataSource: UserDataSource,
) {
    val currentUser = validDevice.user
    val updatedDevice = validDevice.updatedDevice

    userDataSource.saveUser(
        currentUser.copy(
            devices = currentUser.devices.mapIndexed { _, device ->
                if (device.id == updatedDevice.id) {
                    updatedDevice
                } else {
                    device
                }
            }
        )
    )

    call.respond(HttpStatusCode.OK)
}

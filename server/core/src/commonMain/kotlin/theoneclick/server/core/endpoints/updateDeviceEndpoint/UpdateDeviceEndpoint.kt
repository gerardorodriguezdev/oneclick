package theoneclick.server.core.endpoints.updateDeviceEndpoint

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.core.data.validators.ParamsValidator
import theoneclick.server.core.data.validators.ParamsValidator.UpdateDeviceValidationResult.InvalidDevice
import theoneclick.server.core.data.validators.ParamsValidator.UpdateDeviceValidationResult.ValidDevice
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.extensions.post
import theoneclick.server.core.extensions.userSessionAuthentication
import theoneclick.server.core.plugins.koin.inject
import theoneclick.server.core.data.models.endpoints.ServerEndpoints
import theoneclick.shared.core.models.endpoints.ClientEndpoints

fun Routing.updateDeviceEndpoint() {
    val paramsValidator: ParamsValidator by inject()
    val userDataSource: UserDataSource by inject()

    userSessionAuthentication {
        post(
            endpoint = ClientEndpoints.UPDATE_DEVICE,
            requestValidation = paramsValidator::isUpdateDeviceRequestValid,
        ) { updateDeviceValidationResult ->
            when (updateDeviceValidationResult) {
                is ValidDevice -> handleValidDevice(
                    updateDeviceValidationResult,
                    userDataSource,
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
    val currentUserData = validDevice.userData
    val updatedDevice = validDevice.updatedDevice

    userDataSource.saveUserData(
        currentUserData.copy(
            devices = currentUserData.devices.mapIndexed { _, device ->
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

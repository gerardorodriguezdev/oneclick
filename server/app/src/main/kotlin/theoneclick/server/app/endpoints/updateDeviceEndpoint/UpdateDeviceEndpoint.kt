package theoneclick.server.app.endpoints.updateDeviceEndpoint

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.extensions.defaultAuthentication
import theoneclick.server.app.models.UserSession
import org.koin.ktor.ext.inject
import theoneclick.server.app.validators.ParamsValidator
import theoneclick.server.app.validators.ParamsValidator.UpdateDeviceValidationResult.InvalidDevice
import theoneclick.server.app.validators.ParamsValidator.UpdateDeviceValidationResult.ValidDevice
import theoneclick.shared.core.models.endpoints.ClientEndpoint
import theoneclick.shared.core.models.requests.UpdateDeviceRequest

fun Routing.updateDeviceEndpoint() {
    val paramsValidator: ParamsValidator by inject()
    val usersDataSource: UsersDataSource by inject()

    defaultAuthentication {
        post(ClientEndpoint.UPDATE_DEVICE.route) { updateDeviceRequest: UpdateDeviceRequest ->
            val userSession = call.principal<UserSession>()!!
            val updateDeviceValidationResult = paramsValidator.isUpdateDeviceRequestValid(
                sessionToken = userSession.sessionToken,
                updateDeviceRequest = updateDeviceRequest
            )
            when (updateDeviceValidationResult) {
                is ValidDevice -> handleValidDevice(
                    validDevice = updateDeviceValidationResult,
                    usersDataSource = usersDataSource,
                )

                is InvalidDevice -> call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}

private suspend fun RoutingContext.handleValidDevice(
    validDevice: ValidDevice,
    usersDataSource: UsersDataSource,
) {
    val currentUser = validDevice.user
    val updatedDevice = validDevice.updatedDevice

    usersDataSource.saveUser(
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

package theoneclick.server.core.endpoints.addDevice

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.extensions.defaultAuthentication
import theoneclick.server.core.extensions.post
import theoneclick.server.core.platform.UuidProvider
import theoneclick.server.core.plugins.koin.inject
import theoneclick.server.core.validators.ParamsValidator
import theoneclick.server.core.validators.ParamsValidator.AddDeviceRequestValidationResult.InvalidDevice
import theoneclick.server.core.validators.ParamsValidator.AddDeviceRequestValidationResult.ValidDevice
import theoneclick.shared.core.models.endpoints.ClientEndpoint
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.DeviceType
import theoneclick.shared.core.models.entities.Uuid
import theoneclick.shared.core.models.responses.AddDeviceResponse

fun Routing.addDeviceEndpoint() {
    val paramsValidator: ParamsValidator by inject()
    val userDataSource: UserDataSource by inject()
    val uuidProvider: UuidProvider by inject()

    defaultAuthentication {
        post(
            endpoint = ClientEndpoint.ADD_DEVICE,
            requestValidation = paramsValidator::isAddDeviceRequestValid,
        ) { addDeviceValidationResult ->
            when (addDeviceValidationResult) {
                is ValidDevice -> handleValidDevice(
                    validDevice = addDeviceValidationResult,
                    userDataSource = userDataSource,
                    uuidProvider = uuidProvider,
                )

                is InvalidDevice -> call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}

private suspend fun RoutingContext.handleValidDevice(
    validDevice: ValidDevice,
    userDataSource: UserDataSource,
    uuidProvider: UuidProvider,
) {
    val currentUser = validDevice.user
    val newDevice = device(
        uuid = uuidProvider.uuid(),
        deviceName = validDevice.deviceName,
        room = validDevice.room,
        type = validDevice.deviceType,
    )

    userDataSource.saveUser(
        currentUser.copy(devices = currentUser.devices + newDevice)
    )

    call.respond(AddDeviceResponse(newDevice))
}

private fun device(uuid: Uuid, deviceName: String, room: String, type: DeviceType): Device =
    when (type) {
        DeviceType.BLIND -> Device.Blind(
            id = uuid,
            deviceName = deviceName,
            room = room,
            isOpened = false,
            rotation = 0,
        )
    }

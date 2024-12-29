package theoneclick.server.core.endpoints.addDevice

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.core.data.validators.ParamsValidator
import theoneclick.server.core.data.validators.ParamsValidator.AddDeviceRequestValidationResult.InvalidDevice
import theoneclick.server.core.data.validators.ParamsValidator.AddDeviceRequestValidationResult.ValidDevice
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.extensions.post
import theoneclick.server.core.extensions.userSessionAuthentication
import theoneclick.server.core.platform.UuidProvider
import theoneclick.server.core.plugins.koin.inject
import theoneclick.shared.core.dataSources.models.endpoints.Endpoint
import theoneclick.shared.core.dataSources.models.entities.Device
import theoneclick.shared.core.dataSources.models.entities.DeviceType
import theoneclick.shared.core.dataSources.models.entities.Uuid

fun Routing.addDeviceEndpoint() {
    val paramsValidator: ParamsValidator by inject()
    val userDataSource: UserDataSource by inject()
    val uuidProvider: UuidProvider by inject()

    userSessionAuthentication {
        post(
            endpoint = Endpoint.ADD_DEVICE,
            requestValidation = paramsValidator::isAddDeviceRequestValid,
        ) { addDeviceValidationResult ->
            when (addDeviceValidationResult) {
                is ValidDevice -> handleValidDevice(
                    addDeviceValidationResult,
                    userDataSource,
                    uuidProvider,
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
    val currentUserData = validDevice.userData
    val newDevice = device(
        uuid = uuidProvider.uuid(),
        deviceName = validDevice.deviceName,
        room = validDevice.room,
        type = validDevice.deviceType,
    )

    userDataSource.saveUserData(
        currentUserData.copy(devices = currentUserData.devices + newDevice)
    )

    call.respond(HttpStatusCode.OK)
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

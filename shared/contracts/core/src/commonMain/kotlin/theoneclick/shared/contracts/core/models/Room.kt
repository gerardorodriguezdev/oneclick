package theoneclick.shared.contracts.core.models

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.containsDuplicatesBy

@Serializable
class Room private constructor(
    val name: RoomName,
    val devices: List<Device>,
) {
    init {
        require(isValid(devices)) { ERROR_MESSAGE }
    }

    companion object Companion {
        private const val ERROR_MESSAGE = "Duplicated device id"

        fun isValid(devices: List<Device>): Boolean =
            devices.containsDuplicatesBy { deviceDto -> deviceDto.id }

        fun roomDto(name: RoomName, devices: List<Device>): Room? =
            if (isValid(devices)) {
                Room(
                    name = name,
                    devices = devices
                )
            } else {
                null
            }

        fun unsafe(name: RoomName, devices: List<Device>): Room =
            Room(
                name = name,
                devices = devices,
            )
    }
}
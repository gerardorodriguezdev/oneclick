package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.containsDuplicatesBy

@Serializable
class RoomDto private constructor(
    val name: RoomNameDto,
    val devices: List<DeviceDto>,
) {
    init {
        require(isValid(devices)) { ERROR_MESSAGE }
    }

    companion object {
        private const val ERROR_MESSAGE = "Duplicated device id"

        fun isValid(devices: List<DeviceDto>): Boolean =
            devices.containsDuplicatesBy { deviceDto -> deviceDto.id }

        fun roomDto(name: RoomNameDto, devices: List<DeviceDto>): RoomDto? =
            if (isValid(devices)) {
                RoomDto(
                    name = name,
                    devices = devices
                )
            } else {
                null
            }

        fun unsafe(name: RoomNameDto, devices: List<DeviceDto>): RoomDto =
            RoomDto(
                name = name,
                devices = devices,
            )
    }
}
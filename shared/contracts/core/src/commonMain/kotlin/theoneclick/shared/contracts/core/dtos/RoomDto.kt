package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.containsDuplicatesBy

@Serializable
data class RoomDto(
    val name: RoomNameDto,
    val devicesDtos: List<DeviceDto>,
) {
    init {
        require(isValid(devicesDtos)) { ERROR_MESSAGE }
    }

    companion object {
        private const val ERROR_MESSAGE = "Duplicated device id"

        fun isValid(devicesDtos: List<DeviceDto>): Boolean =
            devicesDtos.containsDuplicatesBy { deviceDto -> deviceDto.id }

        fun roomDto(nameDto: RoomNameDto, devicesDtos: List<DeviceDto>): RoomDto? =
            if (isValid(devicesDtos)) RoomDto(nameDto, devicesDtos) else null
    }
}